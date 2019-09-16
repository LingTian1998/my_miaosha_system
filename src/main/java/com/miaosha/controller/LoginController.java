package com.imooc.miaosha.controller;

import com.imooc.miaosha.dao.MiaoshaUserDAO;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.util.ValidateUtil;
import com.imooc.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RequestMapping("/login")
@Controller
public class LoginController {

  private static Logger log = LoggerFactory.getLogger(LoginController.class);

  @RequestMapping("/to_login")
  public String toLogin() {
    return "login";
  }

  @Autowired
  MiaoshaUserService miaoshaUserService;

  @RequestMapping("/do_login")
  @ResponseBody
  public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {

    log.info(loginVo.toString());

    String msg = miaoshaUserService.login(response, loginVo);

    return Result.success(msg);

  }


}
