CREATE TABLE `miaosha_order` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀订单ID',
    `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
    `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
    `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `user_id` (`user_id`,`goods_id`)
) ENGINE = InnoDB AUTO_INCREMENT=3 DEFAULT CHAR SET = utf8mb4;
