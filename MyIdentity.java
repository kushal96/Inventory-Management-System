/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6;

/**
 *
 * @author Nikita
 */
import java.util.Properties;

// A simple helper class that contains all of the properties to identify and access the
// database.

// In an ideal world, all of this information comes from a properties file instead.

public class MyIdentity {

    public static void setIdentity(Properties prop) {
      prop.setProperty("database", "nvpatel");
      prop.setProperty("user", "nvpatel");
      prop.setProperty("password", "B00826639");
    }
}