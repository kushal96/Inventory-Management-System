-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
-- -----------------------------------------------------
-- Schema nvpatel
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema nvpatel
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `nvpatel` DEFAULT CHARACTER SET latin1 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`Theater`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Theater` (
  `idTheater` INT NOT NULL,
  `Theater_Name` VARCHAR(45) NOT NULL,
  `Location` VARCHAR(45) NULL,
  PRIMARY KEY (`idTheater`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Screen`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Screen` (
  `idScreen` INT NOT NULL,
  `Seat_capacity` VARCHAR(45) NOT NULL,
  `idTheater` INT NOT NULL,
  PRIMARY KEY (`idScreen`),
  
  CONSTRAINT `idTheater`
    FOREIGN KEY (`idTheater`)
    REFERENCES `mydb`.`Theater` (`idTheater`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`TimeSlots`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`TimeSlots` (
  `idTimeSlot` INT NOT NULL,
  `Start_Time` VARCHAR(45) NULL,
  `End_Time` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idTimeSlot`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Screen_timeslot`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Screen_timeslot` (
  `idScreen` INT NULL,
  `idTimeSlot` INT NULL,
  `day` DATE NOT NULL,
  `Price` INT NULL,
  `Attendence` INT NULL,
  
 
  CONSTRAINT `idScreen`
    FOREIGN KEY (`idScreen`)
    REFERENCES `mydb`.`Screen` (`idScreen`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `idTimeSlot`
    FOREIGN KEY (`idTimeSlot`)
    REFERENCES `mydb`.`TimeSlots` (`idTimeSlot`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Movies`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Movies` (
  `idMovies` INT NOT NULL,
  `Title` VARCHAR(200) NULL,
  `Duration` INT NULL,
  `Type` VARCHAR(45) NULL,
  PRIMARY KEY (`idMovies`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Movie_Sequence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Movie_Sequence` (
  `idMovie` INT NULL,
  `idTimeSlot` INT NULL,
  `Sequence` INT NULL,
 

  CONSTRAINT `idMovies`
    FOREIGN KEY (`idMovie`)
    REFERENCES `mydb`.`Movies` (`idMovies`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Movie_Sequence_Screen_timeslot1`
    FOREIGN KEY (`idTimeSlot`)
    REFERENCES `mydb`.`TimeSlots` (`idTimeSlot`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `nvpatel` ;

-- -----------------------------------------------------
-- Table `nvpatel`.`categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nvpatel`.`categories` (
  `CategoryID` INT(11) NOT NULL AUTO_INCREMENT,
  `CategoryName` VARCHAR(15) NOT NULL,
  `Description` MEDIUMTEXT NULL DEFAULT NULL,
  `Picture` LONGBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`CategoryID`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `nvpatel`.`web`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nvpatel`.`web` (
  `web_id` INT(2) NOT NULL,
  `URL` VARCHAR(20) NULL DEFAULT NULL,
  PRIMARY KEY (`web_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `nvpatel`.`course`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nvpatel`.`course` (
  `course_key` INT(2) NOT NULL,
  `name` VARCHAR(20) NULL DEFAULT NULL,
  `web_id` INT(2) NOT NULL,
  PRIMARY KEY (`course_key`),
  
  CONSTRAINT `course_ibfk_1`
    FOREIGN KEY (`web_id`)
    REFERENCES `nvpatel`.`web` (`web_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `nvpatel`.`menu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nvpatel`.`menu` (
  `menu_id` INT(2) NOT NULL,
  `menu_description` VARCHAR(20) NULL DEFAULT NULL,
  `menu_type` VARCHAR(20) NULL DEFAULT NULL,
  PRIMARY KEY (`menu_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `nvpatel`.`dish`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nvpatel`.`dish` (
  `dish_id` INT(11) NOT NULL,
  `menu_id` INT(2) NOT NULL,
  `dish_name` VARCHAR(20) NULL DEFAULT NULL,
  `prep_time` TIME NULL DEFAULT NULL,
  `ingredient` ENUM('i1', 'i2') NULL DEFAULT NULL,
  PRIMARY KEY (`dish_id`),
  
  CONSTRAINT `dish_ibfk_1`
    FOREIGN KEY (`menu_id`)
    REFERENCES `nvpatel`.`menu` (`menu_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `nvpatel`.`event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nvpatel`.`event` (
  `Event_ID` INT(11) NOT NULL,
  `Event_Location` VARCHAR(100) NULL DEFAULT NULL,
  `Event_Time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`Event_ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `nvpatel`.`staff`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nvpatel`.`staff` (
  `name` VARCHAR(20) NULL DEFAULT NULL,
  `salary` FLOAT NULL DEFAULT NULL,
  `Skill` ENUM('s1', 's2') NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `nvpatel`.`work_schedule`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nvpatel`.`work_schedule` (
  `start_time` DATETIME NULL DEFAULT NULL,
  `end_time` DATETIME NULL DEFAULT NULL,
  `position` VARCHAR(30) NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
