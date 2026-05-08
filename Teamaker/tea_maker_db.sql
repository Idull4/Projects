-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: tea_maker_db
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `brewing_log`
--

DROP TABLE IF EXISTS `brewing_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brewing_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cups` int NOT NULL,
  `log_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brewing_log`
--

LOCK TABLES `brewing_log` WRITE;
/*!40000 ALTER TABLE `brewing_log` DISABLE KEYS */;
INSERT INTO `brewing_log` VALUES (1,2,'2025-12-22 20:18:23'),(2,2,'2025-12-22 20:19:01'),(3,5,'2025-12-22 21:13:46'),(4,3,'2025-12-22 21:15:00'),(5,5,'2025-12-22 21:49:02'),(6,8,'2025-12-22 21:50:36'),(7,3,'2025-12-22 21:51:20'),(8,3,'2025-12-22 21:56:29'),(9,2,'2025-12-23 15:52:00'),(10,5,'2025-12-23 15:52:14'),(11,2,'2025-12-23 15:52:53'),(12,31,'2025-12-23 15:55:35'),(13,5,'2025-12-23 15:56:12'),(14,8,'2025-12-23 16:06:46'),(15,6,'2025-12-23 16:07:01'),(16,8,'2025-12-23 16:12:05');
/*!40000 ALTER TABLE `brewing_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'tea_maker_db'
--

--
-- Dumping routines for database 'tea_maker_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-23 16:31:15
