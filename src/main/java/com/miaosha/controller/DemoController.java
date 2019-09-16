package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.myredis.RedisService;
import com.imooc.miaosha.myredis.UserKey;
import com.imooc.miaosha.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;

@Controller
@RequestMapping("/demo")
public class DemoController {

  @Autowired
  MQSender mqSender;

    /*
    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
        mqSender.send("Hello World!");
        return Result.success("Hello World!");
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> mqTopic() {
        mqSender.sendTopicMsg("Hello World!");
        return Result.success("Hello World!");
    }

    //swagger
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> mqFanout() {
        mqSender.sendFanoutMsg("Hello World!");
        return Result.success("Hello World!");
    }

    //swagger
    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> mqHeader() {
        mqSender.sendHeaderMsg("Hello World!");
        return Result.success("Hello World!");
    }
     */

  @RequestMapping("/")
  @ResponseBody
  String home() {
    return "Hello World!";
  }

  //1.rest api json输出 2.页面
  @RequestMapping("/hello")
  @ResponseBody
  public Result<String> hello() {
    return Result.success("hello,imooc");
    // return new Result(0, "success", "hello,imooc");
  }

  @RequestMapping("/helloError")
  @ResponseBody
  public Result<String> helloError() {
    return Result.error(CodeMsg.SERVER_ERROR);
    //return new Result(500102, "XXX");
  }

  @RequestMapping("/thymeleaf")
  public String thymeleaf(Model model) {
    model.addAttribute("name", "Joshua");
    return "hello";
  }

  @Autowired
  RedisService redisService;

  @RequestMapping("/redis/get")
  @ResponseBody
  public Result<String> redisGet() {
    String v1 = redisService.get(UserKey.getById, "1", String.class);
    return Result.success(v1);
  }

  @RequestMapping("/redis/set")
  @ResponseBody
  public Result<User> redisSet() {
    User user = new User();
    user.setId(1);
    user.setName("11111");
    redisService.set(UserKey.getById, "1", user);
    return Result.success(user);
  }

  @RequestMapping(value = "/{name}")
  @ResponseBody
  public String testController(@PathVariable String name) {
    return name;
  }


}
