CREATE TABLE `miaosha_goods` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
    `goods_id` bigint(20) DEFAULT NULL COMMENT '商品id',
    `miaosha_price` decimal(10,2) DEFAULT '0.00' COMMENT '秒杀价',
    `miaosha_count` int(11) DEFAULT NULL COMMENT '秒杀库存',
    `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
    `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT=3 DEFAULT CHAR SET = utf8mb4;

INSERT INTO `miaosha_goods` VALUES (1,1,0.01,4,'2019-12-24 18:00:00','2019-12-25 08:00:00'),(2,2,0.01,9,'2019-12-24 13:00:00','2019-12-25 13:00:00');