package com.imooc.miaosha.domain;

import java.util.Date;

public class MiaoshaGoods {
  private Long id;
  private Long goodsId;
  private Integer miaoshaCount;
  private Date startDate;
  private Date endDate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getGoodsId() {
    return goodsId;
  }

  public void setGoodsId(Long goodsId) {
    this.goodsId = goodsId;
  }

  public Integer getMiaoshaCount() {
    return miaoshaCount;
  }

  public void setMiaoshaCount(Integer miaoshaCount) {
    this.miaoshaCount = miaoshaCount;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
}
