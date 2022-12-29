package server;

import java.sql.*;
import javax.swing.*;

public class MyConnection {

    Connection con = null;
    
    public Connection getConnection(String username, String password) {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
//            String URL = "jdbc:mysql://localhost/"+database+"?useTimezone=true&serverTimezone=UTC";
             String URL = "jdbc:mysql://127.0.0.1:3306/QuanLySinhVien";
             System.out.println("username : "+ username);
            System.out.println("password : "+ password);
            Connection con = DriverManager.getConnection(URL, username,password);
            return con;
            
//            System.out.println("username "+username);
//            System.out.println("password "+password);
//            Class.forName("com.mysql.jdbc.Driver");
//            String URL = "jdbc:mysql://127.0.0.1:3306/QuanLySinhVien"; 
//            Connection con = DriverManager.getConnection(URL,username,password);
//            return con;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "Loi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

}
