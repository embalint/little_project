/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.obrade;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.embalint.web.ConfigReader;
import org.foi.nwtis.embalint.web.Registry;

/**
 *
 * @author Emil
 */
public class SocketServerObrada extends Thread{
    private ServerSocket serverSocket = null;
    ConfigReader cr = (ConfigReader) Registry.getInstance().get("ConfigReader");
    private boolean serverStop=false;
    boolean serverRecived=false;
    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
           try {
            System.out.println(cr.getPort());
            serverSocket = new ServerSocket(Integer.parseInt(cr.getPort()));
            serverSocket.setSoTimeout(1000);
            Socket socket = null;
            System.out.println("Socket server pokrenut...");

            while (!serverStop) {
                           
                try {
                    socket = serverSocket.accept();
                    if(socket.isConnected()){
                       serverRecived = true;   
                    }
                } catch (SocketTimeoutException ex) {
                
                }
                
                if (serverRecived == true) {
                    PozadinskaDretvaObrada pdo= new PozadinskaDretvaObrada(socket);
                    pdo.start();
                   
                }
            }
            
            System.out.println("Prekidam rad socket servera");
            serverSocket.close();

        } catch (IOException ex) {
            Logger.getLogger(SocketServerObrada.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
