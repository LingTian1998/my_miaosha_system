package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.myredis.GoodsKey;
import com.imooc.miaosha.myredis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.GoodsDetailVo;
import com.imooc.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jcp.xml.dsig.internal.MacOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

  @Autowired
  MiaoshaUserService miaoshaUserService;

  @Autowired
  GoodsService goodsService;

  @Autowired
  RedisService redisService;

  @Autowired
  ThymeleafViewResolver thymeleafViewResolver;

  @Autowired
  ApplicationContext applicationContext;


  /**
   * QPS 304
   * 1000 * 10
   * <p>
   * 更改后
   * QPS 534
   */

  //直接返回Html源码
  @RequestMapping(value = "/list", produces = "text/html")
  @ResponseBody
  public String toGoodList(HttpServletRequest request,
                           HttpServletResponse response, Model model, MiaoshaUser user) {

    model.addAttribute("user", user);

    //取html缓存
    String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
    if (!StringUtils.isEmpty(html)) {
      //log.info("get html cache");
      return html;
    }

    List<GoodsVo> goodsVoList = goodsService.listGoodsVo();

    model.addAttribute("goodsList", goodsVoList);

    //return "goods_list";

    SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
    //手动渲染
    html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
    if (!StringUtils.isEmpty(html)) {
      redisService.set(GoodsKey.getGoodsList, "", html);
    }
    return html;
  }

  @RequestMapping(value = "/detail/{goodsId}")
  @ResponseBody
  public Result<GoodsDetailVo> toGoodDetail(HttpServletRequest request,
                                            HttpServletResponse response, Model model, MiaoshaUser user,
                                            @PathVariable("goodsId") long goodsId) {

    GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);


    long startAt = goodsVo.getStartDate().getTime();
    long endAt = goodsVo.getEndDate().getTime();
    long now = System.currentTimeMillis();

    int miaoshaStatus = 0;
    int remainSeconds = 0;

    if (now < startAt) {
      //秒杀没开始
      miaoshaStatus = 0;
      remainSeconds = (int) ((startAt - now) / 1000);
    } else if (now > endAt) {
      //秒杀结束
      miaoshaStatus = 2;
      remainSeconds = -1;
    } else {
      //秒杀进行中
      miaoshaStatus = 1;
      remainSeconds = 0;
    }

    GoodsDetailVo vo = new GoodsDetailVo();
    vo.setGoodsVo(goodsVo);
    vo.setMiaoshaStatus(miaoshaStatus);
    vo.setRemainSeconds(remainSeconds);
    vo.setUser(user);

    return Result.success(vo);
  }

  @RequestMapping(value = "/detail2/{goodsId}", produces = "text/html")
  @ResponseBody
  public String toGoodDetail2(HttpServletRequest request,
                              HttpServletResponse response, Model model, MiaoshaUser user,
                              @PathVariable("goodsId") long goodsId) {

    model.addAttribute("user", user);

    //取html缓存
    String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
    if (!StringUtils.isEmpty(html)) {
      return html;
    }
    //手动渲染
    //snowfleka

    GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

    model.addAttribute("goods", goodsVo);

    long startAt = goodsVo.getStartDate().getTime();
    long endAt = goodsVo.getEndDate().getTime();
    long now = System.currentTimeMillis();

    int miaoshaStatus = 0;
    int remainSeconds = 0;

    if (now < startAt) {
      //秒杀没开始
      miaoshaStatus = 0;
      remainSeconds = (int) ((startAt - now) / 1000);
    } else if (now > endAt) {
      //秒杀结束
      miaoshaStatus = 2;
      remainSeconds = -1;
    } else {
      //秒杀进行中
      miaoshaStatus = 1;
      remainSeconds = 0;
    }

    model.addAttribute("miaoshaStatus", miaoshaStatus);
    model.addAttribute("remainSeconds", remainSeconds);


    //return "goods_detail";


    SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);

    html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
    if (!StringUtils.isEmpty(html)) {
      redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
    }
    return html;
  }
}
