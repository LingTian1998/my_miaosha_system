package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.dao.MiaoshaUserDAO;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {
  public static final String MIAOSHA_QUEUE = "miaoshaqueue";

  public static final String QUEUE = "queue";

  public static final String TOPIC_QUEUE1 = "topic.queue1";
  public static final String TOPIC_QUEUE2 = "topic.queue2";

  public static final String HEADER_QUEUE = "header.queue";


  public static final String TOPIC_EXCHANGE = "TopicExchange";
  public static final String FANOUT_EXCHANGE = "FanoutExchange";
  public static final String HEADERS_EXCHANGE = "HeadersExchange";


  @Bean
  public Queue miaoshaQueue() {
    return new Queue(MIAOSHA_QUEUE, true);
  }

  /**
   * Direct模式 交换机Exchange
   **/

  @Bean
  public Queue queue() {
    return new Queue(QUEUE, true);
  }

  /**
   * Topic模式 交换机Exchange
   **/

  @Bean
  public Queue topicQueue1() {
    return new Queue(TOPIC_QUEUE1, true);
  }

  @Bean
  public Queue topicQueue2() {
    return new Queue(TOPIC_QUEUE2, true);
  }

  @Bean
  public TopicExchange topicExchange() {
    return new TopicExchange(TOPIC_EXCHANGE);
  }

  public static final String ROUTING_KEY1 = "topic.key1";

  @Bean
  public Binding topicBinding1() {
    return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
  }

  public static final String ROUTING_KEY2 = "topic.#";

  @Bean
  public Binding topicBinding2() {
    return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
  }


  /**
   * Fanout模式 交换机Exchange
   **/
  @Bean
  public FanoutExchange fanoutExchange() {
    return new FanoutExchange(FANOUT_EXCHANGE);
  }

  @Bean
  public Binding fanoutBingding1() {
    return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
  }

  @Bean
  public Binding fanoutBingding2() {
    return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
  }

  /**
   * Header模式 交换机Exchange
   **/
  @Bean
  public HeadersExchange headersExchange() {
    return new HeadersExchange(HEADERS_EXCHANGE);
  }

  @Bean
  public Queue headerQueue() {
    return new Queue(HEADER_QUEUE, true);
  }

  @Bean
  public Binding HeaderBinding() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("header1", "value 1");
    map.put("header2", "value 2");
    return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();
  }


}
