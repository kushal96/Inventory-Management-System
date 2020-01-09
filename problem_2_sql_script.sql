use nvpatel;



/* query to create table for modify the database for problem 2 task 1 */
drop table if exists track_orders;
create table track_orders 
(TransactionID int(10) primary key, ProductID int(11) not null, CostFromSupplier float(7,2) not null, ArrivalDate Date, 
  SupplierID int(11) not null, CompanyName varchar(40) , DeliveryStatus int(1) ,  
  foreign key(ProductID) references products, foreign key(SupplierID) references suppliers); 
  
  
  /* query to create table to enter data for Issue_reorders method of inventoryControl interface */
  CREATE TABLE `supplier_products_catalog` (
  `ProductID` int(11) NOT NULL,
  `SupplierID` int(11) NOT NULL,
  `ProductCost` decimal(10,0) DEFAULT NULL,
  `TransationID` varchar(13) DEFAULT NULL,
  `Quantity_ordered` int(10) DEFAULT NULL,
  `DeliveryDate` date DEFAULT NULL,
  `DeliveryStatus` tinyint(1) DEFAULT NULL,
  KEY `ProductID` (`ProductID`),
  KEY `SupplierID` (`SupplierID`),
  CONSTRAINT `supplier_products_catalog_ibfk_1` FOREIGN KEY (`ProductID`) REFERENCES `products` (`ProductID`),
  CONSTRAINT `supplier_products_catalog_ibfk_2` FOREIGN KEY (`SupplierID`) REFERENCES `suppliers` (`SupplierID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;