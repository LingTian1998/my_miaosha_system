package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.myredis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {
  @Autowired
  AmqpTemplate amqpTemplate;

  public void sendMiaoshaMessage(Object message) {
    String msg = RedisService.beanToString(message);
    //log.info("send msg is : {}", msg);
    amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
  }


    /*
    public void send(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send msg is : {}", msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    public void sendTopicMsg(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send topic msg is : {}", msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
    }

    public void sendFanoutMsg(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send fanout msg is : {}", msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg + "1");

    }

    public void sendHeaderMsg(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send header msg is : {}", msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1", "value 1");
        properties.setHeader("header2", "value 2");
        Message obj = new Message(msg.getBytes(), properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);

    }

 */
}
