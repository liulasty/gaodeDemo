//package com.example.demo.jmsdemo;
//
//import com.example.demo.mapper.MessageFragmentMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Random;
//import java.util.UUID;
//
///**
// * @author Administrator
// */
//@Component
//@Slf4j
//public class DeviceMessageProducer {
//    private final JmsTemplate jmsTemplate;
//    private final MessageFragmentMapper messageFragmentMapper;
//    private final Random random = new Random();
//
//    public DeviceMessageProducer(JmsTemplate jmsTemplate,
//                                 MessageFragmentMapper messageFragmentMapper) {
//        this.messageFragmentMapper = messageFragmentMapper;
//        this.jmsTemplate = jmsTemplate;
//    }
//
//    @Scheduled(fixedRate = 1000)
//    public void sendFragmentedMessages() {
//        String deviceId = "device-" + random.nextInt(5);
//        String messageId = UUID.randomUUID().toString();
//        int totalFragments = 3 + random.nextInt(3);
//
//        for (int i = 0; i < totalFragments; i++) {
//            MessageFragment fragment = new MessageFragment();
//            fragment.setMessageId(messageId);
//            fragment.setDeviceId(deviceId);
//            fragment.setDataType("DT" + totalFragments);
//            fragment.setTotalFragments(totalFragments);
//            fragment.setFragmentIndex(i + 1);
//            fragment.setPayload("Fragment-" + (i + 1) + "-of-" + totalFragments + "-");
//            log.info("发送分片: " + fragment.getFragmentIndex());
//            jmsTemplate.convertAndSend("device.queue", fragment);
//
//            saveSendRecord( fragment);
//        }
//    }
//
//    @Scheduled(fixedRate = 2000)
//    public void sendFragmentedMessages1() {
//        String deviceId = "device-" + random.nextInt(5);
//        String messageId = UUID.randomUUID().toString();
//        int totalFragments = 3 + random.nextInt(3);
//
//        for (int i = 0; i < totalFragments; i++) {
//            MessageFragment fragment = new MessageFragment();
//            fragment.setMessageId(messageId);
//            fragment.setDeviceId(deviceId);
//            fragment.setDataType("DT" + totalFragments);
//            fragment.setTotalFragments(totalFragments);
//            fragment.setFragmentIndex(i + 1);
//            fragment.setPayload("Fragment-" + (i + 1) + "-of-" + totalFragments + "-");
//            log.info("发送分片1: " + fragment.getFragmentIndex());
//            jmsTemplate.convertAndSend("device.queue1", fragment);
//
//            saveSendRecord( fragment);
//        }
//    }
//
//
//    public void saveSendRecord(MessageFragment  fragment) {
//        messageFragmentMapper.insert(fragment);
//    }
//}