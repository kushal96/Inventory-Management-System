/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6;

import static assignment6.History_Generator_Logic.database;
import static assignment6.History_Generator_Logic.identity;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nikita Patel
 */


    // method to implement interface 
public class inventoryControl_Methods implements inventoryControl {

    
     static  Properties identity = new Properties();
    static MyIdentity me = new MyIdentity();

	static String user;
	static String password;
	static String database;
        static Connection connect;
    
        public inventoryControl_Methods(){    
    
    }
    
    
        // method to connect with database 
    public Connection Obtain_Connection(){
        Connection  connect = null;
             try{
                      Class.forName("com.mysql.jdbc.Driver"); 
                 
                // setting user name, password , database to accsess database connection
                MyIdentity.setIdentity( identity );
                
                user = identity.getProperty("user");
                password = identity.getProperty("password");
                database = identity.getProperty("database");
                System.out.println(password);   
        
                // connecting t driver
                connect=DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306", user , password);
                Statement st = connect.createStatement();
                st.executeQuery("use "+database+";");
                
             }catch(Exception e){ }
           return connect;
        }
    
    
        // method to check the that oder is already shipped or not using shipped date
    boolean Shipping_status(int orderNumber){
           boolean status = false;
           
        try {
            Statement st = connect.createStatement();
            st.executeQuery("use "+database+";");
            
                // resultset to get the shipped date from orders table 
            ResultSet rs = Obtain_Connection().createStatement().executeQuery("select ShippedDate from orders where OrderID = '"+orderNumber+"';");
            
            rs.next();
            
                // if date is null that flag will be true and exception will be thrown that oder is all ready shipped 
            if(rs.getDate(1) == null){
            
              status = true;
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(inventoryControl_Methods.class.getName()).log(Level.SEVERE, null, ex);
        }
          return status;    
       
       }    
    
        // updates the current inventory according to the order shipped  
    boolean Update_Inventory(int orderNumber){
             
        String Message=null; 
             
        boolean status = true;
             
        Connection connect = Obtain_Connection();
             
        try{
              
            Statement st = connect.createStatement();
            st.executeQuery("use "+database+";");
             
             // to get the order number details from orderdetails 
            ResultSet rs1 = Obtain_Connection().createStatement().executeQuery("select * from orderdetails where OrderID = '"+orderNumber+"';");
                             
             
            connect.setAutoCommit(false);
             
                // checking all the data from result set
            while(rs1.next()){
                 
                    
                int Quantity_ordered = rs1.getInt("Quantity");
                int ProductID = rs1.getInt("ProductID");
                 
                    // taking that product from the invantory to update the stock
                ResultSet rs2  = Obtain_Connection().createStatement().executeQuery("select * from products where ProductID ='"+ProductID+"';");
                System.out.println("result set2" + rs2.getFetchSize());
                
                 int units =0;
                 while(rs2.next()){
                      units = rs2.getInt("UnitsInStock");
                 }
                 
                 
               // int units = rs2.getInt("UnitsInStock");
                 System.out.println("units in stock---------------"+units);
                if(units > Quantity_ordered){
                       
                        //  redusong the stock for that product 
                    int newStock = units - Quantity_ordered; 
                    
                        // taking product's unit in stock from product table 
                    connect.createStatement().executeUpdate("update  products set UnitsInStock = '"+newStock+"'where ProductID = '"+ProductID+"';");
                     //update products set UnitsInStock = '"++newStock"' where ProductID ='"+ProductID+"'
                     while(rs2.next()){
                    int UnitsOnOrder = rs2.getInt("UnitsOnOrder");
                    int ReorderLevel = rs2.getInt("ReorderLevel");
                     
                         System.out.println("rs2 units ordered " + UnitsOnOrder + " reorderLevel " + ReorderLevel);
                         System.out.println("if condition " + ((UnitsOnOrder + newStock)< ReorderLevel));
                     // updating unit is order value in product table 
                    if((UnitsOnOrder + newStock) < ReorderLevel){
                         
                        int increment_UnitsOnOrder = UnitsOnOrder + (ReorderLevel-(UnitsOnOrder + newStock));
                         
                       connect.createStatement().executeUpdate("update products set UnitsOnOrder = '"+increment_UnitsOnOrder+"' where ProductID = '"+ProductID+"';");
                     }
                     }
                   Message = "Product ID : "+ProductID+" of OrderID: "+orderNumber + " has been avialabel for Dispatch "+"\n";    
                 }
                 else{
                     
                       status = false; 
                       connect.rollback();
                       break;
                 }
                
             }
             
             if(status == true){connect.commit();   System.out.println(Message);}    
             
             }catch(Exception e){
                 e.printStackTrace();
             }
             
            
             return status;
             
         }
    
            // updating the shipping date of that order
    void Update_Shipping_Date(int orderNumber){
                   try{
                        Statement st = connect.createStatement();
                         st.executeQuery("use "+database+";");
                         java.util.Date date=new java.util.Date();
			 java.sql.Date sqlDate=new java.sql.Date(date.getTime());
                         PreparedStatement ps=Obtain_Connection().prepareStatement("insert into orders(ShippedDate) values(?) where OrderID='"+orderNumber+"';");
			 ps.setDate(1,sqlDate);
                         ps.execute();
                       
                   }catch(Exception e){
                       e.printStackTrace();
                   }
         }
   
        // implementing ship_order interface 
    @Override
    public void Ship_order(int orderNumber) throws OrderException {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
            // if shipped date is not null
        if(!Shipping_status(orderNumber)){
        
              throw new OrderException(orderNumber,"shipped");
        
        }
         // if units in order are greater than units in stock
        if (!Update_Inventory(orderNumber)){
            
              throw new OrderException(orderNumber,"stock");
            }
            // updating current database after shipping the order
        else{
                 if(Update_Inventory(orderNumber)){
                         
                     Update_Shipping_Date(orderNumber);
                     System.out.println("Shipping of order ID : "+orderNumber);
                 
                 }       
        }
    }

    @Override
    
    // implementing Issue_reorders interface
    public int Issue_reorders(int year, int month, int day) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
         int supplier_count = 0;
        try{
                Class.forName("com.mysql.jdbc.Driver"); 
                 
                // setting user name, password , database to accsess database connection
                MyIdentity.setIdentity( identity );
                
                user = identity.getProperty("user");
                password = identity.getProperty("password");
                database = identity.getProperty("database");
                System.out.println(password);   
        
                // connecting t driver
            connect=DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306", user , password);
                
            String date = year+"-"+month+"-"+day;
            Statement st = connect.createStatement();
            st.executeQuery("use "+database+";"); 
            
            //  takinh product details according to given date
            ResultSet distinct_supplierID = Obtain_Connection().createStatement().executeQuery("SELECT DISTINCT new_temp.SupplierID from "
                + "(SELECT products.SupplierID,products.ProductID,TEMP.reordered_units from products , "
                + "(SELECT * from history where Day='"+date+"') AS TEMP WHERE  TEMP.ProductID = products.ProductID\n" +
                ") AS new_temp;");
               
                // fetching product details according to supplier details
            while(distinct_supplierID.next()){
                int SupplierID = distinct_supplierID.getInt(1);
                supplier_count++;
                ResultSet records   =  Obtain_Connection().createStatement().executeQuery("SELECT products.SupplierID,products.ProductID,TEMP.reordered_units from products , "
                        + "(SELECT * from history where Day='"+date+"') AS TEMP WHERE  TEMP.ProductID = products.ProductID and products.SupplierID = '"+SupplierID+"';");
                    
                
                // inserting data into supplier_products_catalog table on which product issue reorder is called
                  while(records.next()){
                      ResultSet UnitPrice = Obtain_Connection().createStatement().executeQuery("select UnitPrice from products where ProductID='"+records.getInt(2)+"';");
                            String transcation_ID = Integer.toString(year);
                            //transcation_ID.concat(Integer.toString(month));
                           // transcation_ID.concat(Integer.toString(day));
                           // transcation_ID.concat(Integer.toString(SupplierID));
                            //transcation_ID = transcation_ID+Integer.toString(year);
                            transcation_ID = transcation_ID+Integer.toString(month);
                            transcation_ID = transcation_ID+Integer.toString(day);
                            transcation_ID = transcation_ID+Integer.toString(SupplierID);
                            
                            UnitPrice.next();
                            double unitprice = UnitPrice.getDouble(1);
                            unitprice =  unitprice / 1.15;
                            double totalprice = unitprice * records.getInt(3);
                            Obtain_Connection().createStatement().execute("insert into supplier_products_catalog(ProductID,SupplierID,ProductCost,TransationID,Quantity_ordered,DeliveryDate)"
                                     +"values('"+records.getInt(2)+"','"+SupplierID+"','"+unitprice+"','"+transcation_ID+"','"+records.getInt(3)+"','"+date+"');");
                             System.out.println("Order Placed...");
                         //   System.out.println(transcation_ID);
                            
                            
                    }
            }
        
        
        
        }catch(Exception e){   System.out.println(e.getMessage());       }
        
        return supplier_count;    
    }

