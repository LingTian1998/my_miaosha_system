package com.imooc.miaosha.controller;

import com.imooc.miaosha.access.AccessLimit;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.myredis.*;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.rabbitmq.MiaoshaMessage;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequestMapping("/miaosha")
@Controller
public class MiaoshaController implements InitializingBean {

  @Autowired
  GoodsService goodsService;

  @Autowired
  OrderService orderService;

  @Autowired
  MiaoshaService miaoshaService;

  @Autowired
  RedisService redisService;

  @Autowired
  MQSender mqSender;

  private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

  /**
   * QPS 41.2
   * 1000 * 10
   *
   * QPS 142
   *
   * QPS 350
   */

  /**
   * GET不对服务端数据产生影响，POST产生影响，应该这样写
   * 否则搜索引擎遍历GET链接会更改自己的数据
   **/

  @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
  @ResponseBody
  public Result<Integer> doMiaosha(Model model, MiaoshaUser user,
                                   @PathVariable("path") String path,
                                   @RequestParam("goodsId") long goodsId) {
    model.addAttribute("user", user);
    if (user == null) {
      return Result.error(CodeMsg.SESSION_ERROR);
    }
    //验证path
    boolean check = miaoshaService.checkPath(path, user, goodsId);
    if (!check) {
      return Result.error(CodeMsg.REQUEST_ILLEGAL);
    }

    //内存标记，减少redis访问
    boolean over = localOverMap.get(goodsId);
    if (over) {
      return Result.error(CodeMsg.OUT_OF_STOCK);
    }

    //redis预减库存
    long stock = redisService.decr(GoodsKey.getMiaoshaCount, "" + goodsId);
    if (stock < 0) {
      return Result.error(CodeMsg.OUT_OF_STOCK);
    }
    //判断是否已经秒杀到了
    MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    if (order != null) {
      return Result.error(CodeMsg.REPEAT_BUY);
    }

    //入队
    MiaoshaMessage mmsg = new MiaoshaMessage();
    mmsg.setGoodsId(goodsId);
    mmsg.setUser(user);
    mqSender.sendMiaoshaMessage(mmsg);
    return Result.success(0);//排队中
        /*
        //check miaoshaCount
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int miaoshaCount = goodsVo.getMiaoshaCount();
        if (miaoshaCount <= 0) {
            return Result.error(CodeMsg.OUT_OF_STOCK);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEAT_BUY);
        }
        //减库存，下订单，写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);

        if (orderInfo != null) {
            return Result.success(orderInfo);
        }else
            return Result.error(CodeMsg.OUT_OF_STOCK);

         */
  }

  /*
   * 系统初始化时将秒杀商品的库存加入到缓存中
   * */
  @Override
  public void afterPropertiesSet() throws Exception {
    List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
    if (goodsVoList == null) {
      return;
    }
    for (GoodsVo g : goodsVoList) {
      redisService.set(GoodsKey.getMiaoshaCount, "" + g.getId(), g.getMiaoshaCount());
      localOverMap.put(g.getId(), false);
    }
  }

  /**
   * return:
   * orderId success
   * -1       fail
   * 0        in queue
   **/
  @RequestMapping(value = "/result", method = RequestMethod.GET)
  @ResponseBody
  public Result<Long> miaoshaResult(Model model, MiaoshaUser user,
                                    @RequestParam("goodsId") long goodsId) {
    model.addAttribute("user", user);
    if (user == null) {
      return Result.error(CodeMsg.SESSION_ERROR);
    }

    long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
    return Result.success(result);
  }

  @RequestMapping(value = "/reset", method = RequestMethod.GET)
  @ResponseBody
  public Result<Boolean> rest(Model model) {
    List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
    for (GoodsVo goodsVo : goodsVoList) {
      goodsVo.setMiaoshaCount(10);
      redisService.set(GoodsKey.getMiaoshaCount, "" + goodsVo.getId(), 10);
      localOverMap.put(goodsVo.getId(), false);
    }
    redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
    redisService.delete(MiaoshaKey.isGoodsOver);
    miaoshaService.reset(goodsVoList);
    return Result.success(true);
  }

  @AccessLimit(seconds = 10, maxCount = 5, needLogin = true)
  @RequestMapping(value = "/path", method = RequestMethod.GET)
  @ResponseBody
  public Result<String> getMiaoshaPath(HttpServletRequest request,
                                       MiaoshaUser user,
                                       @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode,
                                       @RequestParam("goodsId") long goodsId) {
    log.info("enter path");
    if (user == null) {
      return Result.error(CodeMsg.SESSION_ERROR);
    }

    boolean check = miaoshaService.checkVerifyCode(verifyCode, user, goodsId);

    if (!check) {
      return Result.error(CodeMsg.REQUEST_ILLEGAL);
    }

    String path = miaoshaService.createMiaoshaPath(user, goodsId);
    return Result.success(path);
  }

  @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
  @ResponseBody
  public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, MiaoshaUser user,
                                             @RequestParam("goodsId") long goodsId) {
    if (user == null) {
      return Result.error(CodeMsg.SESSION_ERROR);
    }
    BufferedImage image = miaoshaService.createMiaoshaVerifyCode(user, goodsId);
    try {
      OutputStream out = response.getOutputStream();
      ImageIO.write(image, "JPEG", out);
      out.flush();
      out.close();
      return null;
    } catch (IOException e) {
      return Result.error(CodeMsg.MIAOSHA_FAIL);
    }
  }
}
