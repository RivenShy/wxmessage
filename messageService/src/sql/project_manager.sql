-- MySQL dump 10.13  Distrib 5.7.40, for Linux (x86_64)
--
-- Host: localhost    Database: project_manager
-- ------------------------------------------------------
-- Server version	5.7.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `project_manager`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `project_manager` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `project_manager`;

--
-- Table structure for table `PMS_BindApply`
--

DROP TABLE IF EXISTS `PMS_BindApply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PMS_BindApply` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `serverId` int(11) DEFAULT NULL COMMENT '服务器Id',
  `userId` int(11) DEFAULT NULL COMMENT '用户Id',
  `wxNickname` varchar(28) NOT NULL COMMENT '用户微信昵称',
  `openId` varchar(128) DEFAULT NULL COMMENT '用户openId',
  `status` int(11) DEFAULT '0' COMMENT '审核状态',
  `operator` varchar(20) DEFAULT NULL COMMENT '审核人',
  `applyDate` datetime NOT NULL COMMENT '申请时间',
  `reviewDate` datetime DEFAULT NULL COMMENT '审核时间',
  `userCode` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `serverId` (`serverId`),
  KEY `userId` (`userId`),
  CONSTRAINT `PMS_BindApply_ibfk_1` FOREIGN KEY (`serverId`) REFERENCES `PMS_Server` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COMMENT='绑定申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PMS_BindApply`
--

