CREATE DATABASE  IF NOT EXISTS `portshipsecure` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `portshipsecure`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: portshipsecure
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `captain`
--

DROP TABLE IF EXISTS `captain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `captain` (
  `CaptainID` int NOT NULL,
  `Name` varchar(100) NOT NULL,
  `LicenseNumber` varchar(50) DEFAULT NULL,
  `Nationality` varchar(50) DEFAULT NULL,
  `HireDate` date DEFAULT NULL,
  `ExperienceYears` int DEFAULT NULL,
  PRIMARY KEY (`CaptainID`),
  UNIQUE KEY `LicenseNumber` (`LicenseNumber`),
  CONSTRAINT `captain_chk_1` CHECK ((`ExperienceYears` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `captain`
--

LOCK TABLES `captain` WRITE;
/*!40000 ALTER TABLE `captain` DISABLE KEYS */;
INSERT INTO `captain` VALUES (1,'Angelica Tucker','LN1','Bangladesh','2005-06-01',25),(2,'Kim Martinez','LN2','Palestinian Territory','2008-03-15',22),(3,'Joshua Baker','LN3','Tunisia','2009-09-30',20),(4,'Tracey Hickman','LN4','Saudi Arabia','2011-01-20',19),(5,'Christina Collins','LN5','Paraguay','2013-07-10',18),(6,'Jessica Holmes','LN6','Turkey','2014-11-25',16),(7,'Austin Smith','LN7','Mexico','2016-04-18',15),(8,'Daniel Jones','LN8','Niue','2017-12-05',12),(9,'Austin Johnson','LN9','Greenland','2019-08-22',10),(10,'David Caldwell','LN10','Croatia','2021-02-13',9);
/*!40000 ALTER TABLE `captain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `captain_ship_history`
--

DROP TABLE IF EXISTS `captain_ship_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `captain_ship_history` (
  `CaptainID` int NOT NULL,
  `ShipID` int NOT NULL,
  `AssignDate` date NOT NULL,
  PRIMARY KEY (`CaptainID`,`ShipID`,`AssignDate`),
  KEY `FK_CaptainShipHistory_Ship` (`ShipID`),
  CONSTRAINT `FK_CaptainShipHistory_Captain` FOREIGN KEY (`CaptainID`) REFERENCES `captain` (`CaptainID`),
  CONSTRAINT `FK_CaptainShipHistory_Ship` FOREIGN KEY (`ShipID`) REFERENCES `ship` (`ShipID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `captain_ship_history`
--

