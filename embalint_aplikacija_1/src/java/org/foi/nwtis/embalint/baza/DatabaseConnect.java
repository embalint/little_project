/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.baza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.embalint.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.embalint.web.Registry;
//import org.foi.nwtis.embalint.ws.serveri.GeoMeteoWS;

/**
 * Klasa koja sluzi za spajanje na bazu podataka tako da svaki put kada se spajamo samo pozovemo metodu dbConnect
 * @author Emil
 */
public class DatabaseConnect {
    public static Connection dbConncect() {
        BP_Konfiguracija bp_konfig = (BP_Konfiguracija) Registry.getInstance().get("BP_Konfig");
        
        String connection = bp_konfig.getServerDatabase() + bp_konfig.getUserDatabase();
        Connection conn = null;
        try {
            Class.forName(bp_konfig.getDriverDatabase(connection));
            conn = DriverManager.getConnection(connection, bp_konfig.getUserUsername(),bp_konfig.getUserPassword());
           
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return conn;
    }
    
}
