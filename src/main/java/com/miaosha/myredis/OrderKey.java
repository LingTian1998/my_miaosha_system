package com.imooc.miaosha.myredis;

public class OrderKey extends BasePrefix {

  private static final int expireTime = 3600 * 24;

  public OrderKey(String prefix) {
    super(expireTime, prefix);
  }

  public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("OrderByUidGid");
}