   // to check if the delivery date is null or not for issued product for reorder 
    boolean Check_delivery_status(int internal_order_reference){
          
        boolean status = false;
        
        try{
            Statement st = connect.createStatement();
            st.executeQuery("use "+database+";");
                
            ResultSet rs = Obtain_Connection().createStatement().executeQuery("select * from supplier_products_catalog where TransationID = '"+internal_order_reference+"';");
               
                if(rs.getDate("DeliveryDate") == null){
                      
                    status = true;
                }
                
        }catch(Exception e){
        
          System.out.println(e.getMessage());
        }
        return status;
    }
    
    // updating the table when oder is recieved 
    boolean OnRecieve_Update_inv(int internal_order_reference){
        boolean status = false;
        java.util.Date date=new java.util.Date();
	java.sql.Date sqlDate=new java.sql.Date(date.getTime());    
        
        
        try{
            Statement st = connect.createStatement();
            st.executeQuery("use "+database+";");
            
            Obtain_Connection().createStatement().execute("update into supplier_products_catalog set DeliveryDate ='"+sqlDate+"' where TransationID='"+internal_order_reference+"');");
            //Adding to Stock
            
            //ResultSet rs = Obtain_Connection().createStatement().executeQuery("select ProductID from supplier_products_catalog  whete TransationID= '"+internal_order_reference+"';");
            //rs.next();
            //Obtain_Connection().createStatement().execute("select units,UnitsOnOrder,ReorderLevel from products  where ProductID = '"+rs.getInt(1)+"';");
            
            ResultSet rs1 = Obtain_Connection().createStatement().executeQuery("select * from supplier_products_catalog where internal_order_reference= '"+internal_order_reference+"';");
            
            while(rs1.next()){
                int Quantity_ordered = rs1.getInt("Quantity_ordered");
                ResultSet rs2 = Obtain_Connection().createStatement().executeQuery("select UnitsInStock,UnitsOnOrder,ReorderLevel from products where ProductID = '"+rs1.getInt("ProductID")+"';");
                int UnitsInStock = rs2.getInt("UnitsInStock");
                int UnitsOnOrder = rs2.getInt("UnitsOnOrder");
                
                
                if(Quantity_ordered < UnitsOnOrder){
                
                    UnitsOnOrder = UnitsOnOrder - Quantity_ordered;
                    UnitsInStock = UnitsInStock + Quantity_ordered;
                    
                    Obtain_Connection().createStatement().execute("update into products set UnitsOnOrder = '"+UnitsOnOrder+"',UnitsInStock ='"+UnitsInStock+"';");
                  status = true;
                }
                else{
                
                 throw new OrderException(internal_order_reference,"not in stock");
                
                }
                
            }
        }catch(Exception e){
        
          System.out.println(e.getMessage());
        }
        
        return status;
    }
    
    
    @Override
        // implementing  recieve_order method 
    public void Recieve_order(int internal_order_reference) throws OrderException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
           
        if(Check_delivery_status(internal_order_reference) == false){
        
           throw new OrderException(internal_order_reference,"receive");
        
        }
        
        else{
        
                    if(OnRecieve_Update_inv(internal_order_reference)){
                    
                         System.out.println("Order with Transcation ID : "+internal_order_reference+"recieved Successfully!!");
                    
                    }
        
        }
        
            //Obtain_Connection().createStatement().execute("select * from supplier_product_catalog;");
            //Obtain_Connection().createStatement().execute("insert into supplier_product_catalog(DeliveryDate,DeliveryStatus) values('"+sqlDate+"','"+true+"');");
             
        
    }
    
}
