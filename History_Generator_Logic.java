/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6;


import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author Nikita Patel
 */
public class History_Generator_Logic 
{
    
    static int history_array[][];
    static int reorder_level,reorder_units;
    static int day_end_inv,day_start_inv,sales_day_end_inv;
    int ProductID;
  
    static  Properties identity = new Properties();
    static MyIdentity me = new MyIdentity();

	static String user;
	static String password;
	static String database;
        static Connection connect;
           
        public History_Generator_Logic(int ProductID)
        {
           this.ProductID = ProductID;
           
        }
        
        public static void main(String args[]) throws ClassNotFoundException, SQLException{
            
           generate_database_history();
        }
           
        
           // method to create new table and to call other method to create history 
        public static void generate_database_history() throws ClassNotFoundException, SQLException
        {
           
                Class.forName("com.mysql.jdbc.Driver"); 
                 
                // setting user name, password , database to accsess database connection
                MyIdentity.setIdentity( identity );
                
                user = identity.getProperty("user");
                password = identity.getProperty("password");
                database = identity.getProperty("database");
                //System.out.println(password);   
        
                // connecting t driver
                connect=DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306", user , password);
            
            
                // resultset object to store database fatchd from server database
                ResultSet resultset = null ;
                Statement st = connect.createStatement();
            
                try {
                    
                        // selecting a databse
                    st.executeQuery("use "+database+";");
            
                        // to delete the previously created table if it exists
                    st.execute("drop table if exists history;");
                    //System.out.println("drop table");
                        
                        // to get the unique productID from products table
                    resultset = st.executeQuery("select distinct ProductID from products order by ProductID;;");
            
                        // this function will create new table to store history data
                    make_table();
                
                    while(resultset.next()){
                        //System.out.println(resultset.getInt(1));
                        create_purchase_histroy(resultset.getInt(1));
                    } 
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                } finally {
            // Always close connections, otherwise the MySQL database runs out of them.

            // Close any of the resultSet, statements, and connections that are open and holding resources.
                    try {
                        if (resultset != null) {
                        resultset.close();
                        }
    
                        if (st != null) {
                        st.close();
                        }
    
                        if (connect != null) {
                        connect.close();
                        }
                    } catch (Exception e) {
                    System.out.println(e.getMessage());
                    }
                }
        }
           
