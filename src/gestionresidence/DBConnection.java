/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestionresidence;

import java.sql.Connection;
import java.sql.DriverManager;
/**
 *
 * @author charlyrenard
 */
public class DBConnection {
    
    static Connection conn = null;
    
    public static Connection getConnection(){
        
        // URL de la base de données
        String url = "jdbc:postgresql://localhost:5432/gestion_residence";
        // Nom d'utilisateur et mot de passe
        String user = "postgres";
        String password = "Reinge";
        
        try {
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection(url, user, password);
               
            } catch (Exception e) {
                e.printStackTrace();
            }
        return conn;
    }
    
}