LOCK TABLES `PMS_BindApply` WRITE;
/*!40000 ALTER TABLE `PMS_BindApply` DISABLE KEYS */;
INSERT INTO `PMS_BindApply` VALUES (23,5,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-17 09:29:37','2022-11-17 09:30:04','yanghm'),(29,5,13,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-17 13:43:35','2022-11-17 14:05:44','yaoxh'),(30,5,12,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-17 14:32:29','2022-11-22 11:07:09','zhangh17'),(31,1,7,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-17 14:38:14','2022-11-22 10:57:25','10092'),(32,5,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',0,NULL,'2022-11-18 16:29:06',NULL,'admin2'),(34,8,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-21 14:41:22','2022-11-22 12:01:03','hl'),(35,1,7,'止戈','oa9m45mJH8z1gw_LYSP8MyuYNEJc',3,NULL,'2022-11-22 10:35:41',NULL,'10092'),(40,1,10,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-22 11:48:01','2022-11-22 18:28:13','fdasf10086'),(41,1,10,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-22 18:20:39','2022-11-22 20:53:16','fdasf10086'),(42,1,1,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-22 20:59:47','2022-11-22 21:13:23','10086'),(43,1,1,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-22 21:14:00','2022-11-22 21:19:12','10086'),(44,5,0,'黄政','oa9m45rXO72CsDtQY7IUYtfHVMzo',0,NULL,'2022-11-22 23:04:03',NULL,'admin'),(45,5,13,'黄政','oa9m45rXO72CsDtQY7IUYtfHVMzo',3,NULL,'2022-11-22 23:06:44','2022-11-23 16:19:43','yaoxh'),(46,5,15,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-23 09:09:49','2022-11-23 09:51:46','yaoxw'),(47,5,11,'均仔','oa9m45mJH8z1gw_LYSP8MyuYNEJc',3,NULL,'2022-11-23 09:15:26','2022-11-23 15:33:46','yanghm'),(48,8,14,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-23 09:31:24','2022-11-23 09:39:20','hl'),(49,5,12,'均仔','oa9m45mJH8z1gw_LYSP8MyuYNEJc',3,NULL,'2022-11-23 09:47:01','2022-11-24 19:07:55','zhangh17'),(50,5,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-23 14:44:37','2022-11-23 14:48:54','wangar1'),(51,5,11,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-23 15:38:43','2022-11-23 15:43:13','yanghm'),(52,5,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-23 15:56:53','2022-11-23 15:57:14','lib43'),(53,5,12,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-23 16:13:58','2022-11-23 16:14:21','zhangh17'),(54,5,13,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-23 16:20:17','2022-11-23 16:22:03','yaoxh'),(55,7,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',0,NULL,'2022-11-23 16:46:24',NULL,'fyf'),(56,5,0,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-23 17:20:24','2022-11-23 17:20:46','chenq5'),(57,7,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-24 14:36:58','2022-11-24 14:37:50','XYP'),(58,5,18,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-24 16:38:14',NULL,'CHENQ5'),(59,5,12,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-24 18:32:39','2022-11-24 18:37:49','zhangh17'),(60,5,12,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-24 18:38:22',NULL,'zhangh17'),(61,5,12,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-24 18:45:59','2022-11-24 18:46:39','zhangh17'),(62,5,12,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-24 18:57:34','2022-11-24 19:03:03','zhangh17'),(63,5,11,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-25 15:01:16','2022-11-25 16:38:47','yanghm'),(64,5,15,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-25 15:09:37','2022-11-25 16:26:06','yaoxw'),(65,7,19,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-25 16:41:55','2022-11-25 16:43:31','XYP'),(66,5,12,'均仔','oa9m45mJH8z1gw_LYSP8MyuYNEJc',0,NULL,'2022-11-25 16:47:27',NULL,'zhangh17'),(67,5,13,'yao','oa9m45pq-nW1nTX8XyLjWmPcOOTg',1,NULL,'2022-11-25 16:55:42','2022-11-25 16:56:02','yaoxh'),(68,5,15,'yao','oa9m45pq-nW1nTX8XyLjWmPcOOTg',3,NULL,'2022-11-25 17:07:58','2022-11-25 17:09:38','yaoxw'),(69,5,18,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',3,NULL,'2022-11-28 09:34:05','2022-11-28 09:34:22','chenq5'),(70,5,16,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-28 10:58:35','2022-11-28 11:00:39','wangar1'),(71,5,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-28 11:02:39','2022-11-28 11:04:15','chenr23'),(72,5,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-28 11:06:04','2022-11-28 11:10:10','caop'),(73,5,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-28 11:07:20','2022-11-28 11:10:13','jiangs1'),(74,5,17,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',3,NULL,'2022-11-28 11:08:13','2022-11-28 11:10:17','lib43'),(75,5,22,'ℒℬ','oa9m45iuN-Wt6yh6CCMIxI71jbUI',3,NULL,'2022-11-28 11:14:59','2022-11-28 11:15:26','caop'),(76,5,22,'ℒℬ','oa9m45iuN-Wt6yh6CCMIxI71jbUI',0,NULL,'2022-11-28 11:16:29',NULL,'caop');
/*!40000 ALTER TABLE `PMS_BindApply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PMS_ChangeBindApply`
--

DROP TABLE IF EXISTS `PMS_ChangeBindApply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PMS_ChangeBindApply` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `serverId` int(11) DEFAULT NULL COMMENT '服务器Id',
  `userId` int(11) DEFAULT NULL COMMENT '用户Id',
  `wxNickname` varchar(28) NOT NULL COMMENT '用户微信昵称',
  `openId` varchar(128) DEFAULT NULL COMMENT '用户openId',
  `status` int(11) DEFAULT '0' COMMENT '审核状态',
  `operator` varchar(20) DEFAULT NULL COMMENT '审核人',
  `applyDate` datetime NOT NULL COMMENT '申请时间',
  `reviewDate` datetime DEFAULT NULL COMMENT '审核时间',
  `userCode` varchar(64) DEFAULT NULL COMMENT '用户账号',
  PRIMARY KEY (`id`),
  KEY `serverId` (`serverId`),
  CONSTRAINT `PMS_ChangeBindApply_ibfk_1` FOREIGN KEY (`serverId`) REFERENCES `PMS_Server` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COMMENT='更换绑定申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PMS_ChangeBindApply`
--

LOCK TABLES `PMS_ChangeBindApply` WRITE;
/*!40000 ALTER TABLE `PMS_ChangeBindApply` DISABLE KEYS */;
INSERT INTO `PMS_ChangeBindApply` VALUES (1,1,0,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',1,NULL,'2022-11-24 10:38:58','2022-11-24 12:38:36','10087'),(2,1,0,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o1',1,NULL,'2022-11-24 12:22:18','2022-11-24 14:29:04','10087'),(3,1,0,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',0,NULL,'2022-11-24 12:39:38',NULL,'10087'),(4,1,0,'止戈','oa9m45sEj_dDR9FGoeoGmx7_M21o',1,NULL,'2022-11-24 15:55:05','2022-11-24 16:01:55','200'),(5,1,0,'止戈','oa9m45sEj_dDR9FGoeoGmx7_M21o',1,NULL,'2022-11-24 16:02:24','2022-11-24 16:02:31','200'),(6,1,0,'止戈','oa9m45sEj_dDR9FGoeoGmx7_M21o',0,NULL,'2022-11-24 16:06:44',NULL,'200'),(7,5,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',0,NULL,'2022-11-24 16:14:53',NULL,'200'),(8,5,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',1,NULL,'2022-11-24 16:23:15','2022-11-24 16:25:11','chenq5'),(9,5,0,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',1,NULL,'2022-11-24 16:24:18','2022-11-24 18:11:03','zhangh17'),(10,5,0,'均仔','oa9m45mJH8z1gw_LYSP8MyuYNEJc',0,NULL,'2022-11-24 18:34:25',NULL,'zhangh17'),(11,5,0,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',1,NULL,'2022-11-24 18:47:57','2022-11-24 18:48:28','zhangh17'),(12,5,0,'mongo','oa9m45sEj_dDR9FGoeoGmx7_M21o',1,NULL,'2022-11-24 19:08:31','2022-11-24 19:08:46','zhangh17'),(13,5,0,'止戈','oa9m45rV0-lXYLO064zo-BJBNkSE',1,NULL,'2022-11-25 16:34:07','2022-11-25 16:38:24','yanghm');
/*!40000 ALTER TABLE `PMS_ChangeBindApply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PMS_Customer`
--

DROP TABLE IF EXISTS `PMS_Customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PMS_Customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `customerName` varchar(20) NOT NULL COMMENT '客户名称',
  `customerCode` varchar(255) DEFAULT NULL,
  `logoPath` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='客户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PMS_Customer`
--

LOCK TABLES `PMS_Customer` WRITE;
/*!40000 ALTER TABLE `PMS_Customer` DISABLE KEYS */;
INSERT INTO `PMS_Customer` VALUES (1,'小武工程','121','/logoData/1.png'),(4,'61工程',NULL,'/logoData/4.jpg'),(5,'深圳创惠工程有限公司','212','/logoData/5.png'),(6,'深圳市博铭维系统工程有限公司',NULL,'/logoData/6.png'),(7,'深圳市龙运科技有限公司',NULL,'/logoData/7.png'),(8,'深圳市沅欣智能科技有限公司','123123','/logoData/8.jpg'),(9,'赵子龙','123','/logoData/9.jpg'),(10,'赵云','1234','/logoData/10.png'),(11,'客户Test1','CodeTest1','/logoData/11.png');
/*!40000 ALTER TABLE `PMS_Customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PMS_Message`
--

DROP TABLE IF EXISTS `PMS_Message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PMS_Message` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `msgTypeId` int(11) DEFAULT NULL COMMENT '消息类型Id',
  `status` int(11) DEFAULT NULL COMMENT '消息状态',
  `sendTime` datetime DEFAULT NULL COMMENT '发送消息时间',
  `userId` int(11) DEFAULT NULL COMMENT '接收消息的用户Id',
  `clickTime` datetime DEFAULT NULL COMMENT '用户点击消息时间',
  PRIMARY KEY (`id`),
  KEY `msgTypeId` (`msgTypeId`),
  KEY `userId` (`userId`),
  CONSTRAINT `PMS_Message_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `PMS_UserInfo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='发送消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PMS_Message`
--

LOCK TABLES `PMS_Message` WRITE;
/*!40000 ALTER TABLE `PMS_Message` DISABLE KEYS */;
INSERT INTO `PMS_Message` VALUES (1,1,0,'2022-11-09 11:35:02',1,'2022-11-09 15:30:11'),(2,2,0,'2022-11-09 11:36:00',1,'2022-11-23 11:55:22'),(3,1,0,'2022-11-09 14:06:01',1,'2022-11-09 15:09:58'),(4,2,0,'2022-11-09 14:07:00',1,NULL),(5,3,0,'2022-11-09 15:02:02',7,'2022-11-09 15:16:29'),(6,1,0,'2022-11-09 15:21:01',1,NULL),(7,1,0,'2022-11-09 15:30:01',1,NULL),(8,1,0,'2022-11-09 16:20:00',1,'2022-11-09 16:21:13'),(9,3,0,'2022-11-09 16:24:00',7,'2022-11-23 12:46:15'),(11,0,1,'2022-11-10 11:02:50',8,NULL),(12,0,1,'2022-11-10 11:50:42',8,NULL),(13,0,1,'2022-11-10 11:50:51',8,NULL),(14,2,0,'2022-11-10 14:07:00',1,NULL),(15,1,0,'2022-11-10 16:20:00',1,NULL),(16,3,0,'2022-11-10 16:24:00',7,'2022-11-10 16:26:38'),(17,2,0,'2022-11-11 14:07:00',1,NULL),(18,1,0,'2022-11-11 16:20:00',1,NULL),(19,3,0,'2022-11-11 16:24:00',7,'2022-11-11 16:25:54'),(20,0,1,'2022-11-11 18:32:18',8,NULL),(21,3,1,'2022-11-12 18:30:00',7,NULL),(22,1,1,'2022-11-12 18:30:00',1,NULL),(23,3,1,'2022-11-13 18:30:00',7,NULL),(24,1,1,'2022-11-13 18:30:00',1,NULL),(25,4,1,'2022-11-14 08:45:00',7,NULL),(26,2,1,'2022-11-14 08:45:00',1,NULL),(27,7,0,'2022-11-14 16:42:50',1,NULL),(28,7,0,'2022-11-14 16:43:00',1,NULL),(29,7,0,'2022-11-14 16:43:10',1,NULL),(30,7,0,'2022-11-14 16:43:50',1,NULL),(31,7,0,'2022-11-14 16:44:00',1,'2022-11-14 16:44:19'),(32,7,0,'2022-11-14 16:44:10',1,NULL),(33,7,0,'2022-11-14 16:44:20',1,NULL),(34,7,0,'2022-11-14 16:44:30',1,'2022-11-22 15:59:08'),(35,7,0,'2022-11-14 16:44:40',1,'2022-11-14 16:44:46'),(36,7,0,'2022-11-14 16:44:50',1,NULL),(37,7,0,'2022-11-14 16:45:00',1,NULL),(38,7,0,'2022-11-14 16:45:10',1,'2022-11-14 16:45:14'),(39,7,0,'2022-11-14 16:45:20',1,NULL),(40,7,0,'2022-11-14 16:45:30',1,'2022-11-17 15:40:40'),(41,7,0,'2022-11-14 16:45:40',1,'2022-11-14 16:45:44'),(42,7,0,'2022-11-14 16:45:50',1,NULL),(43,7,0,'2022-11-14 16:46:00',1,NULL),(44,7,0,'2022-11-14 16:46:10',1,NULL),(45,7,0,'2022-11-14 16:46:20',1,NULL),(46,7,0,'2022-11-14 16:46:30',1,NULL),(47,7,0,'2022-11-14 16:46:40',1,'2022-11-14 16:47:20'),(48,12,0,'2022-11-23 11:22:00',15,'2022-11-23 14:40:30'),(49,13,0,'2022-11-23 11:50:00',15,NULL),(50,13,0,'2022-11-23 11:58:00',15,NULL),(51,13,0,'2022-11-23 12:27:01',15,'2022-11-23 14:35:46'),(52,13,0,'2022-11-23 14:55:00',16,'2022-11-23 14:55:16'),(53,13,0,'2022-11-23 14:55:01',15,'2022-11-23 14:55:27'),(54,12,0,'2022-11-23 14:56:00',16,'2022-11-23 15:42:51'),(55,12,0,'2022-11-23 14:56:00',15,'2022-11-23 15:45:26'),(56,13,0,'2022-11-23 15:51:00',16,'2022-11-23 15:51:35'),(57,13,0,'2022-11-23 15:51:00',15,NULL),(58,12,0,'2022-11-23 15:59:00',16,NULL),(59,12,0,'2022-11-23 15:59:00',15,NULL),(60,13,0,'2022-11-23 16:00:00',16,NULL),(61,13,0,'2022-11-23 16:00:00',15,NULL),(62,13,0,'2022-11-23 16:00:01',17,'2022-11-23 16:03:51'),(63,12,0,'2022-11-23 16:09:00',16,NULL),(64,12,0,'2022-11-23 16:09:01',15,'2022-11-23 16:10:13'),(65,12,0,'2022-11-23 16:17:00',16,'2022-11-23 17:12:21'),(66,12,0,'2022-11-23 16:17:01',15,'2022-11-23 16:47:28'),(67,13,0,'2022-11-23 16:19:00',16,'2022-11-23 16:47:35'),(68,13,0,'2022-11-23 16:19:01',15,'2022-11-23 16:47:21'),(69,13,0,'2022-11-23 16:19:01',17,'2022-11-23 16:27:45'),(70,12,0,'2022-11-23 16:24:00',16,NULL),(71,12,0,'2022-11-23 16:24:00',15,NULL),(72,12,0,'2022-11-23 17:23:00',18,'2022-11-23 17:48:43'),(73,12,0,'2022-11-23 17:23:01',16,NULL),(74,12,0,'2022-11-23 17:23:01',15,NULL),(75,13,0,'2022-11-23 17:24:00',18,'2022-11-23 17:48:50'),(76,13,0,'2022-11-23 17:24:00',16,NULL),(77,13,0,'2022-11-23 17:24:01',15,NULL),(78,13,0,'2022-11-23 17:24:01',17,NULL),(79,12,0,'2022-11-23 21:01:00',18,NULL),(80,12,0,'2022-11-23 21:01:02',16,'2022-11-23 21:02:00'),(81,12,0,'2022-11-23 21:01:02',15,'2022-11-24 16:22:58'),(82,13,0,'2022-11-23 21:02:00',18,NULL),(83,13,0,'2022-11-23 21:02:01',16,'2022-11-24 16:22:54'),(84,13,0,'2022-11-23 21:02:01',15,'2022-11-24 16:22:48'),(85,13,0,'2022-11-24 09:34:00',18,'2022-11-24 09:34:18'),(86,13,0,'2022-11-24 09:34:02',16,'2022-11-24 16:22:43'),(87,13,0,'2022-11-24 09:34:02',15,'2022-11-24 16:22:39'),(88,12,0,'2022-11-24 21:01:00',18,'2022-11-24 21:05:40'),(89,12,0,'2022-11-24 21:01:01',16,'2022-11-24 21:05:34'),(90,12,0,'2022-11-24 21:01:02',15,'2022-11-25 17:17:30'),(91,13,0,'2022-11-25 09:34:00',18,'2022-11-25 09:48:53'),(92,13,0,'2022-11-25 09:34:01',16,NULL),(93,13,0,'2022-11-25 09:34:01',15,'2022-11-25 17:17:20'),(94,12,0,'2022-11-25 17:12:00',15,'2022-11-25 17:19:46'),(95,13,0,'2022-11-25 17:15:00',15,'2022-11-25 17:18:56'),(96,12,0,'2022-11-28 09:36:00',18,'2022-11-28 09:39:07'),(97,12,0,'2022-11-28 09:36:01',15,NULL),(98,13,0,'2022-11-28 09:37:00',18,'2022-11-28 09:46:51'),(99,13,0,'2022-11-28 09:37:00',15,NULL);
/*!40000 ALTER TABLE `PMS_Message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PMS_MessageType`
--

DROP TABLE IF EXISTS `PMS_MessageType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PMS_MessageType` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `messageName` varchar(20) NOT NULL COMMENT '消息名称',
  `scheduleTime` varchar(64) DEFAULT NULL COMMENT '定时执行时间',
  `serverId` int(11) DEFAULT NULL COMMENT '服务器Id',
  `userId` int(11) DEFAULT NULL COMMENT '用户Id',
  `description` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `messageTime` char(2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `serverId` (`serverId`),
  KEY `userId` (`userId`),
  CONSTRAINT `PMS_MessageType_ibfk_1` FOREIGN KEY (`serverId`) REFERENCES `PMS_Server` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COMMENT='消息类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PMS_MessageType`
--

LOCK TABLES `PMS_MessageType` WRITE;
/*!40000 ALTER TABLE `PMS_MessageType` DISABLE KEYS */;
INSERT INTO `PMS_MessageType` VALUES (10,'流程待审批提醒','0 29 9 ? * 2-6 *',1,-1,'周一到周五8:30推送该消息',1,'早上'),(11,'流程待审批提醒','0 31 09 ? * MON-FRI',1,-1,'周一到周五18:30推送该消息',1,'晚上'),(12,'流程待审批提醒','0 30 8 ? * MON-FRI',5,-1,'周一到周五8:30推送该消息',1,'早上'),(13,'流程待审批提醒','0 30 18 ? * MON-FRI',5,-1,'周一到周五18:30推送该消息',1,'晚上'),(14,'流程待审批提醒','0 30 18 ? * MON-FRI',6,-1,'test',1,'早上'),(15,'流程待审批提醒','0 31 18 ? * MON-FRI',1,-1,'test',1,'晚上');
/*!40000 ALTER TABLE `PMS_MessageType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PMS_Server`
--

DROP TABLE IF EXISTS `PMS_Server`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PMS_Server` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `serverName` varchar(20) DEFAULT NULL,
  `customerId` int(11) DEFAULT NULL COMMENT '客户Id',
  `serverUrl` varchar(255) DEFAULT NULL COMMENT '服务器地址url，包括端口',
  `serverIp` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `customerId` (`customerId`),
  CONSTRAINT `PMS_Server_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `PMS_Customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='服务器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PMS_Server`
--

LOCK TABLES `PMS_Server` WRITE;
/*!40000 ALTER TABLE `PMS_Server` DISABLE KEYS */;
INSERT INTO `PMS_Server` VALUES (1,'小武服务器',1,NULL,'47.106.188.61'),(4,'Test1',4,'222','1111'),(5,'深圳创惠工程有限公司',5,'http://47.119.168.161:8091','8.135.96.87'),(6,'深圳市博铭维系统工程有限公司',6,'http://120.79.189.46:8111','120.79.189.46'),(7,'深圳市龙运科技有限公司',7,'http://120.77.147.57:8111','120.77.147.57'),(8,'深圳市沅欣智能科技有限公司',8,'http://116.205.239.61:8111','116.205.239.61'),(13,'123123',7,'http://120.79.189.46:8111','120.79.189.46'),(14,'阿武Test1',1,'localhost:8091','11.222.333.45');
/*!40000 ALTER TABLE `PMS_Server` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PMS_SysUser`
--

DROP TABLE IF EXISTS `PMS_SysUser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PMS_SysUser` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(20) NOT NULL COMMENT '用户名称',
  `password` varchar(255) NOT NULL COMMENT '用户密码',
  `enabled` tinyint(1) DEFAULT NULL COMMENT '是否可用',
  `org_id` int(11) DEFAULT NULL COMMENT '组Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PMS_SysUser`
--

LOCK TABLES `PMS_SysUser` WRITE;
/*!40000 ALTER TABLE `PMS_SysUser` DISABLE KEYS */;
INSERT INTO `PMS_SysUser` VALUES (1,'admin','$2a$10$mM6f73ctdbxs3v7XBGk6aeZ53gc2XKdwqZ5A/PBcykWmrlW.cwg6C',1,NULL);
/*!40000 ALTER TABLE `PMS_SysUser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PMS_UserInfo`
--

DROP TABLE IF EXISTS `PMS_UserInfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PMS_UserInfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `serverId` int(11) DEFAULT NULL COMMENT '服务器Id',
  `userId` varchar(64) NOT NULL COMMENT '用户Id',
  `userName` varchar(64) DEFAULT NULL COMMENT '用户名称',
  `phone` varchar(11) DEFAULT NULL COMMENT '用户手机号',
  `wxNickname` varchar(28) DEFAULT NULL COMMENT '用户微信昵称',
  `openId` varchar(128) DEFAULT NULL COMMENT '用户公众号openId',
  `wxNickname2` varchar(28) DEFAULT NULL COMMENT '第二个微信昵称',
  `openId2` varchar(128) DEFAULT NULL COMMENT '第二个g公众号openId',
  PRIMARY KEY (`id`),
  KEY `serverId` (`serverId`),
  CONSTRAINT `PMS_UserInfo_ibfk_1` FOREIGN KEY (`serverId`) REFERENCES `PMS_Server` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PMS_UserInfo`
--

LOCK TABLES `PMS_UserInfo` WRITE;
/*!40000 ALTER TABLE `PMS_UserInfo` DISABLE KEYS */;
INSERT INTO `PMS_UserInfo` VALUES (1,1,'10086','张三',NULL,NULL,NULL,NULL,NULL),(2,1,'10087','李四',NULL,NULL,NULL,NULL,NULL),(3,1,'10088','王五',NULL,NULL,NULL,NULL,NULL),(4,1,'10089','赵六',NULL,NULL,NULL,NULL,NULL),(5,1,'10090','ZS',NULL,NULL,NULL,NULL,NULL),(6,1,'10091','LS',NULL,NULL,NULL,NULL,NULL),(7,1,'10092','WW',NULL,NULL,NULL,NULL,NULL),(8,1,'10093','ZL',NULL,NULL,NULL,NULL,NULL),(9,4,'yaoxh','创惠用户1',NULL,NULL,NULL,NULL,NULL),(10,1,'fdasf10086',NULL,NULL,NULL,NULL,NULL,NULL),(11,5,'yanghm',NULL,NULL,NULL,NULL,NULL,NULL),(12,5,'zhangh17',NULL,NULL,NULL,NULL,NULL,NULL),(13,5,'yaoxh',NULL,NULL,'yao','oa9m45pq-nW1nTX8XyLjWmPcOOTg',NULL,NULL),(14,8,'hl',NULL,NULL,NULL,NULL,NULL,NULL),(15,5,'yaoxw',NULL,NULL,NULL,NULL,NULL,NULL),(16,5,'wangar1',NULL,NULL,NULL,NULL,NULL,NULL),(17,5,'lib43',NULL,NULL,NULL,NULL,NULL,NULL),(18,5,'chenq5',NULL,NULL,NULL,NULL,NULL,NULL),(19,7,'XYP',NULL,NULL,NULL,NULL,NULL,NULL),(20,1,'200',NULL,NULL,NULL,NULL,NULL,NULL),(21,5,'chenr23',NULL,NULL,NULL,NULL,NULL,NULL),(22,5,'caop',NULL,NULL,NULL,NULL,NULL,NULL),(23,5,'jiangs1',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `PMS_UserInfo` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-11-28 13:58:26
