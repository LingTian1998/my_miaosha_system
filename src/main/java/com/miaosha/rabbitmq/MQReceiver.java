package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.myredis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

  @Autowired
  GoodsService goodsService;

  @Autowired
  OrderService orderService;

  @Autowired
  MiaoshaService miaoshaService;

  @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
  public void receive(String message) {
    //log.info("receive msg : {}", message);
    MiaoshaMessage mmsg = RedisService.stringToBean(message, MiaoshaMessage.class);
    MiaoshaUser user = mmsg.getUser();
    long goodsId = mmsg.getGoodsId();
    //check miaoshaCount
    GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
    int miaoshaCount = goodsVo.getMiaoshaCount();
    if (miaoshaCount <= 0) {
      return;
    }
    //判断是否重复秒杀
    MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    if (order != null) {
      return;
    }

    //
    miaoshaService.miaosha(user, goodsVo);

  }


    /*
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive msg : {}", message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info("receive topic queue 1 msg : {}", message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info("receive topic queue 2 msg : {}", message);
    }

    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
    public void receiveHeader(byte[] message) {
        log.info("receive header queue msg : {}", new String(message));
    }

     */
}
