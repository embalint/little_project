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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.foi.nwtis.embalint.rest.serveri.MeteoRESTResourceContainer;
import org.foi.nwtis.embalint.web.ConfigReader;
import org.foi.nwtis.embalint.baza.DatabaseConnect;
import org.foi.nwtis.embalint.baza.DnevnikSpremanje;
import org.foi.nwtis.embalint.web.HelpForMedeoPodaci;
import org.foi.nwtis.embalint.web.Registry;
import org.foi.nwtis.embalint.web.podaci.Adresa;
import org.foi.nwtis.embalint.web.podaci.Lokacija;
import org.foi.nwtis.embalint.ws.serveri.GeoMeteoWS;

/**
 * REST Web Service
 *
 * @author Emil
 */
public class AdreseResource {

    private String user, pass;
    DnevnikSpremanje dnevnik;
    int startTime, endTime;
    /**
     * Creates a new instance of AdreseResource
     */
    private AdreseResource(String user, String pass) {

        this.user = user;
        this.pass = pass;
    }

    /**
     * Get instance of the AdreseResource
     */
    public static AdreseResource getInstance(String user, String pass) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of AdreseResource class.
        return new AdreseResource(user, pass);
    }

    /**
     * Retrieves representation of an instance of
     * org.nwtis.embalint.restovi.AdreseResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() { 
        startTime = (int) System.currentTimeMillis();
        Date datum = new Date();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();

        int authentication = userAuth(user, pass);
        int brojZahtjeva = brojZahtjeva(user, pass);
        int brojOdradenihZahtejva = odradeniZahtjevi(user, pass);
        if (authentication == 0) {
            job.add("Niste se dobro autenticirali", jab);
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Lista adresa REST");
                        dnevnik.setResult("Kriva prijava");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(AdreseResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return job.build().toString();

        } else if (brojOdradenihZahtejva < brojZahtjeva) {
            try {
                boolean hasResult = false;
                List<Adresa> adrese = new ArrayList<>();
                Statement stmt;
                String upit = "SELECT * FROM embalint_adrese";
                stmt = DatabaseConnect.dbConncect().createStatement();
                ResultSet aa = stmt.executeQuery(upit);
                while (aa.next()) {
                    hasResult = true;
                    Adresa a = new Adresa(Long.parseLong(aa.getString("IDADRESA")), aa.getString("ADRESA"),
                            new Lokacija(aa.getString("LATITUDE"), aa.getString("LONGITUDE")));
                    adrese.add(a);
                }

                for (Adresa a : adrese) {
                    try {
                        JsonObjectBuilder jObject = Json.createObjectBuilder();
                        jObject.add("id", Long.toString(a.getIdadresa()));
                        jObject.add("adresa", a.getAdresa());
                        jObject.add("lat", a.getGeoloc().getLatitude());
                        jObject.add("lon", a.getGeoloc().getLongitude());
                        jab.add(jObject);

                    } catch (Exception ex) {
                        Logger.getLogger(MeteoRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (hasResult) {
                    dodajIskoristenZahtjev(user, pass);
                    job.add("Popis svih adresa" + pass, jab);
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Lista adresa REST");
                        dnevnik.setResult("Uspješno");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(AdreseResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                } else {
                   job.add("Neuspjesno vracanje adresa", jab);
                   endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Lista adresa REST");
                        dnevnik.setResult("Neuspješno");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(AdreseResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return job.build().toString();
            } catch (SQLException ex) {
                Logger.getLogger(MeteoRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            job.add("Premasili ste kvotu zahtjeva", jab);
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Lista adresa REST");
                        dnevnik.setResult("Korisnik nema zahtjeve");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(AdreseResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return job.build().toString();
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
     * PUT method for updating or creating an instance of AdreseResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource AdreseResource
     */
    @DELETE
    public void delete() {
    }
}
