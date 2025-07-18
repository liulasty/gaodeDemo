package com.example.demo.jmsdemo;

import com.example.demo.mapper.DeviceDataRecordMapper;
import com.example.demo.mapper.TimeoutRecordMapper;
import com.nimbusds.oauth2.sdk.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消息处理器，负责处理接收到的消息片段并将其组装成完整消息
 * @author Administrator
 */
@Component
public class MessageProcessor {
    @Autowired
    private DeviceDataRecordMapper deviceDataRecordMapper;
    @Autowired
    private TimeoutRecordMapper timeoutRecordMapper;


    // 使用虚拟线程池处理消息片段
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * 消息监听方法，当接收到消息时自动调用
     *
     * @param fragment 接收到的消息片段
     */
    @JmsListener(destination = "device.queue")
    public void onMessage(MessageFragment fragment) {
        virtualThreadExecutor.execute(() -> processFragment(fragment));
    }

    @JmsListener(destination = "device.queue1")
    public void onMessage1(MessageFragment fragment) {
        virtualThreadExecutor.execute(() -> processFragment(fragment));
    }

    /**
     * 处理消息片段，尝试将其组装成完整消息
     *
     * @param fragment 消息片段对象，包含片段索引、设备ID、消息ID和负载数据
     */
    private void processFragment(MessageFragment fragment) {
        // 生成消息组ID
        String groupId = fragment.getDeviceId() + "-" + fragment.getMessageId();

        // 使用computeIfAbsent原子操作获取或创建分组
        FragmentGroup group = FragmentGroupManager.getOrCreateGroup(groupId, fragment.getTotalFragments());

        // 尝试将片段添加到分组中，如果成功则说明消息组已完整
        if (group.addFragment(fragment.getFragmentIndex(), fragment.getPayload())) {
            System.out.println("[" + fragment.getDeviceId() + "] 消息组完整: " + groupId);
            String assembled = group.assemble();
            System.out.println("重组结果: " + assembled);

            // 这里可以添加业务处理逻辑...
            DeviceDataRecord deviceDataRecord = new DeviceDataRecord();

            deviceDataRecord.setMessageId(fragment.getMessageId());
            deviceDataRecord.setDeviceId(fragment.getDeviceId());
            deviceDataRecord.setDataType(fragment.getDataType());
            deviceDataRecord.setAssembled(assembled);
            deviceDataRecord.setTimestamp(System.currentTimeMillis());
            deviceDataRecordMapper.insert(deviceDataRecord);
        } else {
            // 如果消息组不完整，则打印收到的片段信息
            System.out.println("[" + fragment.getDeviceId() + "] 收到分片: " +
                    fragment.getFragmentIndex() + "/" + fragment.getTotalFragments());
        }
    }

    public void saveDeviceDataRecord(DeviceDataRecord deviceDataRecord) {
        deviceDataRecordMapper.insert(deviceDataRecord);
    }

    /**
     * 定时清理超时分组
     * 每10秒执行一次，检查并移除超过15秒未完成组装的分组
     */
    /**
     * 定时清理超时分组
     * 每10秒执行一次，检查并移除超过15秒未完成组装的分组
     */
    @Scheduled(fixedRate = 20000)
    public void cleanupExpiredGroups() {
        virtualThreadExecutor.execute(() -> {
            FragmentGroupManager.getAllGroups().entrySet().removeIf(entry -> {
                boolean expired = entry.getValue().isExpired(15000); // 15秒超时
                if (expired) {
                    System.out.println("清理超时分组: " + entry.getKey());

                    // 解析 groupID 获取设备ID和消息ID
                    String[] parts = entry.getKey().split("-");
                    String deviceId = parts[0];
                    String messageId = parts.length > 1 ? parts[1] : "";

                    // 构建 TimeoutRecord 并保存到数据库
                    TimeoutRecord timeoutRecord = new TimeoutRecord();
                    timeoutRecord.setGroupId(entry.getKey());
                    timeoutRecord.setDeviceId(deviceId);
                    timeoutRecord.setMessageId(messageId);
                    timeoutRecord.setExpireTime(entry.getValue().getLastAccessTime() + 15000);
                    timeoutRecord.setCleanupTime(System.currentTimeMillis());


                    timeoutRecordMapper.insert(timeoutRecord);

                    FragmentGroupManager.removeGroup(entry.getKey());
                }
                return expired;
            });
        });
    }
}

