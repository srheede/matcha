-- phpMyAdmin SQL Dump
-- version 4.9.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8889
-- Generation Time: Aug 28, 2020 at 02:01 PM
-- Server version: 5.7.26
-- PHP Version: 7.4.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `matcha`
--

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
  `firebaseID` varchar(100) NOT NULL,
  `data` varchar(2000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Users`
--

INSERT INTO `Users` (`firebaseID`, `data`) VALUES
('tJdUOZCFa8TmUFYbEoLQ9mA3sYG3', '{lastName=WOW Imports, gender=Male, filterLocation=, geoHash=ke7fyjxqj9, filterAgeMax=55, bio=Wow Imports, filterInterests=, platform=matcha, popularity=0, sortBy=Both, pic2=, pic3=, email=info@wowimports.co.za, filterAgeMin=18, pic4=, pic5=, filterDistance=100, birthDate=08 08 1998, sexPref=Women, firstName=WOW Imports '),
('SCNi0ROvK6XwHlMXici7w8av8CD3', '{lastName=CodingIsLife, gender=Male, filterLocation=, geoHash=k3vp5hn1zk, filterAgeMax=55, bio=I love coding!, filterInterests=, platform=matcha, popularity=7, sortBy=Popularity, pic2=, pic3=, email=srheede@student.wethinkcode.co.za, filterAgeMin=18, pic4=, pic5=, filterDistance=5, birthDate=21 January 2002, sexPref=Women, firstName=WeThinkCode, location=ChIJfb9uOX9nzB0RvfDUMSh9ed8, interests=, age=18, notifications=yes, profPic=https://firebasestorage.googleapis.com/v0/b/matcha-809dc.appspot.com/o/ProfileImages/SCNi0ROvK6XwHlMXici7w8av8CD3.jpg?alt=media'),
('ma6x5kr34BcECJL3a19C0v7i8lE3', '{lastName=Rheeders, gender=Male, filterLocation=, geoHash=ke7fyjxqj9, filterAgeMax=55, bio=Facebook, filterInterests=, platform=matcha, popularity=3, sortBy=Popularity, pic2=https://firebasestorage.googleapis.com/v0/b/matcha-809dc.appspot.com/o/ProfileImages/ma6x5kr34BcECJL3a19C0v7i8lE32.jpg?alt=media');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
