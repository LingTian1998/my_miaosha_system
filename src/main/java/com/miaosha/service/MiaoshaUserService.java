package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoshaUserDAO;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.myredis.MiaoshaUserKey;
import com.imooc.miaosha.myredis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
public class MiaoshaUserService {

  public static final String COOKIE_NAME_TOKEN = "token";

  @Autowired
  MiaoshaUserDAO miaoshaUserDAO;

  @Autowired
  RedisService redisService;

  public MiaoshaUser getById(long id) {
    //取缓存
    MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
    if (user != null) {
      return user;
    }
    //取数据库
    user = miaoshaUserDAO.getById(id);
    if (user != null) {
      redisService.set(MiaoshaUserKey.getById, "" + id, user);
    }
    return user;
  }

  public boolean updatePassword(long id, String password, String token) {
    //取user对象
    MiaoshaUser user = getById(id);
    if (user == null) {
      throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
    }
    //更新数据库
    MiaoshaUser toUpdate = new MiaoshaUser();
    toUpdate.setId(id);
    toUpdate.setPassword(MD5Util.formPassToDBPass(password, user.getSalt()));
    miaoshaUserDAO.update(toUpdate);
    //清理缓存
    redisService.delete(MiaoshaUserKey.getById);
    user.setPassword(toUpdate.getPassword());
    redisService.set(MiaoshaUserKey.token, token, user);
    return true;
  }

  public MiaoshaUser getByToken(HttpServletResponse response, String token) {
    if (StringUtils.isEmpty(token)) {
      return null;
    }

    MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
    //long cookie
    addCookie(response, token, user);

    return user;
  }

  public String login(HttpServletResponse response, LoginVo loginVo) {
    if (loginVo == null) {
      throw new GlobalException(CodeMsg.SERVER_ERROR);
    }
    String mobile = loginVo.getMobile();
    String formPass = loginVo.getPassword();

    MiaoshaUser user = getById(Long.parseLong(mobile));
    // verify user
    if (user == null) {
      throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
    }
    // verify password
    String dbPass = user.getPassword();
    String saltDB = user.getSalt();
    String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
    log.info("form pass is {}", formPass);
    log.info("calc pass is {}", calcPass);
    if (!calcPass.equals(dbPass)) {
      throw new GlobalException(CodeMsg.PASSWORD_ERROR);
    }

    String token = UUIDUtil.uuid();
    addCookie(response, token, user);

    return token;
  }

  private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
    // generate cookie
    redisService.set(MiaoshaUserKey.token, token, user);
    Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
    cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
    cookie.setPath("/");
    response.addCookie(cookie);
  }
}
