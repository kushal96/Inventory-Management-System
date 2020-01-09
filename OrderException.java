/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6;

/**
 *
 * @author Nikita Patel
 */
class OrderException extends Exception {
    int OrderID;
    String label;
    public OrderException(int OrderID,String label){
       this.OrderID = OrderID;
       this.label = label;
    }
    
    @Override
    public String getMessage(){
       
         String Message = null;
         
         if(label.equals("shipped")){
           Message =  "Exception : Can't place Order ..Order Already left from company of Order Number : ";
         }
         if(label.equals("receive")){
             Message =  "Exception : Can't recieve again ..Order Already recieved  to company  ";
         }
         if(label.equals("stock")){
             Message =  "Exception : Can't place ..Order no stock left in inentory  ";
         }
         if(label.equals("not in stock")){
             Message =  "Exception : Can't recieve ..Order units on order dose not match with quantity ordered  ";
         }
         return Message;
    }
    public int getReference(){
    
      return OrderID;
    }
}
