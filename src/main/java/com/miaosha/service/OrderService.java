package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.OrderDAO;
import com.imooc.miaosha.domain.*;
import com.imooc.miaosha.myredis.OrderKey;
import com.imooc.miaosha.myredis.RedisService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
  @Autowired
  OrderDAO orderDAO;

  @Autowired
  RedisService redisService;

  public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {

    //return orderDAO.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);

    return redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userId + "_" + goodsId, MiaoshaOrder.class);
  }

  public OrderInfo getOrderById(long orderId) {
    return orderDAO.getOrderById(orderId);
  }


  @Transactional(rollbackFor = Exception.class)
  public OrderInfo createOrder(MiaoshaUser user, GoodsVo goodsVo) {
    OrderInfo orderInfo = new OrderInfo();
    orderInfo.setCreateDate(new Date());
    orderInfo.setDeliveryAddrId(0L);
    orderInfo.setGoodsCount(1);
    orderInfo.setGoodsId(goodsVo.getId());
    orderInfo.setGoodsName(goodsVo.getGoodsName());
    orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
    orderInfo.setOrderChannel(1);
    orderInfo.setStatus(ORDER_STATUS.UNPAY.ordinal());
    orderInfo.setUserId(user.getId());
    orderDAO.insertOrderInfo(orderInfo);
    MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
    miaoshaOrder.setGoodsId(goodsVo.getId());
    miaoshaOrder.setOrderId(orderInfo.getId());
    miaoshaOrder.setUserId(user.getId());

    orderDAO.insertMiaoshaOrder(miaoshaOrder);
    redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + miaoshaOrder.getUserId() + "_" + miaoshaOrder.getGoodsId(), miaoshaOrder);

    return orderInfo;
  }

  public void deleteOrders() {
    orderDAO.deleteOrders();
    orderDAO.deleteMiaoshaOrders();
  }

}
