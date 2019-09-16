package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.validator.NeedLogin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/user")
@Controller
public class UserController {

  @RequestMapping("/info")
  @ResponseBody
  @NeedLogin
  public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
    return Result.success(user);
  }
}
