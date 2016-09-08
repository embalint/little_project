/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.obrade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.embalint.rest.klijenti.GMKlijent;
import org.foi.nwtis.embalint.rest.klijenti.OWMKlijent;
import org.foi.nwtis.embalint.rest.klijenti.OWMKlijentForecast;
import org.foi.nwtis.embalint.web.ConfigReader;
import org.foi.nwtis.embalint.baza.DatabaseConnect;
import org.foi.nwtis.embalint.web.Registry;
import org.foi.nwtis.embalint.web.podaci.Adresa;
import org.foi.nwtis.embalint.web.podaci.Lokacija;
import org.foi.nwtis.embalint.web.podaci.MeteoPodaci;

/**
 *
 * @author Emil
 */
public class MeteoPodaciObrada extends Thread {

    boolean paused = false;
    boolean sleeping = false;

    public boolean isSleeping() {
        return sleeping;
    }

    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }
    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    ConfigReader cr = (ConfigReader) Registry.getInstance().get("ConfigReader");
    String apiKey;

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {

        if (!paused) {

            List<Adresa> adrese = getAllAdreses(); // napravi metodu
            apiKey = cr.getApiKey();
            OWMKlijent owmk = new OWMKlijent(apiKey); // procitat key registry

            for (Adresa a : adrese) {
                MeteoPodaci mp = null;
                System.out.println(a.getGeoloc().getLatitude());
                try{
                mp = owmk.getRealTimeWeather(a.getGeoloc().getLatitude(),a.getGeoloc().getLongitude());
                saveDataDB(a, mp);
                }
                catch(Exception e){
                    System.out.println("Javila se greška: "+e+" prilikom dohvaćanja");
                }
                OWMKlijentForecast owf = new OWMKlijentForecast(apiKey);
                try{
                List<MeteoPodaci> listaPodataka = owmk.getForecastWeather(a.getGeoloc().getLatitude(),a.getGeoloc().getLongitude());
                saveForecastDataDB(a,listaPodataka);
                }
                catch(Exception e){
                    System.out.println("Javila se greška: "+e+" prilikom dohvaćanja");
                }

            }

            try {
                sleep(Integer.parseInt(cr.getThreadInterval())*1000);
                //System.out.println(new Date() + "Preuzeti meteo podaci");
            } catch (InterruptedException ex) {
                Logger.getLogger(MeteoPodaciObrada.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    private List<Adresa> getAllAdreses() {

        List<Adresa> lista = new ArrayList<>();
        Adresa ad = new Adresa();
        Statement stmt;
        try {
            stmt = DatabaseConnect.dbConncect().createStatement();
            String sqlDohvatiPodatke = "SELECT * FROM EMBALINT_ADRESE";
            ResultSet aa = stmt.executeQuery(sqlDohvatiPodatke);

            while (aa.next()) {
                int idAdrese = Integer.parseInt(aa.getString("IDADRESA"));
                String adres = aa.getString("ADRESA");
                Lokacija lokacija = new Lokacija(aa.getString("LATITUDE"), aa.getString("LONGITUDE"));
                ad = new Adresa(idAdrese, adres, lokacija);
                lista.add(ad);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MeteoPodaciObrada.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lista;
    }

    private void saveDataDB(Adresa a, MeteoPodaci mp) {       
        try {      
                    Statement stmt = DatabaseConnect.dbConncect().createStatement();
                    String dbEnter = "insert into EMBALINT_METEO (IDADRESA,ADRESASTANICE,LATITUDE,LONGITUDE,"
                            + "VRIJEME,VRIJEMEOPIS,TEMP,TEMPMIN,TEMPMAX,VLAGA,TLAK,VJETAR,VJETARSMJER) VALUES "
                            + "(" + Integer.parseInt(Long.toString(a.getIdadresa())) + ",'"
                            + a.getAdresa() + "','"
                            + a.getGeoloc().getLatitude() + "','"
                            + a.getGeoloc().getLongitude()+ "','"
                            + mp.getWeatherNumber() + "','"
                            + mp.getWeatherValue() + "',"
                            + mp.getTemperatureValue() + ","
                            + mp.getTemperatureMin() + ","
                            + mp.getTemperatureMax() + ","
                            + mp.getHumidityValue() + ","
                            + mp.getPressureValue() + ","
                            + mp.getWindSpeedValue() + ","
                            + mp.getWindDirectionValue() + ")";
                    
                    stmt.executeUpdate(dbEnter);
                    System.out.println("Dretva je spremila dohvacene podatke u bazu");
                
            } catch (SQLException ex) {
                Logger.getLogger(MeteoPodaciObrada.class.getName()).log(Level.SEVERE, null, ex);
            }
         

    }

    private void saveForecastDataDB(Adresa a, List<MeteoPodaci> listaPodataka) {
        int counter=0;
        while (listaPodataka.size()>counter){
         try {      
                    Statement stmt = DatabaseConnect.dbConncect().createStatement();
                    String dbEnter = "insert into EMBALINT_METEO_PROGNOSTICAR (IDADRESA,ADRESASTANICE,LATITUDE,LONGITUDE,"
                            + "VRIJEME,VRIJEMEOPIS,TEMP,TEMPMIN,TEMPMAX,VLAGA,TLAK,VJETAR,VJETARSMJER,DATUM) VALUES "
                            + "(" + Integer.parseInt(Long.toString(a.getIdadresa())) + ",'"
                            + a.getAdresa() + "','"
                            + a.getGeoloc().getLatitude() + "','"
                            + a.getGeoloc().getLongitude()+ "','"
                            + listaPodataka.get(counter).getWeatherNumber() + "','"
                            + listaPodataka.get(counter).getWeatherValue() + "',"
                            + listaPodataka.get(counter).getTemperatureValue() + ","
                            + listaPodataka.get(counter).getTemperatureMin() + ","
                            + listaPodataka.get(counter).getTemperatureMax() + ","
                            + listaPodataka.get(counter).getHumidityValue() + ","
                            + listaPodataka.get(counter).getPressureValue() + ","
                            + listaPodataka.get(counter).getWindSpeedValue() + ","
                            + listaPodataka.get(counter).getWindDirectionValue() + ",'"
                            + listaPodataka.get(counter).getDateForecast()+ "')";
                    System.out.println(dbEnter);
                    System.out.println("datum"+listaPodataka.get(counter).getDateForecast());
                    stmt.executeUpdate(dbEnter);
                    counter++;
                
            } catch (SQLException ex) {
                Logger.getLogger(MeteoPodaciObrada.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
        System.out.println("Dretva je spremila dohvacene podatke u bazu za 5 dana");
    }

}
