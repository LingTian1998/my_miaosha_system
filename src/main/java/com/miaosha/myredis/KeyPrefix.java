package com.imooc.miaosha.myredis;

public interface KeyPrefix {

  public int expireSeconds();

  public String getPrefix();
}
