CREATE TABLE `goods` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品id',
    `goods_name` varchar(64) DEFAULT NULL COMMENT '商品名称',
    `goods_title` varchar(64) DEFAULT NULL COMMENT '商品标题',
    `goods_img` varchar(64) DEFAULT NULL COMMENT '商品的图片',
    `goods_detail` longtext COMMENT '商品的详情介绍',
    `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
    `goods_stock` int(11) DEFAULT '0' COMMENT '商品库存，-1表示无限制',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT=3 DEFAULT CHAR SET = utf8mb4;

INSERT INTO `goods` VALUES (1, 'Nintendo Switch','ns Title','ns img','ns detail','1999.99',10),(2, 'Sony PlayStation5','ps5 Title','ps5 img','ps5 detail','2999.99',10);