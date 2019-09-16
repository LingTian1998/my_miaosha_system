package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.GoodsDAO;
import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class GoodsService {

  @Autowired
  GoodsDAO goodsDAO;

  public List<GoodsVo> listGoodsVo() {
    return goodsDAO.getGoodsVoList();
  }

  public GoodsVo getGoodsVoByGoodsId(long goodsId) {
    return goodsDAO.getGoodsVoByGoodsId(goodsId);
  }

  //返回1表示减少库存成功，返回0表示失败（没有更改数据）
  public boolean reduceStock(GoodsVo goodsVo) {
    MiaoshaGoods g = new MiaoshaGoods();
    g.setGoodsId(goodsVo.getId());
    int tmp = goodsDAO.reduceStock(g);
    //log.info("tmp is {}",tmp);
    return tmp > 0;
  }

  public void resetStock(List<GoodsVo> goodsList) {
    for (GoodsVo goods : goodsList) {
      MiaoshaGoods g = new MiaoshaGoods();
      g.setGoodsId(goods.getId());
      g.setMiaoshaCount(goods.getMiaoshaCount());
      goodsDAO.resetStock(g);
    }
  }
}
