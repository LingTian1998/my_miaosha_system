package com.imooc.miaosha.service;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.myredis.MiaoshaKey;
import com.imooc.miaosha.myredis.RedisService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class MiaoshaService {

  @Autowired
  GoodsService goodsService;

  @Autowired
  OrderService orderService;

  @Autowired
  RedisService redisService;

  @Transactional
  public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {
    OrderInfo orderInfo;
    if (goodsService.reduceStock(goodsVo))//如果还有库存
      return orderService.createOrder(user, goodsVo);
    else {
      //商品秒杀完进行标记
      setGoodsOver(goodsVo.getId());
      return null;
    }

  }


  public long getMiaoshaResult(Long userId, long goodsId) {
    MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
    if (order != null) {
      return order.getOrderId();
    } else {
      //商品卖完返回-1，没有则返回排队中0
      return (getGoodsOver(goodsId)) ? -1 : 0;
    }
  }

  public void reset(List<GoodsVo> goodsList) {
    goodsService.resetStock(goodsList);
    orderService.deleteOrders();
  }


  private void setGoodsOver(Long goodsId) {
    redisService.set(MiaoshaKey.isGoodsOver, "" + goodsId, true);
  }

  private boolean getGoodsOver(long goodsId) {
    return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
  }

  public boolean checkPath(String path, MiaoshaUser user, long goodsId) {
    if (user == null || path == null)
      return false;
    return (path.equals(redisService.get(MiaoshaKey.getMiaoshaPath, "" + user.getId() + goodsId, String.class)));
  }

  public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
    if (user == null || goodsId <= 0)
      return null;
    String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
    redisService.set(MiaoshaKey.getMiaoshaPath, "" + user.getId() + goodsId, str);
    return str;
  }

  public BufferedImage createMiaoshaVerifyCode(MiaoshaUser user, long goodsId) {
    if (user == null || goodsId <= 0)
      return null;
    int width = 80;
    int height = 32;
    //create the image
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();
    // set the background color
    g.setColor(new Color(0xDCDCDC));
    g.fillRect(0, 0, width, height);
    // draw the border
    g.setColor(Color.black);
    g.drawRect(0, 0, width - 1, height - 1);
    // create a random instance to generate the codes
    Random rdm = new Random();
    // make some confusion
    for (int i = 0; i < 50; i++) {
      int x = rdm.nextInt(width);
      int y = rdm.nextInt(height);
      g.drawOval(x, y, 0, 0);
    }
    // generate a random code
    String verifyCode = generateVerifyCode(rdm);
    g.setColor(new Color(0, 100, 0));
    g.setFont(new Font("Candara", Font.BOLD, 24));
    g.drawString(verifyCode, 8, 24);
    g.dispose();
    //把验证码存到redis中
    int rnd = calc(verifyCode);
    redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, rnd);
    //输出图片
    return image;
  }


  public boolean checkVerifyCode(int verifyCode, MiaoshaUser user, long goodsId) {
    if (user == null || goodsId <= 0)
      return false;
    Integer old = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, Integer.class);
    if (old == null || old != verifyCode) {
      return false;
    }
    redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId);
    return true;
  }


  private int calc(String exp) {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("JavaScript");
      return (Integer) engine.eval(exp);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }


  private static final char[] ops = {'+', '-', '*'};

  /**
   * 生成+ - *的运算
   */
  private String generateVerifyCode(Random rdm) {
    int a = rdm.nextInt(10);
    int b = rdm.nextInt(10);
    int c = rdm.nextInt(10);
    char op1 = ops[rdm.nextInt(3)];
    char op2 = ops[rdm.nextInt(3)];
    String exp = "" + a + op1 + b + op2 + c;
    return exp;
  }

}
