/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.baza;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Emil
 */
public class DnevnikSpremanje {
    String ipAdress,user,operation,result,time;
    int duration;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

 
    
    public void spremiDnevnik(){
         try {
            Statement stmt = DatabaseConnect.dbConncect().createStatement();
                  String dbEnter = "insert into EMBALINT_DNEVNIK (IPADRESA,KORISNIK,OPERACIJA,REZULTAT,"
                            + "TRAJANJE,VRIJEME) VALUES "
                            + "('" + ipAdress + "','"
                            + user + "','"
                            + operation + "','"
                            + result+ "',"
                            + duration + ",'"
                            + time + "')";
                    System.out.println("sql prikaz"+ dbEnter);
                    stmt.executeUpdate(dbEnter);
                    System.out.println("Podaci su zapisani u dnevnik");

           
        } catch (SQLException ex) {
            Logger.getLogger(DnevnikSpremanje.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
}
