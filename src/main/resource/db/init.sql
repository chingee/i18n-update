DROP TABLE IF EXISTS `i18n`;

CREATE TABLE `i18n` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `jar` varchar(1000) DEFAULT NULL,
  `filepath` varchar(2000) DEFAULT NULL,
  `keyone` varchar(500) DEFAULT NULL,
  `en` varchar(1000) DEFAULT NULL,
  `zh` varchar(1000) DEFAULT NULL,
  `changed` varchar(3) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;