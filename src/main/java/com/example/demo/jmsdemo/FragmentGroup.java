package com.example.demo.jmsdemo;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

/**
 * 用于管理分片消息组的类，支持分片消息的添加、组装和过期检查
 * @author Administrator
 */
public class FragmentGroup {
    private final String groupId;
    private final int totalFragments;
    private final TreeMap<Integer, String> fragments = new TreeMap<>();
    private final AtomicInteger receivedCount = new AtomicInteger(0);
    private final StampedLock lock = new StampedLock();

    private volatile long lastAccessTime = System.currentTimeMillis();
    /**
     * 构造函数，初始化分片组
     * @param groupId 分片组的唯一标识
     * @param totalFragments 该组预期的总分片数量
     */
    public FragmentGroup(String groupId, int totalFragments) {
        this.groupId = groupId;
        this.totalFragments = totalFragments;
    }

    /**
     * 添加一个分片到组中
     * @param index 分片索引
     * @param payload 分片内容
     * @return 如果所有分片都已接收返回true，否则返回false
     */
    public boolean addFragment(int index, String payload) {
        long stamp = lock.writeLock();
        try {
            // 检查是否已存在相同索引的分片
            if (fragments.containsKey(index)) {
                return false;
            }
            fragments.put(index, payload);
            return receivedCount.incrementAndGet() == totalFragments;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * 组装所有已接收的分片内容
     * @return 组装后的完整内容字符串
     */
    public String assemble() {
        long stamp = lock.readLock();
        try {
            // 按索引顺序拼接所有分片内容
            StringBuilder builder = new StringBuilder();
            fragments.values().forEach(builder::append);
            return builder.toString();
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * 检查分片组是否已过期
     * @param timeoutMs 超时时间(毫秒)
     * @return 如果从第一个分片接收时间到现在超过超时时间则返回true
     */
    public boolean isExpired(long timeoutMs) {
        // 使用乐观读尝试获取第一个分片的时间
        long stamp = lock.tryOptimisticRead();
        long now = System.currentTimeMillis();
        long firstFragmentTime = fragments.isEmpty() ? now :
                fragments.firstEntry().getValue().hashCode(); // 简化示例

        // 如果乐观读失败，转为悲观读
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                firstFragmentTime = fragments.isEmpty() ? now :
                        fragments.firstEntry().getValue().hashCode();
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return (now - firstFragmentTime) > timeoutMs;
    }

    // 提供 getter 方法
    public long getLastAccessTime() {
        // 使用读锁确保并发安全
        long stamp = lock.tryOptimisticRead();
        long result = lastAccessTime;

        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = lastAccessTime;
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return result;
    }
}