            //  method to generate history and to store data in databse 
        public static void create_purchase_histroy(int ProductID) throws ClassNotFoundException, SQLException
        {
         
         // int history_array[][];
            int SDI, day_sales; 
       
            Class.forName("com.mysql.jdbc.Driver"); 
                 
                // setting user name, password , database to accsess database connection
                MyIdentity.setIdentity( identity );
                
                user = identity.getProperty("user");
                password = identity.getProperty("password");
                database = identity.getProperty("database");
                //System.out.println(password);   
        
                // connecting t driver
                connect=DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306", user , password);
            
            
            ResultSet rs = null;
            Statement stmt  = connect.createStatement();
            Statement stmt1 = connect.createStatement();
            
            
            
            try
            {
                    // selecting database
                stmt.executeQuery("use "+database+";");

    
                rs =  stmt.executeQuery("SELECT * FROM (SELECT orders.ShippedDate, SUM(orderdetails.Quantity) \n" +
                                        "from orders,orderdetails WHERE orders.OrderID = orderdetails.OrderID \n" +
                                        "AND orders.ShippedDate <> 'NULL' and orderdetails.ProductID = '"+ProductID +"' GROUP BY orders.ShippedDate ) AS new_join_result  \n" +
                                         "ORDER BY new_join_result.ShippedDate DESC;");
            
       
                // calling the determine_units method to set values according to    the conditions for reorder_level , sales_day_end_inv ,  reorder_units  columns initial data
                int ordering_units = determine_units(ProductID,connect);
                
                int no_of_records = 0;
     
                //System.out.println(ordering_units+"\n");     
       
                // no_of_records = determine_records(ProductID,connect);
                //System.out.println(no_of_records+"\n");
            
                // System.out.println(reorder_level+"\n");
      
                // System.out.println("\n"+"Day"+" "+"DEI"+" "+"RU"+" "+"SDI"+" "+"DS"+" "+"DSI");
        
                    // to take the 1st entry form resultset
                rs.next();
            
                    // adding reorders units and sales day end invantory to find day end invantory 
                day_end_inv = reorder_units + sales_day_end_inv;
                    // day sales is equal to total quantity 
                day_sales = rs.getInt(2);
            
                    // day start invantory is equal to sales day end invantory plus day sales 
                day_start_inv = sales_day_end_inv + day_sales;
                
                    // inserting data into history table only when reorder is called 
                if(reorder_units!=0){
                    stmt1.execute("insert into history(ProductID,Day ,day_end_inventory,reordered_units,sales_day_end_inv ,day_sales,day_start_inv) "
                    + "values('"+ProductID+"','"+rs.getDate(1)+"','"+day_end_inv+"','"+reorder_units+"','"+sales_day_end_inv+"','"+day_sales+"','"+day_start_inv+"');");          
                }          
           
                // after 1st entry for the next days day start invantory is equal to previous day's day end invantory
                day_end_inv = day_start_inv;
        
                while(rs.next()){
          
                    // when units are greter then 4*reorder level 
                    if (day_end_inv >= ordering_units){
                        reorder_units = day_end_inv - reorder_level;
                    }
          
                    else{
                        reorder_units = 0 ;
                        ordering_units = 4 *reorder_level;
                    }
           
                    // calculating sales day end invantory 
                    sales_day_end_inv = day_end_inv - reorder_units  ;
          
                    day_sales = rs.getInt(2);
         
                    day_start_inv  = sales_day_end_inv + day_sales;
                     
                    //inserting data into history table only when reorder is called
                    if(reorder_units!=0){       
                        stmt1.execute("insert into history(ProductID,Day ,day_end_inventory,reordered_units,sales_day_end_inv ,day_sales,day_start_inv) "
                        + "values('"+ProductID+"','"+rs.getDate(1)+"','"+day_end_inv+"','"+reorder_units+"','"+sales_day_end_inv+"','"+day_sales+"','"+day_start_inv+"');");
                    }

                    day_end_inv = day_start_inv;
                    reorder_units = 0;
          
                }
            }catch(Exception e) {
                System.out.println(e.getMessage());
            } finally {
            // Always close connections, otherwise the MySQL database runs out of them.

            // Close any of the resultSet, statements, and connections that are open and holding resources.
                try {
                    if (rs != null) {
                    rs.close();
                    }
                    if (connect != null) {
                    connect.close();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }   
            }
        }
     
            // to set diffrent parameters values according to the conditions
        static int determine_units(int ProductID,Connection connect) throws SQLException
        {
        
            int no_of_units = 0;  
            Statement stmt =connect.createStatement();
         
                // taking units In stock , units on order and reorder level values from products table   
            ResultSet rs= stmt.executeQuery("SELECT products.UnitsInStock,products.UnitsOnOrder,products.ReorderLevel "
                    + "FROM products WHERE products.ProductID = '"+ProductID+"';");
            
                // taking 1st entry
            rs.next();
           
            int UnitsInStock = rs.getInt(1);
            int UnitsOnOrder = rs.getInt(2);
            int ReorderLevel = rs.getInt(3);
           
            reorder_level = ReorderLevel;
            sales_day_end_inv = UnitsInStock;
            reorder_units = UnitsOnOrder;
            
            //System.out.println("\n Units on Order  : "+UnitsOnOrder);
           
          // System.out.println(UnitsInStock+" "+UnitsOnOrder+" "+ReorderLevel);
          
                // when units in order value is not 0
            if(UnitsOnOrder > 0 ){
                no_of_units = UnitsOnOrder;
            }
            else{
                    // when unitson order value is zero but reorder level is not 0
                if(ReorderLevel != 0 && UnitsOnOrder == 0 ){
                    no_of_units = 4 * ReorderLevel; 
                }
                    // when all the three values are 0
                else if(UnitsInStock ==0 && UnitsOnOrder == 0 && ReorderLevel==0){
                    
                    reorder_level = 20;
                    
                    if(UnitsOnOrder == 0){
                      no_of_units = 4 * reorder_level;      
                    }   
                }
                    // for any other conditions
                else {
                    ReorderLevel = (1/4) * UnitsInStock;
                    reorder_level = ReorderLevel;
                } 
                              
                              // no_of_units = UnitsInStock;
               
            }          
           return no_of_units;
        }
    
      
            // to create new table in database
        public static void make_table() throws ClassNotFoundException, SQLException{
            
            
            Class.forName("com.mysql.jdbc.Driver"); 
                 
                // setting user name, password , database to accsess database connection
                MyIdentity.setIdentity( identity );
                
                user = identity.getProperty("user");
                password = identity.getProperty("password");
                database = identity.getProperty("database");
                //System.out.println(password);   
        
                // connecting t driver
                connect=DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306", user , password);
             
        
            Statement st = connect.createStatement();
          
        
            try{
                st.executeQuery("use "+database+";");
                ResultSet rs = st.executeQuery("select * from history;");
            }catch(Exception e){
                
                    // to create new history table 
                boolean x = st.execute("create table history( ProductID int(11),Day date,day_end_inventory int(11),reordered_units int(11),"
                        + "sales_day_end_inv int(11),day_sales int(11),day_start_inv int(11));");
                if(x==true){
                     //  fill_data(history,ProductID);
                }
            }finally {
            // Always close connections, otherwise the MySQL database runs out of them.

            // Close any of the resultSet, statements, and connections that are open and holding resources.
            try {                
                if (st != null) {
                    st.close();
                }
    
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        }
   
}
