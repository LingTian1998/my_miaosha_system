package com.imooc.miaosha.vo;

import com.imooc.miaosha.domain.Goods;

import java.util.Date;

public class GoodsVo extends Goods {
  private Double miaoshaPrice;
  private Integer miaoshaCount;
  private Date startDate;
  private Date endDate;

  public Double getMiaoshaPrice() {
    return miaoshaPrice;
  }

  public void setMiaoshaPrice(Double miaoshaPrice) {
    this.miaoshaPrice = miaoshaPrice;
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
