package com.imooc.miaosha.myredis;

import com.imooc.miaosha.domain.MiaoshaUser;

public class MiaoshaUserKey extends BasePrefix {

  private static final int EXPIRE_TIME = 3600 * 24 * 2;

  public static MiaoshaUserKey token = new MiaoshaUserKey(EXPIRE_TIME, "tk");
  public static MiaoshaUserKey getById = new MiaoshaUserKey(EXPIRE_TIME, "tk");

  public MiaoshaUserKey(int expireSeconds, String prefix) {
    super(expireSeconds, prefix);
  }

  public MiaoshaUserKey(String str) {
    super(str);
  }

}
