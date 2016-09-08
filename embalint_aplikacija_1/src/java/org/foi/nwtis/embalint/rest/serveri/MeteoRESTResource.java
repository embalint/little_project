/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.rest.serveri;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.embalint.web.ConfigReader;
import org.foi.nwtis.embalint.baza.DatabaseConnect;
import org.foi.nwtis.embalint.baza.DnevnikSpremanje;
import org.foi.nwtis.embalint.web.Registry;
import org.foi.nwtis.embalint.ws.serveri.GeoMeteoWS;

/**
 * REST Web Service Rest web servis koji vraća meteo podatke prema ID-u koji se
 * zadaje u URL-u servisa
 *
 * @author Emil
 */
public class MeteoRESTResource {

    private String id,user,pass;
    DnevnikSpremanje dnevnik;
    int startTime, endTime;
    /**
     * Creates a new instance of MeteoRESTResource
     */
    private MeteoRESTResource(String id,String user, String pass) {
        this.id = id;
        this.user=user;
        this.pass=pass;
    }

    /**
     * Get instance of the MeteoRESTResource
     */
    public static MeteoRESTResource getInstance(String id,String user, String pass) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of MeteoRESTResource class.
        return new MeteoRESTResource(id,user, pass);
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.embalint.rest.serveri.MeteoRESTResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        startTime = (int) System.currentTimeMillis();
        Date datum = new Date();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder jobRoot = Json.createObjectBuilder();
        JsonObjectBuilder jobMeteo = Json.createObjectBuilder();

        int authentication = userAuth(user, pass);
        int brojZahtjeva = brojZahtjeva(user, pass);
        int brojOdradenihZahtejva = odradeniZahtjevi(user, pass);
        if (authentication == 0) {
            jobRoot.add("Niste se dobro autenticirali", jab);
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Trenutna prognoza REST");
                        dnevnik.setResult("Kriva prijava");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(AdreseResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return jobRoot.build().toString();

        } else if (brojOdradenihZahtejva < brojZahtjeva) {
        try {
            boolean hasResult = false;
            String lastDataQuery = "Select  *from embalint_meteo_prognosticar WHERE IDADRESA = " + id + " ORDER BY IDMETEO DESC FETCH NEXT 8 ROWS ONLY";
            Statement stmt = DatabaseConnect.dbConncect().createStatement();
            ResultSet aa = stmt.executeQuery(lastDataQuery);
            while (aa.next()) {
                 hasResult = true;
                jobMeteo.add("Adresa ", aa.getString("ADRESASTANICE"));
                jobMeteo.add("Temperatura", aa.getString("TEMP"));
                jobMeteo.add("Temperatura minimum", aa.getString("TEMPMIN"));
                jobMeteo.add("Temperatura maximum", aa.getString("TEMPMAX"));
                jobMeteo.add("Tlak", aa.getString("TLAK"));
                jobMeteo.add("Vjetar", aa.getString("VJETAR"));
                jobMeteo.add("Vrijeme", aa.getString("VRIJEME"));
                jobMeteo.add("Vrijeme opis", aa.getString("VRIJEMEOPIS"));
                jobMeteo.add("Vjetar smjer", aa.getString("VJETARSMJER"));
                jobMeteo.add("Vlaga", aa.getString("VLAGA"));
                jab.add(jobMeteo);
            }
             if (hasResult) {
                    dodajIskoristenZahtjev(user, pass);
                    jobRoot.add("Prikaz zadnje dohvacenih meteo podataka iz baze za adresu" , jab);
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Trenutna prognoza REST");
                        dnevnik.setResult("Uspješno");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(AdreseResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                } else {
                    jobRoot.add("Neuspjesno dohvaceni podaci" , jab);
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Trenutna prognoza REST");
                        dnevnik.setResult("Neuspješno");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(AdreseResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            
            return jobRoot.build().toString();
        } catch (SQLException ex) {
            Logger.getLogger(MeteoRESTResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        else {
            jobRoot.add("Premasili ste kvotu zahtjeva", jab);
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Trenutna prognoza REST");
                        dnevnik.setResult("Nedovoljno zahtjeva");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(AdreseResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return jobRoot.build().toString();
        }
        return null;
    
    }
     private int userAuth(String korisnik, String lozinka) {

        try {
            Statement stmt = DatabaseConnect.dbConncect().createStatement();
            String sqlUpit = "SELECT ULOGA FROM embalint_korisnici "
                    + "WHERE korisnicko_ime='" + korisnik + "' AND password='" + lozinka + "'";
            System.out.println(sqlUpit);
            ResultSet aa = stmt.executeQuery(sqlUpit);
            if (aa.next()) {
                int uloga = Integer.parseInt(aa.getString("ULOGA"));
                return uloga;
            }

        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    private int brojZahtjeva(String korisnik, String lozinka) {
        ConfigReader cr = (ConfigReader) Registry.getInstance().get("ConfigReader");
        try {
            Statement stmt = DatabaseConnect.dbConncect().createStatement();
            String sqlUpit = "SELECT *FROM embalint_korisnici "
                    + "WHERE korisnicko_ime='" + korisnik + "' AND password='" + lozinka + "'";
            System.out.println(sqlUpit);
            ResultSet aa = stmt.executeQuery(sqlUpit);
            if (aa.next()) {
                int kategorija = Integer.parseInt(aa.getString("KATEGORIJA"));
                int brojZahtjeva = kategorija * Integer.parseInt(cr.getBrojZahtjeva());

                return brojZahtjeva;
            }

        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    private int odradeniZahtjevi(String korisnik, String lozinka) {
        try {
            Statement stmt = DatabaseConnect.dbConncect().createStatement();
            String sqlUpit = "SELECT *FROM embalint_korisnici "
                    + "WHERE korisnicko_ime='" + korisnik + "' AND password='" + lozinka + "'";
            System.out.println(sqlUpit);
            ResultSet aa = stmt.executeQuery(sqlUpit);
            if (aa.next()) {
                int odradeniZahtjevi = Integer.parseInt(aa.getString("ODRADENIZAHTJEVI"));

                return odradeniZahtjevi;
            }

        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    private void dodajIskoristenZahtjev(String korisnik, String lozinka) {
        try {
            int brojOdradenihZahtejva = odradeniZahtjevi(korisnik, lozinka);
            brojOdradenihZahtejva++;
            Statement stmt = DatabaseConnect.dbConncect().createStatement();
            String sqlUpit = "Update embalint_korisnici SET ODRADENIZAHTJEVI='" + brojOdradenihZahtejva + "' WHERE korisnicko_ime='" + korisnik + "' AND password='" + lozinka + "'";
            System.out.println(sqlUpit);
            stmt.executeUpdate(sqlUpit);

        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }  

    /**
     * PUT method for updating or creating an instance of MeteoRESTResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource MeteoRESTResource
     */
    @DELETE
    public void delete() {
    }
}
