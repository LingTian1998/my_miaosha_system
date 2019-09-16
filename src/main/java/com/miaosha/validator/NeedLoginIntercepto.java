package com.imooc.miaosha.validator;

import com.alibaba.fastjson.JSON;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.service.MiaoshaUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
public class NeedLoginIntercepto extends HandlerInterceptorAdapter {

  @Autowired
  MiaoshaUserService miaoshaUserService;

  //在请求处理之前进行调用（Controller方法调用之前
  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
    //如果不是映射到方法直接通过
    if (!(o instanceof HandlerMethod)) {
      return true;
    }
    HandlerMethod handlerMethod = (HandlerMethod) o;
    Method method = handlerMethod.getMethod();
    if (method.getAnnotation(NeedLogin.class) != null) {
      NeedLogin needLoginAnnotation = method.getAnnotation(NeedLogin.class);
      boolean required = needLoginAnnotation.required();

      //检查cookie中的token或者param里面的token，为空即为用户为空
      String paramToken = httpServletRequest.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
      String cookieToken = getCookieValue(httpServletRequest, MiaoshaUserService.COOKIE_NAME_TOKEN);

      if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
        httpServletResponse.getWriter().write(JSON.toJSONString(CodeMsg.SESSION_ERROR));
        return false;
      }

      String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;

      MiaoshaUser user = miaoshaUserService.getByToken(httpServletResponse, token);

      if (user == null) {
        httpServletResponse.getWriter().write(JSON.toJSONString(CodeMsg.SESSION_ERROR));
        return false;
      } else {
        return true;
      }
    } else {
      return true;
    }
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

  //请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
  @Override
  public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

  }

  //在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
  @Override
  public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

  }
}
