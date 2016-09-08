/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.embalint.konfiguracije.Konfiguracija;
import org.foi.nwtis.embalint.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.embalint.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.embalint.web.slusaci.SlusacAplikacije;

/**
 * Klasa koja sluzi za čitanje konfiguracijske datoteke, koju spremamo u singleton hashmapu tako da 
 * ju mozemo dohvatiti u bilo kojoj klasi bez novog citanja datoteke
 * @author Emil
 */
public class ConfigReader {

    Konfiguracija konf;
    private String apiKey,username,userPass,serverDb,adminUser,adminPass,adminDb,userDb,threadInterval;
    private String server,port,mailServer,mailPort,paginacija,brojZahtjeva;
;
    public String getBrojZahtjeva() {
        return brojZahtjeva;
    }

    public void setBrojZahtjeva(String brojZahtjeva) {
        this.brojZahtjeva = brojZahtjeva;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getApiKey() {
        return apiKey;
    }

    public Konfiguracija getKonf() {
        return konf;
    }

    public String getUsername() {
        return username;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getServerDb() {
        return serverDb;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public String getAdminPass() {
        return adminPass;
    }

    public String getAdminDb() {
        return adminDb;
    }

    public String getUserDb() {
        return userDb;
    }

    public String getThreadInterval() {
        return threadInterval;
    }



     /**
     * Dodjeljuju se vrijednosti iz konfiguracijske datoteke varijablama za koje postoje seteri i geteri
     *
     * @param datoteka  datoteka koju prima konstruktor iz koje se citaju konfiguracijski podaci
     * 
     * 
     */
    public ConfigReader(String datoteka) {
             try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            this.apiKey = konf.dajPostavku("opeanwather.apiKey");
            this.username = konf.dajPostavku("user.username");
            this.userPass = konf.dajPostavku("user.password");
            this.serverDb = konf.dajPostavku("server.database");
            this.adminUser = konf.dajPostavku("admin.username");
            this.adminPass = konf.dajPostavku("admin.password");
            this.adminDb = konf.dajPostavku("admin.database");
            this.userDb = konf.dajPostavku("user.database");
            this.threadInterval = konf.dajPostavku("dretva.interval");
            this.server=konf.dajPostavku("server");
            this.port = konf.dajPostavku("port");
            this.brojZahtjeva = konf.dajPostavku("user.kvota.kategorija");
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
}
