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

public interface inventoryControl{
public void Ship_order(int orderNumber) throws OrderException;
public int Issue_reorders(int year,int month,int day);
public void Recieve_order(int internal_order_reference) throws OrderException;
}


