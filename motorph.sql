-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: motor_ph
-- ------------------------------------------------------
-- Server version	8.0.36

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
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `AttID` int NOT NULL AUTO_INCREMENT,
  `ID` int DEFAULT NULL,
  `LogDate` text,
  `LogTime` text,
  `Status` text,
  PRIMARY KEY (`AttID`),
  KEY `attend_EmployeeID_idx` (`ID`),
  CONSTRAINT `attend_EmployeeID` FOREIGN KEY (`ID`) REFERENCES `employee_details` (`EmployeeID`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=195 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (1,6,'2024-01-01','07:56:01','Logged in'),(2,32,'2024-01-01','07:58:01','Logged in'),(3,6,'2024-01-01','17:02:01','Logged out'),(4,32,'2024-01-01','17:01:31','Logged out'),(5,6,'2024-01-02','07:58:01','Logged in'),(6,32,'2024-01-02','08:06:01','Logged in'),(7,6,'2024-01-02','20:02:01','Logged out'),(8,32,'2024-01-02','20:02:01','Logged out'),(9,6,'2024-01-05','07:59:01','Logged in'),(10,32,'2024-01-05','08:14:01','Logged in'),(11,6,'2024-01-05','17:02:01','Logged out'),(12,32,'2024-01-05','16:02:01','Logged out'),(13,6,'2024-01-08','07:59:08','Logged in'),(14,32,'2024-01-08','07:59:40','Logged in'),(15,6,'2024-01-08','17:02:12','Logged out'),(16,32,'2024-01-08','17:02:18','Logged out'),(17,6,'2024-01-09','07:59:08','Logged in'),(18,32,'2024-01-09','07:59:28','Logged in'),(19,6,'2024-01-09','17:02:01','Logged out'),(20,32,'2024-01-09','17:02:48','Logged out'),(21,6,'2024-01-10','07:49:08','Logged in'),(22,32,'2024-01-10','07:59:58','Logged in'),(23,6,'2024-01-10','17:02:48','Logged out'),(24,32,'2024-01-10','16:52:48','Logged out'),(25,6,'2024-01-11','07:49:08','Logged in'),(26,32,'2024-01-11','07:49:08','Logged in'),(27,6,'2024-01-11','17:02:48','Logged out'),(28,32,'2024-01-11','17:02:48','Logged out'),(29,6,'2024-01-12','07:49:08','Logged in'),(30,32,'2024-01-12','07:49:08','Logged in'),(31,6,'2024-01-12','17:02:48','Logged out'),(32,32,'2024-01-12','17:02:48','Logged out'),(33,6,'2024-01-15','07:49:08','Logged in'),(34,32,'2024-01-15','07:49:08','Logged in'),(35,6,'2024-01-15','17:02:48','Logged out'),(36,32,'2024-01-15','17:02:48','Logged out'),(37,6,'2024-01-16','07:49:08','Logged in'),(38,32,'2024-01-16','07:49:08','Logged in'),(39,6,'2024-01-16','17:02:48','Logged out'),(40,32,'2024-01-16','17:02:48','Logged out'),(41,6,'2024-01-17','07:49:08','Logged in'),(42,32,'2024-01-17','07:49:08','Logged in'),(43,6,'2024-01-17','17:02:48','Logged out'),(44,32,'2024-01-17','17:02:48','Logged out'),(45,6,'2024-01-18','07:49:08','Logged in'),(46,32,'2024-01-18','07:49:08','Logged in'),(47,6,'2024-01-18','17:02:48','Logged out'),(48,32,'2024-01-18','17:02:48','Logged out'),(49,6,'2024-01-19','07:49:08','Logged in'),(50,32,'2024-01-19','07:49:08','Logged in'),(51,6,'2024-01-19','17:02:48','Logged out'),(52,32,'2024-01-19','17:02:48','Logged out'),(53,6,'2024-01-22','07:49:08','Logged in'),(54,32,'2024-01-22','07:49:08','Logged in'),(55,6,'2024-01-22','17:02:48','Logged out'),(56,32,'2024-01-22','17:02:48','Logged out'),(57,6,'2024-01-23','07:49:08','Logged in'),(58,32,'2024-01-23','07:49:08','Logged in'),(59,6,'2024-01-23','17:02:48','Logged out'),(60,32,'2024-01-23','17:02:48','Logged out'),(61,6,'2024-01-26','07:49:08','Logged in'),(62,32,'2024-01-26','07:49:08','Logged in'),(63,6,'2024-01-26','17:02:48','Logged out'),(64,32,'2024-01-26','17:02:48','Logged out'),(65,6,'2024-01-29','07:49:08','Logged in'),(66,32,'2024-01-29','07:49:08','Logged in'),(67,6,'2024-01-29','17:02:48','Logged out'),(68,32,'2024-01-29','17:02:48','Logged out'),(69,6,'2024-01-30','07:49:08','Logged in'),(70,32,'2024-01-30','07:49:08','Logged in'),(71,6,'2024-01-30','17:02:48','Logged out'),(72,32,'2024-01-30','17:02:48','Logged out'),(73,6,'2024-01-31','07:49:08','Logged in'),(74,32,'2024-01-31','07:49:08','Logged in'),(75,6,'2024-01-31','17:02:48','Logged out'),(76,32,'2024-01-31','17:02:48','Logged out'),(77,3,'2024-01-01','07:56:01','Logged in'),(78,3,'2024-01-01','17:02:48','Logged out'),(79,3,'2024-01-02','07:56:01','Logged in'),(80,3,'2024-01-02','17:02:48','Logged out'),(81,3,'2024-01-03','07:56:01','Logged in'),(82,3,'2024-01-03','17:02:48','Logged out'),(83,3,'2024-01-04','07:56:01','Logged in'),(84,3,'2024-01-04','17:02:48','Logged out'),(85,3,'2024-01-05','07:56:01','Logged in'),(86,3,'2024-01-05','17:02:48','Logged out'),(87,3,'2024-01-08','07:56:01','Logged in'),(88,3,'2024-01-08','17:02:48','Logged out'),(89,3,'2024-01-09','07:56:01','Logged in'),(90,3,'2024-01-09','17:02:48','Logged out'),(91,3,'2024-01-10','07:56:01','Logged in'),(92,3,'2024-01-10','17:02:48','Logged out'),(93,3,'2024-01-11','07:56:01','Logged in'),(94,3,'2024-01-11','17:02:48','Logged out'),(95,3,'2024-01-12','07:56:01','Logged in'),(96,3,'2024-01-12','17:02:48','Logged out'),(97,3,'2024-01-15','07:56:01','Logged in'),(98,3,'2024-01-15','17:02:48','Logged out'),(99,3,'2024-01-16','07:56:01','Logged in'),(100,3,'2024-01-16','17:02:48','Logged out'),(101,3,'2024-01-17','07:56:01','Logged in'),(102,3,'2024-01-17','17:02:48','Logged out'),(103,3,'2024-01-18','07:56:01','Logged in'),(104,3,'2024-01-18','17:02:48','Logged out'),(105,3,'2024-01-19','07:56:01','Logged in'),(106,3,'2024-01-19','17:02:48','Logged out'),(107,3,'2024-01-22','07:56:01','Logged in'),(108,3,'2024-01-22','17:02:48','Logged out'),(109,3,'2024-01-23','07:56:01','Logged in'),(110,3,'2024-01-23','17:02:48','Logged out'),(111,3,'2024-01-24','07:56:01','Logged in'),(112,3,'2024-01-24','17:02:48','Logged out'),(113,3,'2024-01-25','07:56:01','Logged in'),(114,3,'2024-01-25','17:02:48','Logged out'),(115,3,'2024-01-26','07:56:01','Logged in'),(116,3,'2024-01-26','17:02:48','Logged out'),(117,3,'2024-01-29','07:56:01','Logged in'),(118,3,'2024-01-29','17:02:48','Logged out'),(119,3,'2024-01-30','07:56:01','Logged in'),(120,3,'2024-01-30','17:02:48','Logged out'),(121,3,'2024-01-31','07:56:01','Logged in'),(122,3,'2024-01-31','17:02:48','Logged out'),(123,6,'2024-02-26','19:17:23','Logged in'),(124,6,'2024-02-26','19:17:34','Logged out'),(125,6,'2024-02-26','19:19:56','Logged in'),(126,2,'2024-02-26','19:52:04','Logged in'),(127,2,'2024-02-26','19:52:11','Logged out'),(128,6,'2024-02-27','15:00:40','Logged in'),(129,6,'2024-02-27','15:00:50','Logged out'),(130,2,'2024-02-27','15:11:26','Logged in'),(131,2,'2024-02-27','15:11:33','Logged out'),(132,1,'2024-03-01','16:31:37','Logged in'),(133,1,'2024-03-01','16:31:43','Logged out'),(164,6,'2024-03-04','12:59:24','Logged In'),(165,6,'2024-03-04','12:59:30','Logged Out'),(166,6,'2024-03-04','13:01:32','Logged In'),(167,6,'2024-03-04','13:01:36','Logged Out'),(168,6,'2024-03-04','13:03:57','Logged In'),(169,6,'2024-03-04','13:04:02','Logged Out'),(170,6,'2024-03-05','18:24:35','Logged In'),(171,6,'2024-03-05','18:24:39','Logged Out'),(172,6,'2024-03-05','18:37:38','Logged In'),(173,6,'2024-03-05','18:37:43','Logged Out'),(174,6,'2024-03-05','18:40:11','Logged In'),(175,6,'2024-03-05','18:42:37','Logged In'),(176,6,'2024-03-05','18:52:10','Logged In'),(177,6,'2024-03-05','18:52:14','Logged Out'),(178,6,'2024-03-06','14:57:51','Logged In'),(179,6,'2024-03-06','15:01:43','Logged In'),(180,6,'2024-03-06','15:01:46','Logged Out'),(181,6,'2024-03-06','15:05:30','Logged In'),(182,6,'2024-03-06','15:05:36','Logged Out'),(183,6,'2024-03-06','15:07:45','Logged In'),(184,6,'2024-03-06','15:07:50','Logged Out'),(185,6,'2024-03-06','15:08:52','Logged In'),(186,6,'2024-03-06','15:08:56','Logged Out'),(187,6,'2024-03-08','16:13:30','Logged In'),(188,6,'2024-03-08','16:13:33','Logged Out'),(189,6,'2024-03-09','12:16:55','Logged In'),(190,6,'2024-03-09','12:16:59','Logged Out'),(191,6,'2024-03-17','13:32:18','Logged In'),(192,6,'2024-03-17','13:32:21','Logged Out'),(193,32,'2024-03-17','13:32:35','Logged In'),(194,32,'2024-03-17','13:32:39','Logged Out');
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bonuses`
--

DROP TABLE IF EXISTS `bonuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bonuses` (
  `idbonuses` int NOT NULL,
  `basic_salary` double DEFAULT NULL,
  `phone_allowance` int DEFAULT NULL,
  `clothing_allowance` int DEFAULT NULL,
  `rice_subsidy` int DEFAULT NULL,
  PRIMARY KEY (`idbonuses`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bonuses`
--

LOCK TABLES `bonuses` WRITE;
/*!40000 ALTER TABLE `bonuses` DISABLE KEYS */;
INSERT INTO `bonuses` VALUES (1,90000,2000,1000,1500),(2,53500,1000,1000,1500),(3,40000,800,800,1500),(4,23000,500,500,1500);
/*!40000 ALTER TABLE `bonuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee_details`
--

DROP TABLE IF EXISTS `employee_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_details` (
  `EmployeeID` int NOT NULL AUTO_INCREMENT,
  `Last_Name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `First_Name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `Birthday` date NOT NULL,
  `Address` varchar(100) NOT NULL,
  `Phone_Number` varchar(12) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `SSS` varchar(12) NOT NULL,
  `Philhealth` varchar(14) NOT NULL,
  `Pag_ibig` varchar(14) NOT NULL,
  `TIN` varchar(15) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`EmployeeID`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee_details`
--

LOCK TABLES `employee_details` WRITE;
/*!40000 ALTER TABLE `employee_details` DISABLE KEYS */;
INSERT INTO `employee_details` VALUES (1,'Garcia','Manuel III','1983-10-11','Valero Carpark Building Valero Street 1227, Makati City','966-860-270','44-4506057-3','820126853951','691295330870','442-605-657-000'),(2,'Lim','Antonio','1988-06-19','San Antonio De Padua 2, Block 1 Lot 8 and 2, Dasmarinas, Cavite','171-867-411','52-2061274-9','331735646338','663904995411','683-102-776-000'),(3,'Aquino','Bianca Sofia','1989-08-04','Rm. 402 4/F Jiao Building Timog Avenue Cor. Quezon Avenue 1100, Quezon City','966-889-370','30-8870406-2','177451189665','171519773969','971-711-280-000'),(4,'Reyes','Isabella','1994-06-16','460 Solanda Street Intramuros 1000, Manila','786-868-477','40-2511815-0','341911411254','416946776041','876-809-437-000'),(5,'Hernandez','Eduard','1989-09-23','National Highway, Gingoog,  Misamis Occidental','088-861-012','50-5577638-1','957436191812','952347222457','031-702-374-000'),(6,'Villanueva','Andrea Mae','1988-02-14','17/85 Stracke Via Suite 042, Poblacion, Las Piñas 4783 Dinagat Islands ','918-621-603','49-1632020-8','382189453145','441093369646','317-674-022-000'),(7,'San Jose','Brad ','1996-03-15','99 Strosin Hills, Poblacion, Bislig 5340 Tawi-Tawi','797-009-261','40-2400714-1','239192926939','210850209964','672-474-690-000'),(8,'Romualdez','Alice','1992-05-14','12A/33 Upton Isle Apt. 420, Roxas City 1814 Surigao del Norte ','983-606-799','55-4476527-2','545652640232','211385556888','888-572-294-000'),(9,'Atienza','Rosie ','1948-09-24','90A Dibbert Terrace Apt. 190, San Lorenzo 6056 Davao del Norte','266-036-427','41-0644692-3','708988234853','260107732354','604-997-793-000'),(10,'Alvaro','Roderick','1988-03-30','#284 T. Morato corner, Scout Rallos Street, Quezon City','053-381-386','64-7605054-4','578114853194','799254095212','525-420-419-000'),(11,'Salcedo','Anthony','1993-09-14','93/54 Shanahan Alley Apt. 183, Santo Tomas 1572 Masbate','070-766-300','26-9647608-3','126445315651','218002473454','210-805-911-000'),(12,'Lopez','Josie ','1987-01-14','49 Springs Apt. 266, Poblacion, Taguig 3200 Occidental Mindoro','478-355-427','44-8563448-3','431709011012','113071293354','218-489-737-000'),(13,'Farala','Martha','1942-01-11','42/25 Sawayn Stream, Ubay 1208 Zamboanga del Norte ','329-034-366','45-5656375-0','233693897247','631130283546','210-835-851-000'),(14,'Martinez','Leila','1970-07-11','37/46 Kulas Roads, Maragondon 0962 Quirino ','877-110-749','27-2090996-4','515741057496','101205445886.0','275-792-513-000'),(15,'Romualdez','Fredrick ','1985-03-10','22A/52 Lubowitz Meadows, Pililla 4895 Zambales','023-079-009','26-8768374-1','308366860059','223057707853.0','598-065-761-000'),(16,'Mata','Christian','1987-10-21','90 O\'Keefe Spur Apt. 379, Catigbian 2772 Sulu ','783-776-744','49-2959312-6','824187961962','631052853464','103-100-522-000'),(17,'De Leon','Selena ','1975-02-20','89A Armstrong Trace, Compostela 7874 Maguindanao','975-432-139','27-2090208-8','587272469938','719007608464','482-259-498-000'),(18,'San Jose','Allison ','1986-06-24','08 Grant Drive Suite 406, Poblacion, Iloilo City 9186 La Union','179-075-129','45-3251383-0','745148459521','114901859343','121-203-336-000'),(19,'Rosario','Cydney ','1996-10-06','93A/21 Berge Points, Tapaz 2180 Quezon','868-819-912','49-1629900-2','579253435499','265104358643','122-244-511-000'),(20,'Bautista','Mark ','1991-02-12','65 Murphy Center Suite 094, Poblacion, Palayan 5636 Quirino','683-725-348','49-1647342-5','399665157135','260054585575','273-970-941-000'),(21,'Lazaro','Darlene ','1985-11-25','47A/94 Larkin Plaza Apt. 179, Poblacion, Caloocan 2751 Quirino','740-721-558','45-5617168-2','606386917510','104907708845','354-650-951-000'),(22,'Delos Santos','Kolby ','1980-02-26','06A Gulgowski Extensions, Bongabon 6085 Zamboanga del Sur','739-443-033','52-0109570-6','357451271274','113017988667','187-500-345-000'),(23,'Santos','Vella ','1983-12-31','99A Padberg Spring, Poblacion, Mabalacat 3959 Lanao del Sur','955-879-269','52-9883524-3','548670482885','360028104576','101-558-994-000'),(24,'Del Rosario','Tomas','1978-12-18','80A/48 Ledner Ridges, Poblacion, Kabankalan 8870 Marinduque','882-550-989','45-5866331-6','953901539995','913108649964','560-735-732-000'),(25,'Tolentino','Jacklyn ','1984-05-19','96/48 Watsica Flats Suite 734, Poblacion, Malolos 1844 Ifugao','675-757-366','47-1692793-0','753800654114','210546661243','841-177-857-000'),(26,'Gutierrez','Percival ','1970-12-18','58A Wilderman Walks, Poblacion, Digos 5822 Davao del Sur','512-899-876','40-9504657-8','797639382265','210897095686','502-995-671-000'),(27,'Manalaysay','Garfield ','1986-08-28','60 Goyette Valley Suite 219, Poblacion, Tabuk 3159 Lanao del Sur','948-628-136','45-3298166-4','810909286264','211274476563','336-676-445-000'),(28,'Villegas','Lizeth ','1981-12-12','66/77 Mann Views, Luisiana 1263 Dinagat Islands','332-372-215','40-2400719-4','934389652994','122238077997','210-395-397-000'),(29,'Ramos','Carol ','1978-08-20','72/70 Stamm Spurs, Bustos 4550 Iloilo','250-700-389','60-1152206-4','351830469744','212141893454','395-032-717-000'),(30,'Maceda','Emelia ','1973-04-14','50A/83 Bahringer Oval Suite 145, Kiamba 7688 Nueva Ecija','973-358-041','54-1331005-0','465087894112','515012579765','215-973-013-000'),(31,'Aguilar','Delia ','1989-01-27','95 Cremin Junction, Surallah 2809 Cotabato','529-705-439','52-1859253-1','136451303068','110018813465','599-312-588-000'),(32,'Castro','John Rafael','1992-02-09','Hi-way, Yati, Liloan Cebu','332-424-955 ','26-7145133-4','601644902402','697764069311','404-768-309-000'),(33,'Martinez','Carlos Ian','1990-11-16','Bulala, Camalaniugan','078-854-208','11-5062972-7','380685387212','993372963726','256-436-296-000'),(34,'Santos','Beatriz','1990-08-07','Agapita Building, Metro Manila','526-639-511','20-2987501-5','918460050077','874042259378','911-529-713-000'),(35,'qwe','qwe','1994-06-16','qwe','786-868-477','49-1632020-8','341911411254','663904995411','876-809-437-000'),(36,'Ferrer','Mia','2000-01-30','San san','123-456-789','44-4506057-3','331735646338','171519773969','971-711-280-000'),(37,'Garren','Maki','2000-09-02','Nananannan','123-456-777','67-4506057-7','331735777338','171519773967','971-711-777-000'),(39,'Null','Null','2000-06-03','NullNullNullNull','123-456-000','67-4506057-0','331735770008','171519770007','971-711-000-000');
/*!40000 ALTER TABLE `employee_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job_details`
--

DROP TABLE IF EXISTS `job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job_details` (
  `jobId` int NOT NULL AUTO_INCREMENT,
  `EmployeeID` int NOT NULL,
  `Last_Name` text NOT NULL,
  `First_Name` text NOT NULL,
  `Status` text,
  `Position` text,
  `Immediate_Supervisor` text,
  `Basic_Salary` double DEFAULT NULL,
  `Rice_Subsidy` double DEFAULT NULL,
  `Phone_Allowance` double DEFAULT NULL,
  `Clothing_Allowance` double DEFAULT NULL,
  `Gross_Semi_monthly_Rate` double DEFAULT NULL,
  `Hourly_Rate` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`jobId`),
  KEY `fk_job_details_employee_details1_idx` (`EmployeeID`),
  CONSTRAINT `fk_job_details_employee_details1` FOREIGN KEY (`EmployeeID`) REFERENCES `employee_details` (`EmployeeID`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job_details`
--

LOCK TABLES `job_details` WRITE;
/*!40000 ALTER TABLE `job_details` DISABLE KEYS */;
INSERT INTO `job_details` VALUES (1,1,'Garcia','Manuel III','Regular','Chief Executive Officer','N/A',90000,1500,2000,1000,45000,535.71),(2,2,'Lim','Antonio','Regular','Chief Operating Officer','Garcia, Manuel III',60000,1500,2000,1000,30000,357.14),(3,3,'Aquino','Bianca Sofia','Regular','Chief Finance Officer','Garcia, Manuel III',60000,1500,2000,1000,30000,357.14),(4,4,'Reyes','Isabella','Regular','Chief Marketing Officer','Garcia, Manuel III',60000,1500,2000,1000,30000,357.14),(5,5,'Hernandez','Eduard','Regular','IT Operations and Systems','Lim, Antonio',52670,1500,1000,1000,26335,313.51),(6,6,'Villanueva','Andrea Mae','Regular','HR Manager','Lim, Antonio',52670,1500,1000,1000,26335,313.51),(7,7,'San Jose','Brad ','Regular','HR Team Leader','Villanueva, Andrea Mae',42975,1500,800,800,21487.5,255.80),(8,8,'Romualdez','Alice','Regular','HR Rank and File','San, Jose Brad',22500,1500,500,500,11250,133.93),(9,9,'Atienza','Rosie ','Regular','HR Rank and File','San, Jose Brad',22500,1500,500,500,11250,133.93),(10,10,'Alvaro','Roderick','Regular','Accounting Head','Aquino, Bianca Sofia ',52670,1500,1000,1000,26335,313.51),(11,11,'Salcedo','Anthony','Regular','Payroll Manager','Alvaro, Roderick',50825,1500,1000,1000,25412.5,302.53),(12,12,'Lopez','Josie ','Regular','Payroll Team Leader','Salcedo, Anthony',38475,1500,800,800,19237.5,229.02),(13,13,'Farala','Martha','Regular','Payroll Rank and File','Salcedo, Anthony',24000,1500,500,500,12000,142.86),(14,14,'Martinez','Leila','Regular','Payroll Rank and File','Salcedo, Anthony',24000,1500,500,500,12000,142.86),(15,15,'Romualdez','Fredrick ','Regular','Account Manager','Lim, Antonio',53500,1500,1000,1000,26750,318.45),(16,16,'Mata','Christian','Regular','Account Team Leader','Romualdez, Fredrick ',42975,1500,800,800,21487.5,255.80),(17,17,'De Leon','Selena ','Regular','Account Team Leader','Romualdez, Fredrick ',41850,1500,800,800,20925,249.11),(18,18,'San Jose','Allison ','Regular','Account Rank and File','Mata, Christian',22500,1500,500,500,11250,133.93),(19,19,'Rosario','Cydney ','Regular','Account Rank and File','Mata, Christian',22500,1500,500,500,11250,133.93),(20,20,'Bautista','Mark ','Regular','Account Rank and File','Mata, Christian',23250,1500,500,500,11625,138.39),(21,21,'Lazaro','Darlene ','Probationary','Account Rank and File','Mata, Christian',23250,1500,500,500,11625,138.39),(22,22,'Delos Santos','Kolby ','Probationary','Account Rank and File','Mata, Christian',24000,1500,500,500,12000,142.86),(23,23,'Santos','Vella ','Probationary','Account Rank and File','Mata, Christian',22500,1500,500,500,11250,133.93),(24,24,'Del Rosario','Tomas','Probationary','Account Rank and File','Mata, Christian',22500,1500,500,500,11250,133.93),(25,25,'Tolentino','Jacklyn ','Probationary','Account Rank and File','De Leon, Selena',24000,1500,500,500,12000,142.86),(26,26,'Gutierrez','Percival ','Probationary','Account Rank and File','De Leon, Selena',24750,1500,500,500,12375,147.32),(27,27,'Manalaysay','Garfield ','Probationary','Account Rank and File','De Leon, Selena',24750,1500,500,500,12375,147.32),(28,28,'Villegas','Lizeth ','Probationary','Account Rank and File','De Leon, Selena',24000,1500,500,500,12000,142.86),(29,29,'Ramos','Carol ','Probationary','Account Rank and File','De Leon, Selena',22500,1500,500,500,11250,133.93),(30,30,'Maceda','Emelia ','Probationary','Account Rank and File','De Leon, Selena',22500,1500,500,500,11250,133.93),(31,31,'Aguilar','Delia ','Probationary','Account Rank and File','De Leon, Selena',22500,1500,500,500,11250,133.93),(32,32,'Castro','John Rafael','Regular','Sales & Marketing','Reyes, Isabella',52670,1500,1000,1000,26335,313.51),(33,33,'Martinez','Carlos Ian','Regular','Supply Chain and Logistics','Reyes, Isabella',52670,1500,1000,1000,26335,313.51),(34,34,'Santos','Beatriz','Regular','Customer Service and Relations','Reyes, Isabella',52670,1500,1000,1000,26335,313.51),(35,35,'qwe','qwe','Regular','QWE','qwe',24000,1500,500,500,12000,142.86),(36,39,'Null','Null','Regular','Null','Null',24000,1500,500,500,12000,142.86);
/*!40000 ALTER TABLE `job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login` (
  `userID` int NOT NULL AUTO_INCREMENT,
  `EmployeeID` int NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `role` varchar(45) NOT NULL,
  PRIMARY KEY (`userID`),
  KEY `EmployeeID_idx` (`EmployeeID`),
  CONSTRAINT `EmployeeID` FOREIGN KEY (`EmployeeID`) REFERENCES `employee_details` (`EmployeeID`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login`
--

LOCK TABLES `login` WRITE;
/*!40000 ALTER TABLE `login` DISABLE KEYS */;
INSERT INTO `login` VALUES (1,6,'admin','admin','Admin'),(2,6,'hr','h123','Admin'),(3,10,'finance','f123','Admin'),(4,32,'non','n123','Non-admin'),(5,3,'user','user','Admin');
/*!40000 ALTER TABLE `login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payroll_report`
--

DROP TABLE IF EXISTS `payroll_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payroll_report` (
  `idpayroll` int NOT NULL AUTO_INCREMENT,
  `employeeID` int NOT NULL,
  `Last Name` varchar(45) DEFAULT NULL,
  `First Name` varchar(45) DEFAULT NULL,
  `Position` varchar(45) DEFAULT NULL,
  `Basic Salary` varchar(45) DEFAULT NULL,
  `Date` varchar(45) DEFAULT NULL,
  `Month` varchar(45) DEFAULT NULL,
  `Salary Period` varchar(45) DEFAULT NULL,
  `Year` int DEFAULT NULL,
  `Days Worked` int DEFAULT NULL,
  `Gross Income` varchar(45) DEFAULT NULL,
  `Rice Subsidy` varchar(45) DEFAULT NULL,
  `Phone Allowance` varchar(45) DEFAULT NULL,
  `Clothing Allowance` varchar(45) DEFAULT NULL,
  `Total Allowances` varchar(45) DEFAULT NULL,
  `Gross Salary` varchar(45) DEFAULT NULL,
  `Philhealth` varchar(45) DEFAULT NULL,
  `SSS` varchar(45) DEFAULT NULL,
  `Pagibig` varchar(45) DEFAULT NULL,
  `Total Deductions` varchar(45) DEFAULT NULL,
  `Taxable Income` varchar(45) DEFAULT NULL,
  `Withholding Tax` varchar(45) DEFAULT NULL,
  `Net Pay` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idpayroll`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payroll_report`
--

LOCK TABLES `payroll_report` WRITE;
/*!40000 ALTER TABLE `payroll_report` DISABLE KEYS */;
INSERT INTO `payroll_report` VALUES (1,3,'Aquino','Bianca Sofia','Chief Finance Officer','60000.0','2024-03-15 15:25:10','January','Salary Period: 1 - 15',2024,11,'33,000.00','1500.0','2000.0','1000.0','4500.0','37,500.00','900.00','1,125.00','100.00','2125.0','35375.0','3,010.50','32,364.50'),(2,3,'Aquino','Bianca Sofia','Chief Finance Officer','60000.0','2024-03-17 13:13:42','January','Salary Period: 1 - 15',2024,11,'33,000.00','1500.0','2000.0','1000.0','4500.0','37,500.00','900.00','1,125.00','100.00','2125.0','35375.0','3,010.50','32,364.50');
/*!40000 ALTER TABLE `payroll_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timesheet`
--

DROP TABLE IF EXISTS `timesheet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timesheet` (
  `idTimesheet` int NOT NULL AUTO_INCREMENT,
  `EmployeeID` int NOT NULL,
  `date` date NOT NULL,
  `checkinTime` datetime NOT NULL,
  `checkinStatus` varchar(50) NOT NULL,
  `checkoutTime` datetime NOT NULL,
  `checkoutStatus` varchar(45) NOT NULL,
  PRIMARY KEY (`idTimesheet`),
  KEY `fk_timesheet_employee_details1_idx` (`EmployeeID`),
  CONSTRAINT `fk_timesheet_employee_details1` FOREIGN KEY (`EmployeeID`) REFERENCES `employee_details` (`EmployeeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timesheet`
--

LOCK TABLES `timesheet` WRITE;
/*!40000 ALTER TABLE `timesheet` DISABLE KEYS */;
/*!40000 ALTER TABLE `timesheet` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-03-17 16:09:26
