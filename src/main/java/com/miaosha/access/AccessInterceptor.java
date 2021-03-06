package com.imooc.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.myredis.AccessKey;
import com.imooc.miaosha.myredis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
@Slf4j
public class AccessInterceptor extends HandlerInterceptorAdapter {

  @Autowired
  MiaoshaUserService miaoshaUserService;

  @Autowired
  RedisService redisService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    //log.info("enter interceptor");
    if (handler instanceof HandlerMethod) {

      //log.info("enter handler");

      MiaoshaUser user = getUser(request, response);

      UserContext.setUser(user);

      HandlerMethod hm = (HandlerMethod) handler;
      AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
      if (accessLimit == null) {
        return true;
      }

      int seconds = accessLimit.seconds();
      int maxCount = accessLimit.maxCount();
      boolean needLogin = accessLimit.needLogin();

      String key = request.getRequestURI();

      if (needLogin) {
        if (user == null) {
          render(response, CodeMsg.SESSION_ERROR);
          return false;
        }
        key += "_" + user.getId();
      } else {
        //do nothing
      }

      //查询访问次数,5秒钟访问5次

      AccessKey ak = AccessKey.withExpire(seconds);

      Integer count = redisService.get(ak, key, Integer.class);
      if (count == null) {
        redisService.set(ak, key, 1);
      } else if (count < maxCount) {
        redisService.incr(ak, key);
      } else {
        render(response, CodeMsg.ACCESS_REACH_LIMIT);
        return false;
      }
    }
    return true;
  }

  private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception {
    response.setContentType("application/json;charset=UTF-8");
    OutputStream out = response.getOutputStream();
    String str = JSON.toJSONString(Result.error(codeMsg));
    out.write(str.getBytes("UTF-8"));
    out.flush();
    out.close();

  }

  private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
    String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
    String cookieToken = getCookieValue(request, MiaoshaUserService.COOKIE_NAME_TOKEN);

    if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
      return null;
    }
    // cookie first
    String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
    return miaoshaUserService.getByToken(response, token);
  }

  private String getCookieValue(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null)
      return null;
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(cookieName))
        return cookie.getValue();
    }
    return null;
  }
}
