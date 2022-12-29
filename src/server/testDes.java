/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import server.utils.DES;

/**
 *
 * @author devtry
 */
public class testDes {
    public static void main(String[] args) {
//         String enhoten = DES.DES_CBC_Encrypt("cong nghe thong tin cong nghe th", "12345678");
         String enhoten = DES.DES_CBC_Encrypt("10", "12345678");
         System.out.println("Encrypt : "+ enhoten);
         String dehoten = DES.DES_CBC_Decrypt(enhoten, "12345678");
         System.out.println("Decrypt : "+ dehoten);
    }
}
