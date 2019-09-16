package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.validator.NeedLogin;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.OrderDetailVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

  @Autowired
  OrderService orderService;

  @Autowired
  GoodsService goodsService;

  @RequestMapping("/detail")
  @ResponseBody
  public Result<OrderDetailVo> info(@RequestParam("orderId") long orderId, MiaoshaUser user) {
    if (user == null) {
      return Result.error(CodeMsg.SESSION_ERROR);
    }
    OrderInfo orderInfo = orderService.getOrderById(orderId);
    if (orderInfo == null) {
      return Result.error(CodeMsg.ORDER_NOT_EXIST);
    }
    long goodsId = orderInfo.getGoodsId();
    GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
    OrderDetailVo orderDetailVo = new OrderDetailVo();
    orderDetailVo.setOrderInfo(orderInfo);
    orderDetailVo.setGoodsVo(goodsVo);

    return Result.success(orderDetailVo);
  }
}
