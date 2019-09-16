package com.imooc.miaosha.myredis;


public abstract class BasePrefix implements KeyPrefix {

  private String prefix;

  private int expireSeconds;

  public BasePrefix(int expireSeconds, String prefix) {
    this.prefix = prefix;
    this.expireSeconds = expireSeconds;
  }

  public BasePrefix(String prefix) {
    this.prefix = prefix;
    this.expireSeconds = 0;
  }

  @Override
  public int expireSeconds() {
    return expireSeconds;
  }

  @Override
  public String getPrefix() {
    String className = getClass().getSimpleName();
    return className + ":" + prefix;
  }
}