LOCK TABLES `captain_ship_history` WRITE;
/*!40000 ALTER TABLE `captain_ship_history` DISABLE KEYS */;
INSERT INTO `captain_ship_history` VALUES (2,2,'2024-12-01'),(6,2,'2024-11-01'),(7,4,'2024-11-28'),(9,4,'2024-11-05'),(7,6,'2024-12-05'),(4,8,'2024-11-25'),(5,8,'2024-11-10'),(10,8,'2024-11-20'),(4,9,'2024-11-12'),(3,10,'2024-11-15');
/*!40000 ALTER TABLE `captain_ship_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cargo`
--

DROP TABLE IF EXISTS `cargo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cargo` (
  `CargoID` int NOT NULL,
  `VoyageID` int DEFAULT NULL,
  `ProductID` int DEFAULT NULL,
  `DestinationPortID` int DEFAULT NULL,
  `Weight` float DEFAULT NULL,
  `ContainerType` varchar(50) DEFAULT NULL,
  `ContainerSeal` varchar(50) DEFAULT NULL,
  `ContainerStatus` varchar(50) DEFAULT NULL,
  `LoadedByStaffID` int DEFAULT NULL,
  `LoadingDate` date DEFAULT NULL,
  `UnloadingDate` date DEFAULT NULL,
  PRIMARY KEY (`CargoID`),
  KEY `FK_Cargo_Voyage` (`VoyageID`),
  KEY `FK_Cargo_Product` (`ProductID`),
  KEY `FK_Cargo_DestinationPort` (`DestinationPortID`),
  KEY `FK_Cargo_Staff` (`LoadedByStaffID`),
  CONSTRAINT `FK_Cargo_DestinationPort` FOREIGN KEY (`DestinationPortID`) REFERENCES `port` (`PortID`),
  CONSTRAINT `FK_Cargo_Product` FOREIGN KEY (`ProductID`) REFERENCES `product` (`ProductID`),
  CONSTRAINT `FK_Cargo_Staff` FOREIGN KEY (`LoadedByStaffID`) REFERENCES `portstaff` (`StaffID`),
  CONSTRAINT `FK_Cargo_Voyage` FOREIGN KEY (`VoyageID`) REFERENCES `voyage` (`VoyageID`),
  CONSTRAINT `cargo_chk_1` CHECK ((`Weight` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cargo`
--

LOCK TABLES `cargo` WRITE;
/*!40000 ALTER TABLE `cargo` DISABLE KEYS */;
INSERT INTO `cargo` VALUES (1,2,5,7,1665.76,'TypeA','Seal1','OK',1,'2025-01-02','2025-01-14'),(2,5,9,3,5126.05,'TypeA','Seal2','OK',2,'2025-01-03','2025-01-15'),(3,5,9,10,2069.22,'TypeA','Seal3','OK',6,'2025-01-04','2025-01-15'),(4,3,9,9,9193.09,'TypeB','Seal4','OK',10,'2025-01-02','2025-01-13'),(5,6,8,1,1207.49,'TypeB','Seal5','OK',6,'2025-01-05','2025-01-14'),(6,5,4,1,2484.63,'TypeB','Seal6','OK',10,'2025-01-03','2025-01-15'),(7,2,2,8,8178.63,'TypeC','Seal7','OK',9,'2025-01-06','2025-01-13'),(8,3,3,8,9473.8,'TypeC','Seal8','OK',3,'2025-01-04','2025-01-14'),(9,5,9,10,4289.07,'TypeC','Seal9','OK',4,'2025-01-06','2025-01-15'),(10,9,4,5,4050.02,'TypeC','Seal10','OK',6,'2025-01-05','2025-01-13');
/*!40000 ALTER TABLE `cargo` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_before_cargo_insert` BEFORE INSERT ON `cargo` FOR EACH ROW BEGIN
    DECLARE hazard VARCHAR(20);

    SELECT HazardCode INTO hazard
    FROM Product
    WHERE ProductID = NEW.ProductID
    LIMIT 1;

    IF hazard IS NOT NULL AND hazard <> '' AND NEW.DestinationPortID IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Hazardous cargo must have a destination port assigned.';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company` (
  `CompanyID` int NOT NULL,
  `Name` varchar(100) NOT NULL,
  `HeadquarterCountry` varchar(50) DEFAULT NULL,
  `EstablishedYear` year DEFAULT NULL,
  PRIMARY KEY (`CompanyID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'Rodriguez, Figueroa and Sanchez','Burundi',1964),(2,'Garcia, Yang and Gardner','Namibia',1953),(3,'Johnson-Davis','Djibouti',1985),(4,'Guzman, Hoffman and Baldwin','Reunion',1981),(5,'Barnes, Cole and Ramirez','Trinidad and Tobago',1978),(6,'Blake and Sons','Qatar',1967),(7,'Peterson-Moore','Serbia',1963),(8,'Garcia-James','Guinea-Bissau',2019),(9,'Munoz-Roman','Saint Lucia',1961),(10,'Herrera-Dudley','Honduras',2004);
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_before_delete_company` BEFORE DELETE ON `company` FOR EACH ROW BEGIN
    IF EXISTS (SELECT 1 FROM Ship WHERE OperatingCompanyID = OLD.CompanyID) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot delete company: ships still assigned.';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `crewmember`
--

DROP TABLE IF EXISTS `crewmember`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crewmember` (
  `CrewID` int NOT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `Ranking` varchar(50) DEFAULT NULL,
  `HireDate` date DEFAULT NULL,
  `ShipID` int DEFAULT NULL,
  `SupervisorID` int DEFAULT NULL,
  PRIMARY KEY (`CrewID`),
  KEY `FK_CrewMember_Ship` (`ShipID`),
  KEY `FK_CrewMember_Supervisor` (`SupervisorID`),
  CONSTRAINT `FK_CrewMember_Ship` FOREIGN KEY (`ShipID`) REFERENCES `ship` (`ShipID`),
  CONSTRAINT `FK_CrewMember_Supervisor` FOREIGN KEY (`SupervisorID`) REFERENCES `crewmember` (`CrewID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `crewmember`
--

LOCK TABLES `crewmember` WRITE;
/*!40000 ALTER TABLE `crewmember` DISABLE KEYS */;
INSERT INTO `crewmember` VALUES (1,'Andrew Graham','Sailor','2020-01-01',10,NULL),(2,'Renee Mcdaniel','Sailor','2020-01-01',8,1),(3,'Nancy Gonzalez','Sailor','2020-01-01',9,1),(4,'Travis Mccall','Sailor','2020-01-01',5,NULL),(5,'Mary Martinez','Sailor','2020-01-01',9,4),(6,'Katie Estrada','Sailor','2020-01-01',1,NULL),(7,'Michael Spencer','Sailor','2020-01-01',2,NULL),(8,'Maurice Gray','Sailor','2020-01-01',9,7),(9,'Carrie Carroll','Sailor','2020-01-01',5,4),(10,'Roy Torres','Sailor','2020-01-01',6,6);
/*!40000 ALTER TABLE `crewmember` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dock`
--

DROP TABLE IF EXISTS `dock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dock` (
  `DockID` int NOT NULL,
  `PortID` int DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `MaxSize` float DEFAULT NULL,
  `Depth` float DEFAULT NULL,
  PRIMARY KEY (`DockID`),
  KEY `FK_Dock_Port` (`PortID`),
  CONSTRAINT `FK_Dock_Port` FOREIGN KEY (`PortID`) REFERENCES `port` (`PortID`),
  CONSTRAINT `dock_chk_1` CHECK ((`MaxSize` > 0)),
  CONSTRAINT `dock_chk_2` CHECK ((`Depth` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dock`
--

LOCK TABLES `dock` WRITE;
/*!40000 ALTER TABLE `dock` DISABLE KEYS */;
INSERT INTO `dock` VALUES (1,5,'North Dock',431.76,14.28),(2,6,'Main Container Dock',330.94,15.57),(3,1,'East Cargo Pier',364.51,16.6),(4,2,'South Terminal Dock',442.13,18),(5,7,'Fuel Handling Dock',211.19,14.54),(6,6,'Maintenance Dock',165.06,10.33),(7,5,'General Freight Dock',380.73,15.25),(8,2,'Heavy Equipment Dock',343.65,7.57),(9,4,'Inspection Dock',165.36,10.69),(10,9,'Emergency Access Dock',187.85,9.86);
/*!40000 ALTER TABLE `dock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inspection`
--

DROP TABLE IF EXISTS `inspection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inspection` (
  `InspectionID` int NOT NULL,
  `CargoID` int DEFAULT NULL,
  `InspectorID` int DEFAULT NULL,
  `PortID` int DEFAULT NULL,
  `InspectionDate` date DEFAULT NULL,
  `Result` varchar(50) DEFAULT NULL,
  `Notes` text,
  PRIMARY KEY (`InspectionID`),
  KEY `FK_Inspection_Cargo` (`CargoID`),
  KEY `FK_Inspection_Inspector` (`InspectorID`),
  KEY `FK_Inspection_Port` (`PortID`),
  CONSTRAINT `FK_Inspection_Cargo` FOREIGN KEY (`CargoID`) REFERENCES `cargo` (`CargoID`),
  CONSTRAINT `FK_Inspection_Inspector` FOREIGN KEY (`InspectorID`) REFERENCES `portstaff` (`StaffID`),
  CONSTRAINT `FK_Inspection_Port` FOREIGN KEY (`PortID`) REFERENCES `port` (`PortID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inspection`
--

LOCK TABLES `inspection` WRITE;
/*!40000 ALTER TABLE `inspection` DISABLE KEYS */;
INSERT INTO `inspection` VALUES (1,1,8,9,'2025-01-16','Passed','All OK'),(2,2,8,2,'2025-01-16','Passed','All OK'),(3,3,4,4,'2025-01-16','Passed','All OK'),(4,4,2,6,'2025-01-16','Passed','All OK'),(5,5,1,10,'2025-01-16','Passed','All OK'),(6,6,9,4,'2025-01-16','Passed','All OK'),(7,7,10,4,'2025-01-16','Passed','All OK'),(8,8,1,2,'2025-01-16','Passed','All OK'),(9,9,1,4,'2025-01-16','Passed','All OK'),(10,10,2,1,'2025-01-16','Passed','All OK');
/*!40000 ALTER TABLE `inspection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `port`
--

DROP TABLE IF EXISTS `port`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `port` (
  `PortID` int NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Location` varchar(100) DEFAULT NULL,
  `Country` varchar(50) DEFAULT NULL,
  `CapacityDocks` int DEFAULT NULL,
  PRIMARY KEY (`PortID`),
  CONSTRAINT `port_chk_1` CHECK ((`CapacityDocks` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `port`
--

LOCK TABLES `port` WRITE;
/*!40000 ALTER TABLE `port` DISABLE KEYS */;
INSERT INTO `port` VALUES (1,'New Kellystad Port','Lake Chad','Panama',7),(2,'Port Keith Port','Port Jesseville','Hong Kong',6),(3,'Shawnstad Port','West Michael','Paraguay',10),(4,'Jacquelineland Port','New Jessica','Central African Republic',18),(5,'Grayside Port','Lake Mark','Seychelles',19),(6,'East Lydiamouth Port','Adamsborough','United States Minor Outlying Islands',37),(7,'Jasonfort Port','Wilkersonmouth','Cambodia',43),(8,'Donaldside Port','Juliechester','Ukraine',6),(9,'Coxberg Port','Daviston','Tokelau',40),(10,'East Courtneychester Port','South Patrickmouth','Malta',17);
/*!40000 ALTER TABLE `port` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_before_delete_port` BEFORE DELETE ON `port` FOR EACH ROW BEGIN
    IF EXISTS (
        SELECT 1 FROM Voyage WHERE DeparturePortID = OLD.PortID OR ArrivalPortID = OLD.PortID
        UNION
        SELECT 1 FROM Cargo WHERE DestinationPortID = OLD.PortID
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot delete port: it is referenced by cargo or voyages.';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `portstaff`
--

DROP TABLE IF EXISTS `portstaff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `portstaff` (
  `StaffID` int NOT NULL,
  `FullName` varchar(100) NOT NULL,
  `Role` varchar(50) DEFAULT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `AssignedPortID` int DEFAULT NULL,
  `ShiftStart` time DEFAULT NULL,
  `ShiftEnd` time DEFAULT NULL,
  PRIMARY KEY (`StaffID`),
  KEY `FK_PortStaff_Port` (`AssignedPortID`),
  CONSTRAINT `FK_PortStaff_Port` FOREIGN KEY (`AssignedPortID`) REFERENCES `port` (`PortID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `portstaff`
--

LOCK TABLES `portstaff` WRITE;
/*!40000 ALTER TABLE `portstaff` DISABLE KEYS */;
INSERT INTO `portstaff` VALUES (1,'Miranda Khan','Loader','001-737-631-1656','dshields@hotmail.com',1,'08:00:00','17:00:00'),(2,'Maria Brown','Loader','001-513-338-7262','williamsyvette@gmail.com',4,'08:00:00','17:00:00'),(3,'Tammy Allison','Loader','(677)360-2606','smitchell@hotmail.com',1,'08:00:00','17:00:00'),(4,'Kristi Higgins MD','Loader','309-805-0097','caseyjones@powell.com',6,'08:00:00','17:00:00'),(5,'Amanda Miller','Loader','+1-998-543-5346','khowell@jones.net',7,'08:00:00','17:00:00'),(6,'Timothy Ibarra','Loader','542.784.9808','mcmillandennis@gmail.com',5,'08:00:00','17:00:00'),(7,'Natalie Moore','Loader','935-348-7401','danagreen@hotmail.com',2,'08:00:00','17:00:00'),(8,'Michael Hoffman','Loader','128-059-8262','michael05@fleming.com',4,'08:00:00','17:00:00'),(9,'Justin Baxter','Loader','+1-232-260-2563','garrisonjeffrey@yahoo.com',10,'08:00:00','17:00:00'),(10,'Robert Savage','Loader','036.541.4586','wilsontara@gmail.com',6,'08:00:00','17:00:00');
/*!40000 ALTER TABLE `portstaff` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_after_portstaff_delete` AFTER DELETE ON `portstaff` FOR EACH ROW BEGIN
    DELETE FROM StaffAccount WHERE StaffID = OLD.StaffID;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `ProductID` int NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Category` varchar(50) DEFAULT NULL,
  `Description` text,
  `HazardCode` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ProductID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Industrial Solvent','Chemical','Highly flammable solvent used in cleaning operations','HZ-001'),(2,'Laptop','Electronics','Portable computer with lithium-ion battery',NULL),(3,'Engine Oil','Automotive','Lubricant for vehicle engines','HZ-002'),(4,'Organic Bananas','Food','Fresh bananas from tropical regions',NULL),(5,'Medical Oxygen','Medical','Compressed oxygen for hospital use','HZ-003'),(6,'Detergent Powder','Household','Used for laundry and cleaning',NULL),(7,'Paint Cans','Construction','Contains flammable paint substances','HZ-004'),(8,'Mobile Phone','Electronics','Latest smartphone model with charger',NULL),(9,'Compressed Gas Cylinder','Industrial','Used in welding and cutting applications','HZ-005'),(10,'Rice Bags','Food','Packaged rice from agricultural suppliers',NULL);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_before_delete_product` BEFORE DELETE ON `product` FOR EACH ROW BEGIN
    IF EXISTS (SELECT 1 FROM Cargo WHERE ProductID = OLD.ProductID) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot delete product: it is still used in cargo.';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `ship`
--

DROP TABLE IF EXISTS `ship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ship` (
  `ShipID` int NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Type` varchar(50) DEFAULT NULL,
  `FlagCountry` varchar(50) DEFAULT NULL,
  `Capacity` int DEFAULT NULL,
  `BuiltYear` year DEFAULT NULL,
  `Status` varchar(50) DEFAULT NULL,
  `EngineType` varchar(50) DEFAULT NULL,
  `InsuranceExpiry` date DEFAULT NULL,
  `LastMaintenanceDate` date DEFAULT NULL,
  `EngineerName` varchar(100) DEFAULT NULL,
  `OperatingCompanyID` int DEFAULT NULL,
  PRIMARY KEY (`ShipID`),
  KEY `FK_Ship_Company` (`OperatingCompanyID`),
  CONSTRAINT `FK_Ship_Company` FOREIGN KEY (`OperatingCompanyID`) REFERENCES `company` (`CompanyID`),
  CONSTRAINT `ship_chk_1` CHECK ((`Capacity` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ship`
--

LOCK TABLES `ship` WRITE;
/*!40000 ALTER TABLE `ship` DISABLE KEYS */;
INSERT INTO `ship` VALUES (1,'Ship1','Cargo','El Salvador',47925,2001,'Active','Diesel','2025-12-31','2024-01-01','Shannon Smith',9),(2,'Ship2','Cargo','Saint Helena',28493,2002,'Active','Diesel','2025-12-31','2024-01-01','Stephen Potter',4),(3,'Ship3','Cargo','Seychelles',30439,2003,'Active','Diesel','2025-12-31','2024-01-01','Ashley Graham',10),(4,'Ship4','Cargo','Korea',19231,2004,'Active','Diesel','2025-12-31','2024-01-01','Nathan Cortez',1),(5,'Ship5','Cargo','Macao',11463,2005,'Active','Diesel','2025-12-31','2024-01-01','Katie Rodriguez',7),(6,'Ship6','Cargo','Nigeria',23298,2006,'Active','Diesel','2025-12-31','2024-01-01','Mark Lynch',5),(7,'Ship7','Cargo','Cape Verde',11189,2007,'Active','Diesel','2025-12-31','2024-01-01','Christopher Ashley',4),(8,'Ship8','Cargo','Uzbekistan',23059,2008,'Active','Diesel','2025-12-31','2024-01-01','April Nguyen',2),(9,'Ship9','Cargo','Andorra',7078,2009,'Active','Diesel','2025-12-31','2024-01-01','Jack Galloway',7),(10,'Ship10','Cargo','Sri Lanka',7338,2010,'Active','Diesel','2025-12-31','2024-01-01','Anthony Humphrey',6);
/*!40000 ALTER TABLE `ship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ship_port_visit`
--

DROP TABLE IF EXISTS `ship_port_visit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ship_port_visit` (
  `ShipID` int NOT NULL,
  `PortID` int NOT NULL,
  `VisitDate` date NOT NULL,
  PRIMARY KEY (`ShipID`,`PortID`,`VisitDate`),
  KEY `FK_ShipPortVisit_Port` (`PortID`),
  CONSTRAINT `FK_ShipPortVisit_Port` FOREIGN KEY (`PortID`) REFERENCES `port` (`PortID`),
  CONSTRAINT `FK_ShipPortVisit_Ship` FOREIGN KEY (`ShipID`) REFERENCES `ship` (`ShipID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ship_port_visit`
--

LOCK TABLES `ship_port_visit` WRITE;
/*!40000 ALTER TABLE `ship_port_visit` DISABLE KEYS */;
INSERT INTO `ship_port_visit` VALUES (2,1,'2025-01-05'),(8,1,'2025-01-04'),(2,4,'2025-01-07'),(4,4,'2025-01-08'),(8,4,'2025-01-12'),(3,5,'2025-01-11'),(7,6,'2025-01-06'),(3,7,'2025-01-10'),(7,7,'2025-01-03'),(9,8,'2025-01-09');
/*!40000 ALTER TABLE `ship_port_visit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staffaccount`
--

DROP TABLE IF EXISTS `staffaccount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staffaccount` (
  `StaffID` int NOT NULL,
  `Username` varchar(50) NOT NULL,
  `HashedPassword` char(64) NOT NULL,
  PRIMARY KEY (`StaffID`),
  UNIQUE KEY `Username` (`Username`),
  CONSTRAINT `FK_StaffAccount_Staff` FOREIGN KEY (`StaffID`) REFERENCES `portstaff` (`StaffID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staffaccount`
--

LOCK TABLES `staffaccount` WRITE;
/*!40000 ALTER TABLE `staffaccount` DISABLE KEYS */;
INSERT INTO `staffaccount` VALUES (1,'user1','8a356973a80f1c81187e42a1fe8c09c0f79d4ab26b2234f9ac866257d98166e3'),(2,'user2','3c9d3b8353c75896666cd615b789919ec66a5656db4f9cbe156edf2a6fbcec24'),(3,'user3','f864589d6610f75156597a974d09e6afa68e12f01515146bdb1415707f719481'),(4,'user4','456c952261b9bd913a5f318b199e43d99f40362fed3ae3be17e9c3f61e6981c4'),(5,'user5','ec5f4a447101f0d47330516edb4e5613230c13c79bd3e30a0ad93d0d3972a753'),(6,'user6','d0ef6a493487d4e60baa0db5be222fadcaca6beca8c938567f93e8fdb5af6606'),(7,'user7','37b0d8585eb657cb8a9aa6a1d64d6269fdb47879a35925b7f251795fd3e0dee0'),(8,'user8','82abc4ea1d3df8832058737ab637eff5f3c5711bc50736898b2e4bcb4f07f320'),(9,'user9','a77621f0b659193ce9f9bcd8a1857b2523fd39965da3a7d9b9dc17844f006249'),(10,'user10','0a5e70d68846f4c72a7eeb510fb3b81d83d136919421988cb7229be4181369cd');
/*!40000 ALTER TABLE `staffaccount` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voyage`
--

DROP TABLE IF EXISTS `voyage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voyage` (
  `VoyageID` int NOT NULL,
  `ShipID` int DEFAULT NULL,
  `DeparturePortID` int DEFAULT NULL,
  `ArrivalPortID` int DEFAULT NULL,
  `DepartureDate` date DEFAULT NULL,
  `ArrivalDate` date DEFAULT NULL,
  PRIMARY KEY (`VoyageID`),
  KEY `FK_Voyage_Ship` (`ShipID`),
  KEY `FK_Voyage_DeparturePort` (`DeparturePortID`),
  KEY `FK_Voyage_ArrivalPort` (`ArrivalPortID`),
  CONSTRAINT `FK_Voyage_ArrivalPort` FOREIGN KEY (`ArrivalPortID`) REFERENCES `port` (`PortID`),
  CONSTRAINT `FK_Voyage_DeparturePort` FOREIGN KEY (`DeparturePortID`) REFERENCES `port` (`PortID`),
  CONSTRAINT `FK_Voyage_Ship` FOREIGN KEY (`ShipID`) REFERENCES `ship` (`ShipID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voyage`
--

LOCK TABLES `voyage` WRITE;
/*!40000 ALTER TABLE `voyage` DISABLE KEYS */;
INSERT INTO `voyage` VALUES (1,4,8,7,'2025-01-01','2025-01-15'),(2,8,3,5,'2025-01-01','2025-01-15'),(3,3,4,9,'2025-01-01','2025-01-15'),(4,9,5,10,'2025-01-01','2025-01-15'),(5,7,10,7,'2025-01-01','2025-01-15'),(6,6,4,3,'2025-01-01','2025-01-15'),(7,9,8,2,'2025-01-01','2025-01-15'),(8,1,2,3,'2025-01-01','2025-01-15'),(9,3,7,10,'2025-01-01','2025-01-15'),(10,2,7,7,'2025-01-01','2025-01-15');
/*!40000 ALTER TABLE `voyage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voyagestatus`
--

DROP TABLE IF EXISTS `voyagestatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voyagestatus` (
  `VoyageID` int NOT NULL,
  `Status` varchar(50) DEFAULT NULL,
  `DelayReason` text,
  `LastUpdate` datetime DEFAULT NULL,
  PRIMARY KEY (`VoyageID`),
  CONSTRAINT `FK_VoyageStatus_Voyage` FOREIGN KEY (`VoyageID`) REFERENCES `voyage` (`VoyageID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voyagestatus`
--

LOCK TABLES `voyagestatus` WRITE;
/*!40000 ALTER TABLE `voyagestatus` DISABLE KEYS */;
INSERT INTO `voyagestatus` VALUES (1,'On Time','','2025-01-01 00:00:00'),(2,'On Time','','2025-01-01 00:00:00'),(3,'On Time','','2025-01-01 00:00:00'),(4,'On Time','','2025-01-01 00:00:00'),(5,'On Time','','2025-01-01 00:00:00'),(6,'On Time','','2025-01-01 00:00:00'),(7,'On Time','','2025-01-01 00:00:00'),(8,'On Time','','2025-01-01 00:00:00'),(9,'On Time','','2025-01-01 00:00:00'),(10,'On Time','','2025-01-01 00:00:00');
/*!40000 ALTER TABLE `voyagestatus` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_before_insert_voyage_status` BEFORE INSERT ON `voyagestatus` FOR EACH ROW BEGIN
    IF NOT EXISTS (SELECT 1 FROM Voyage WHERE VoyageID = NEW.VoyageID) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot insert status for non-existent VoyageID.';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Temporary view structure for view `vw_captainshipassignments`
--

DROP TABLE IF EXISTS `vw_captainshipassignments`;
/*!50001 DROP VIEW IF EXISTS `vw_captainshipassignments`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_captainshipassignments` AS SELECT 
 1 AS `CaptainID`,
 1 AS `CaptainName`,
 1 AS `ShipID`,
 1 AS `ShipName`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_hazardouscargoinfo`
--

DROP TABLE IF EXISTS `vw_hazardouscargoinfo`;
/*!50001 DROP VIEW IF EXISTS `vw_hazardouscargoinfo`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_hazardouscargoinfo` AS SELECT 
 1 AS `CargoID`,
 1 AS `VoyageID`,
 1 AS `ProductID`,
 1 AS `DestinationPortID`,
 1 AS `Weight`,
 1 AS `ContainerType`,
 1 AS `ContainerSeal`,
 1 AS `ContainerStatus`,
 1 AS `LoadedByStaffID`,
 1 AS `LoadingDate`,
 1 AS `UnloadingDate`,
 1 AS `ProductName`,
 1 AS `Category`,
 1 AS `HazardCode`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_shipswithcompany`
--

DROP TABLE IF EXISTS `vw_shipswithcompany`;
/*!50001 DROP VIEW IF EXISTS `vw_shipswithcompany`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_shipswithcompany` AS SELECT 
 1 AS `ShipID`,
 1 AS `Name`,
 1 AS `Type`,
 1 AS `FlagCountry`,
 1 AS `Capacity`,
 1 AS `BuiltYear`,
 1 AS `Status`,
 1 AS `EngineType`,
 1 AS `InsuranceExpiry`,
 1 AS `LastMaintenanceDate`,
 1 AS `EngineerName`,
 1 AS `OperatingCompanyID`,
 1 AS `CompanyName`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping events for database 'portshipsecure'
--

--
-- Dumping routines for database 'portshipsecure'
--
/*!50003 DROP FUNCTION IF EXISTS `GetCaptainName` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `GetCaptainName`(capId INT) RETURNS varchar(100) CHARSET utf8mb4
    DETERMINISTIC
BEGIN
    DECLARE capName VARCHAR(100);
    SELECT Name INTO capName FROM Captain WHERE CaptainID = capId;
    RETURN capName;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `GetCrewmemberName` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `GetCrewmemberName`(crewId INT) RETURNS varchar(100) CHARSET utf8mb4
    DETERMINISTIC
BEGIN
    DECLARE crewName VARCHAR(100);
    SELECT Name INTO crewName FROM CrewMember WHERE CrewID = crewId;
    RETURN crewName;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `GetDockCapacity` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `GetDockCapacity`(dockId INT) RETURNS float
    DETERMINISTIC
BEGIN
    DECLARE capacity FLOAT;
    SELECT MaxSize INTO capacity FROM Dock WHERE DockID = dockId;
    RETURN capacity;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `GetPortName` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `GetPortName`(pid INT) RETURNS varchar(100) CHARSET utf8mb4
    DETERMINISTIC
BEGIN
    DECLARE portName VARCHAR(100);
    SELECT Name INTO portName FROM Port WHERE PortID = pid;
    RETURN portName;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `GetShipCompany` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `GetShipCompany`(shipId INT) RETURNS varchar(100) CHARSET utf8mb4
    DETERMINISTIC
BEGIN
    DECLARE companyName VARCHAR(100);
    SELECT c.Name INTO companyName
    FROM Ship s
    JOIN Company c ON s.OperatingCompanyID = c.CompanyID
    WHERE s.ShipID = shipId;
    RETURN companyName;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `AddPort` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `AddPort`(
    IN pid INT, IN pname VARCHAR(100), IN plocation VARCHAR(100),
    IN pcountry VARCHAR(50), IN pcapacity INT
)
BEGIN
    INSERT INTO Port (PortID, Name, Location, Country, CapacityDocks)
    VALUES (pid, pname, plocation, pcountry, pcapacity);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `AddStaff` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `AddStaff`(
    IN sid INT, IN fname VARCHAR(100), IN role VARCHAR(50),
    IN phone VARCHAR(20), IN email VARCHAR(100),
    IN portId INT, IN shiftStart TIME, IN shiftEnd TIME
)
BEGIN
    INSERT INTO PortStaff (StaffID, FullName, Role, Phone, Email, AssignedPortID, ShiftStart, ShiftEnd)
    VALUES (sid, fname, role, phone, email, portId, shiftStart, shiftEnd);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `AssignCaptainToShip` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `AssignCaptainToShip`(IN capId INT, IN shipId INT, IN assignDate DATE)
BEGIN
    INSERT INTO Captain_Ship_History (CaptainID, ShipID, AssignDate)
    VALUES (capId, shipId, assignDate);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `AssignCrewmemberToShip` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `AssignCrewmemberToShip`(IN crewId INT, IN shipId INT)
BEGIN
    UPDATE CrewMember SET ShipID = shipId WHERE CrewID = crewId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `AssignStaffToPort` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `AssignStaffToPort`(IN staffId INT, IN portId INT)
BEGIN
    UPDATE PortStaff SET AssignedPortID = portId WHERE StaffID = staffId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `assign_captain` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `assign_captain`(IN capId INT, IN shipId INT, IN assignDate DATE)
BEGIN
    INSERT INTO Captain_Ship_History (CaptainID, ShipID, AssignDate)
    VALUES (capId, shipId, assignDate);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteCaptainById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteCaptainById`(IN captainId INT)
BEGIN
    DELETE FROM Captain WHERE CaptainID = captainId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteCargoById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteCargoById`(IN cargoId INT)
BEGIN
    DELETE FROM Cargo WHERE CargoID = cargoId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteInspectionById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteInspectionById`(IN inspectionId INT)
BEGIN
    DELETE FROM Inspection WHERE InspectionID = inspectionId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeletePortById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeletePortById`(IN portId INT)
BEGIN
    DELETE FROM Port WHERE PortID = portId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteProductById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteProductById`(IN productId INT)
BEGIN
    DELETE FROM Product WHERE ProductID = productId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteShipById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteShipById`(IN shipId INT)
BEGIN
    DELETE FROM Ship WHERE ShipID = shipId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteStaffAccountById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteStaffAccountById`(IN staffId INT)
BEGIN
    DELETE FROM StaffAccount WHERE StaffID = staffId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteStaffById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteStaffById`(IN staffId INT)
BEGIN
    DELETE FROM PortStaff WHERE StaffID = staffId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteVoyageById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteVoyageById`(IN voyageId INT)
BEGIN
    DELETE FROM Voyage WHERE VoyageID = voyageId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteVoyageStatusById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteVoyageStatusById`(IN voyageId INT)
BEGIN
    DELETE FROM VoyageStatus WHERE VoyageID = voyageId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DisplayVoyageStatus` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DisplayVoyageStatus`(IN voyageId INT)
BEGIN
    SELECT * FROM VoyageStatus WHERE VoyageID = voyageId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_available_ships` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_available_ships`()
BEGIN
    SELECT * FROM Ship
    WHERE ShipID NOT IN (SELECT ShipID FROM Voyage);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_captain_history` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_captain_history`(IN capId INT)
BEGIN
    SELECT * FROM Captain_Ship_History WHERE CaptainID = capId ORDER BY AssignDate DESC;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_inspection_history` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_inspection_history`()
BEGIN
    SELECT * FROM Inspection ORDER BY InspectionDate DESC;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `ListCrewmembersByShipId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `ListCrewmembersByShipId`(IN shipId INT)
BEGIN
    SELECT * FROM CrewMember WHERE ShipID = shipId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `ListInspectionsByCargoId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `ListInspectionsByCargoId`(IN cargoId INT)
BEGIN
    SELECT * FROM Inspection WHERE CargoID = cargoId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `ListPortsByCountry` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `ListPortsByCountry`(IN countryName VARCHAR(50))
BEGIN
    SELECT * FROM Port WHERE Country = countryName;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `ListStaffByPortId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `ListStaffByPortId`(IN pid INT)
BEGIN
    SELECT * FROM PortStaff WHERE AssignedPortID = pid;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SearchCaptainHistoryById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchCaptainHistoryById`(IN capId INT)
BEGIN
    SELECT * FROM Captain_Ship_History WHERE CaptainID = capId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SearchCargoByCargoId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchCargoByCargoId`(IN cargoId INT)
BEGIN
    SELECT * FROM Cargo WHERE CargoID = cargoId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SearchShipByShipId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchShipByShipId`(IN shipId INT)
BEGIN
    SELECT * FROM Ship WHERE ShipID = shipId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SearchStaffById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchStaffById`(IN sid INT)
BEGIN
    SELECT * FROM PortStaff WHERE StaffID = sid;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SearchVoyageByVoyageId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchVoyageByVoyageId`(IN voyageId INT)
BEGIN
    SELECT * FROM Voyage WHERE VoyageID = voyageId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `UpdateStaffShift` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `UpdateStaffShift`(IN staffId INT, IN shiftStart TIME, IN shiftEnd TIME)
BEGIN
    UPDATE PortStaff SET ShiftStart = shiftStart, ShiftEnd = shiftEnd WHERE StaffID = staffId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `ViewProductsByCargoId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `ViewProductsByCargoId`(IN cargoId INT)
BEGIN
    SELECT p.* FROM Cargo c
    JOIN Product p ON c.ProductID = p.ProductID
    WHERE c.CargoID = cargoId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `vw_captainshipassignments`
--

/*!50001 DROP VIEW IF EXISTS `vw_captainshipassignments`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_captainshipassignments` AS select `c`.`CaptainID` AS `CaptainID`,`c`.`Name` AS `CaptainName`,`s`.`ShipID` AS `ShipID`,`s`.`Name` AS `ShipName` from ((`captain_ship_history` `ch` join `captain` `c` on((`ch`.`CaptainID` = `c`.`CaptainID`))) join `ship` `s` on((`ch`.`ShipID` = `s`.`ShipID`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_hazardouscargoinfo`
--

/*!50001 DROP VIEW IF EXISTS `vw_hazardouscargoinfo`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_hazardouscargoinfo` AS select `c`.`CargoID` AS `CargoID`,`c`.`VoyageID` AS `VoyageID`,`c`.`ProductID` AS `ProductID`,`c`.`DestinationPortID` AS `DestinationPortID`,`c`.`Weight` AS `Weight`,`c`.`ContainerType` AS `ContainerType`,`c`.`ContainerSeal` AS `ContainerSeal`,`c`.`ContainerStatus` AS `ContainerStatus`,`c`.`LoadedByStaffID` AS `LoadedByStaffID`,`c`.`LoadingDate` AS `LoadingDate`,`c`.`UnloadingDate` AS `UnloadingDate`,`p`.`Name` AS `ProductName`,`p`.`Category` AS `Category`,`p`.`HazardCode` AS `HazardCode` from (`cargo` `c` join `product` `p` on((`c`.`ProductID` = `p`.`ProductID`))) where ((`p`.`HazardCode` is not null) and (`p`.`HazardCode` <> '')) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_shipswithcompany`
--

/*!50001 DROP VIEW IF EXISTS `vw_shipswithcompany`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_shipswithcompany` AS select `s`.`ShipID` AS `ShipID`,`s`.`Name` AS `Name`,`s`.`Type` AS `Type`,`s`.`FlagCountry` AS `FlagCountry`,`s`.`Capacity` AS `Capacity`,`s`.`BuiltYear` AS `BuiltYear`,`s`.`Status` AS `Status`,`s`.`EngineType` AS `EngineType`,`s`.`InsuranceExpiry` AS `InsuranceExpiry`,`s`.`LastMaintenanceDate` AS `LastMaintenanceDate`,`s`.`EngineerName` AS `EngineerName`,`s`.`OperatingCompanyID` AS `OperatingCompanyID`,`c`.`Name` AS `CompanyName` from (`ship` `s` join `company` `c` on((`s`.`OperatingCompanyID` = `c`.`CompanyID`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-14 14:31:11
