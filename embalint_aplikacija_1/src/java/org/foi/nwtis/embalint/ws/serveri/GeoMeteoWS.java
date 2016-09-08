/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.ws.serveri;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.embalint.web.ConfigReader;
import org.foi.nwtis.embalint.baza.DatabaseConnect;
import org.foi.nwtis.embalint.baza.DnevnikSpremanje;
import org.foi.nwtis.embalint.web.HelpForMedeoPodaci;
import org.foi.nwtis.embalint.web.Korisnik;
import org.foi.nwtis.embalint.web.MyComparator;
import org.foi.nwtis.embalint.web.RangList;
import org.foi.nwtis.embalint.web.Registry;
import org.foi.nwtis.embalint.web.podaci.MeteoPodaci;

/**
 * SOAP web servis koji ima 5 metoda koje vraćaju ono sto je zadano, poput svih
 * adresa, vazeće meteo podatke, zadnje spremljene podatke u bazu, sve podatke
 * iz baze, te meteo podatke za sljedecih 5 dana za zadanu adresu. Servis se
 * koristi i u aplikaciji_2 u kojoj prikazujemo dohvacene podatke putem ovog
 * servisa
 *
 * @author Emil
 */
@WebService(serviceName = "GeoMeteoWS")
public class GeoMeteoWS {

    DnevnikSpremanje dnevnik;
    int startTime, endTime;

    /**
     * Web service operation
     *
     * @return
     */
    /**
     * Web service operation
     *
     * @param numbeOFData
     * @param user
     * @param pass
     * @return
     */
    @WebMethod(operationName = "rangLista")
    public HelpForMedeoPodaci rangLista(@WebParam(name = "NumbeOFData") String numbeOFData,
            @WebParam(name = "user") final String user, @WebParam(name = "pass") final String pass) {
        startTime = (int) System.currentTimeMillis();
        Date datum = new Date();
        HelpForMedeoPodaci hmp = null;

        int authentication = userAuth(user, pass);
        int brojZahtjeva = brojZahtjeva(user, pass);
        int brojOdradenihZahtejva = odradeniZahtjevi(user, pass);
        if (authentication == 0) {
            hmp = new HelpForMedeoPodaci();
            hmp.setPoruka("Niste se dobro prijavili ");
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Rang lista adresa SOAP");
                        dnevnik.setResult("Kriva prijava");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return hmp;
        } else if (brojOdradenihZahtejva < brojZahtjeva) {
            try {
                RangList rl = null;
                boolean hasResult = false;
                hmp = new HelpForMedeoPodaci();
                List<RangList> rangList = new ArrayList<>();
                List<RangList> helpList = new ArrayList<>();
                Statement stmt = DatabaseConnect.dbConncect().createStatement();
                String sqlUpit = "SELECT *from embalint_adrese";
                System.out.println(sqlUpit);
                ResultSet aa = stmt.executeQuery(sqlUpit);

                while (aa.next()) {
                    hasResult = true;
                    int count = 0;
                    rl = new RangList();
                    String adrese = aa.getString("ADRESA");
                    rl.setAdresa(adrese);
                    Statement stmt2 = DatabaseConnect.dbConncect().createStatement();
                    String sqlUpit2 = "SELECT *from embalint_meteo where adresastanice='" + adrese + "'";
                    ResultSet numberAdres = stmt2.executeQuery(sqlUpit2);
                    while (numberAdres.next()) {

                        count++;
                    }
                    Statement stmt3 = DatabaseConnect.dbConncect().createStatement();
                    String sqlUpit3 = "SELECT *from embalint_meteo_prognosticar where adresastanice='" + aa.getString("ADRESA") + "'";
                    ResultSet numberData = stmt3.executeQuery(sqlUpit3);
                    while (numberData.next()) {
                        count++;
                    }
                    rl.setNumberMeteoData(String.valueOf(count));
                    rangList.add(rl);
                }
                Collections.sort(rangList, new MyComparator());
                for (int i = 0; i < Integer.parseInt(numbeOFData); i++) {
                    helpList.add(rangList.get(i));

                }
                if (hasResult) {
                    dodajIskoristenZahtjev(user, pass);
                    hmp.setRangList(helpList);
                    hmp.setPoruka("Uspjesno vraćeni podaci");
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Rang lista adresa SOAP");
                        dnevnik.setResult("Uspješno");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return hmp;
                } else {
                    hmp.setPoruka("Ne postoje podaci za unesenu adresu ili datum");
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Rang lista adresa SOAP");
                        dnevnik.setResult("Ne postoji adresa");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return hmp;
                }

            } catch (SQLException ex) {
                Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            hmp = new HelpForMedeoPodaci();
            hmp.setPoruka("Nemate više zahtjeva");
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Rang lista adresa SOAP");
                        dnevnik.setResult("Korisnik nema zahtjeva");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return hmp;
        }
        return null;
    }

    @WebMethod(operationName = "listaAdresa")
    public HelpForMedeoPodaci listaAdresa(@WebParam(name = "user") final String user, @WebParam(name = "pass") final String pass) {
        startTime = (int) System.currentTimeMillis();
        Date datum = new Date();
        HelpForMedeoPodaci hmp = null;

        int authentication = userAuth(user, pass);
        int brojZahtjeva = brojZahtjeva(user, pass);
        int brojOdradenihZahtejva = odradeniZahtjevi(user, pass);
        if (authentication == 0) {
            hmp = new HelpForMedeoPodaci();
            hmp.setPoruka("Niste se dobro prijavili ");

            return hmp;
        } else if (brojOdradenihZahtejva < brojZahtjeva) {
            try {
                boolean hasResult = false;
                hmp = new HelpForMedeoPodaci();
                List<String> listaSvihAdresa = new ArrayList<>();
                Statement stmt = DatabaseConnect.dbConncect().createStatement();
                String sqlUpit = "SELECT *from embalint_adrese";
                System.out.println(sqlUpit);
                ResultSet aa = stmt.executeQuery(sqlUpit);
                while (aa.next()) {
                    hasResult = true;
                    String adrese = aa.getString("ADRESA");
                    listaSvihAdresa.add(adrese);
                }
                if (hasResult) {
                    dodajIskoristenZahtjev(user, pass);
                    hmp.setListaSvihAdresa(listaSvihAdresa);
                    hmp.setPoruka("Uspjesno vraćeni podaci");
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Vrati listu adresa SOAP");
                        dnevnik.setResult("Uspješno");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return hmp;
                } else {
                    hmp.setPoruka("Ne postoje podaci za unesenu adresu ili datum");
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Vrati listu adresa SOAP");
                        dnevnik.setResult("Ne postoji adresa");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return hmp;
                }

            } catch (SQLException ex) {
                Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            hmp = new HelpForMedeoPodaci();
            hmp.setPoruka("Nemate više zahtjeva");
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Vrati listu adresa SOAP");
                        dnevnik.setResult("Korisnik nema dovoljno zahtjeva");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return hmp;
        }
        return null;
    }

    /**
     * Web service operation
     *
     * @param adresa
     * @param user
     * @param pass
     * @return
     */
    @WebMethod(operationName = "trenutniMeteoZaAdresu")
    public HelpForMedeoPodaci trenutniMeteoZaAdresu(@WebParam(name = "adresa") String adresa,
            @WebParam(name = "user") final String user, @WebParam(name = "pass") final String pass) {
        startTime = (int) System.currentTimeMillis();
        Date datum = new Date();
        HelpForMedeoPodaci hmp = null;

        int authentication = userAuth(user, pass);
        int brojZahtjeva = brojZahtjeva(user, pass);
        int brojOdradenihZahtejva = odradeniZahtjevi(user, pass);
        if (authentication == 0) {
            hmp = new HelpForMedeoPodaci();
            hmp.setPoruka("Niste se dobro prijavili ");
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Trenutni meteo podaci SOAP");
                        dnevnik.setResult("Kriva prijava");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return hmp;
        } else if (brojOdradenihZahtejva < brojZahtjeva) {
            String lastDataQuery = "Select  *from embalint_meteo WHERE ADRESASTANICE = '" + adresa + "' ORDER BY IDMETEO DESC FETCH FIRST ROW ONLY";
            System.out.println(lastDataQuery);
            MeteoPodaci mp = new MeteoPodaci();

            try {
                boolean hasResult = false;
                hmp = new HelpForMedeoPodaci();
                Statement stmt = DatabaseConnect.dbConncect().createStatement();
                ResultSet aa = stmt.executeQuery(lastDataQuery);
                while (aa.next()) {
                    hasResult = true;
                    mp.setTemperatureValue(Float.parseFloat(aa.getString("TEMP")));
                    mp.setTemperatureMin(Float.parseFloat(aa.getString("TEMPMIN")));
                    mp.setTemperatureMax(Float.parseFloat(aa.getString("TEMPMAX")));
                    mp.setPressureValue(Float.parseFloat(aa.getString("TLAK")));
                    mp.setWindSpeedValue(Float.parseFloat(aa.getString("VJETAR")));
                    mp.setWeatherNumber(Integer.parseInt(aa.getString("VRIJEME")));
                    mp.setWeatherValue(aa.getString("VRIJEMEOPIS"));
                    mp.setWindDirectionValue(Float.parseFloat(aa.getString("VJETARSMJER")));
                    mp.setHumidityValue(Float.parseFloat(aa.getString("VLAGA")));

                }
                if (hasResult) {
                    dodajIskoristenZahtjev(user, pass);
                    hmp.setMp(mp);
                    hmp.setPoruka("Uspjesno vraćeni podaci");
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Trenutni meteo podaci SOAP");
                        dnevnik.setResult("Uspiješno");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return hmp;
                } else {
                    hmp.setPoruka("Ne postoje podaci za unesenu adresu ili datum");
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Trenutni meteo podaci SOAP");
                        dnevnik.setResult("Ne postoji adresa");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return hmp;
                }
            } catch (SQLException ex) {
                Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            hmp = new HelpForMedeoPodaci();
            hmp.setPoruka("Nemate više zahtjeva");
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("Trenutni meteo podaci SOAP");
                        dnevnik.setResult("Korisnik nema zahtjeva");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return hmp;
        }
        return null;

    }

    @WebMethod(operationName = "NMedteoZaAdresu")
    public HelpForMedeoPodaci NMedteoZaAdresu(@WebParam(name = "adresa") String adresa,
            @WebParam(name = "numberOfData") final String numberOfData,
            @WebParam(name = "user") final String user, @WebParam(name = "pass") final String pass) {
        startTime = (int) System.currentTimeMillis();
        Date datum = new Date();
        HelpForMedeoPodaci hmp = null;

        int authentication = userAuth(user, pass);
        int brojZahtjeva = brojZahtjeva(user, pass);
        int brojOdradenihZahtejva = odradeniZahtjevi(user, pass);
        if (authentication == 0) {
            hmp = new HelpForMedeoPodaci();
            hmp.setPoruka("Niste se dobro prijavili ");
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("posljednih N meteo podataka SOAP");
                        dnevnik.setResult("Kriva prijava");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return hmp;
        } else if (brojOdradenihZahtejva < brojZahtjeva) {
            String lastDataQuery = "Select *from embalint_meteo WHERE ADRESASTANICE = '" + adresa + "' ORDER BY IDMETEO DESC FETCH NEXT " + numberOfData + " ROWS ONLY";
            System.out.println(lastDataQuery);

            try {
                boolean hasResult = false;
                hmp = new HelpForMedeoPodaci();
                List<MeteoPodaci> listMp = new ArrayList<>();
                Statement stmt = DatabaseConnect.dbConncect().createStatement();
                ResultSet aa = stmt.executeQuery(lastDataQuery);
                while (aa.next()) {
                    hasResult = true;
                    MeteoPodaci mp = new MeteoPodaci();
                    mp.setTemperatureValue(Float.parseFloat(aa.getString("TEMP")));
                    mp.setTemperatureMin(Float.parseFloat(aa.getString("TEMPMIN")));
                    mp.setTemperatureMax(Float.parseFloat(aa.getString("TEMPMAX")));
                    mp.setPressureValue(Float.parseFloat(aa.getString("TLAK")));
                    mp.setWindSpeedValue(Float.parseFloat(aa.getString("VJETAR")));
                    mp.setWeatherNumber(Integer.parseInt(aa.getString("VRIJEME")));
                    mp.setWeatherValue(aa.getString("VRIJEMEOPIS"));
                    mp.setWindDirectionValue(Float.parseFloat(aa.getString("VJETARSMJER")));
                    mp.setHumidityValue(Float.parseFloat(aa.getString("VLAGA")));
                    listMp.add(mp);

                }
                if (hasResult) {
                    dodajIskoristenZahtjev(user, pass);
                    hmp.setMeteoPodaci(listMp);
                    hmp.setPoruka("Uspjesno vraćeni podaci");
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("posljednih N meteo podataka SOAP");
                        dnevnik.setResult("Uspiješno");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return hmp;
                } else {
                    hmp.setPoruka("Ne postoje podaci za unesenu adresu ili datum");
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("posljednih N meteo podataka SOAP");
                        dnevnik.setResult("Ne postoji adresa");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return hmp;
                }
            } catch (SQLException ex) {
                Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            hmp = new HelpForMedeoPodaci();
            hmp.setPoruka("Nemate više zahtjeva");
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("posljednih N meteo podataka SOAP");
                        dnevnik.setResult("Korisnik nema zahtjeva");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return hmp;
        }
        return null;

    }

    @WebMethod(operationName = "ZaNekiInterval")
    public HelpForMedeoPodaci ZaNekiInterval(@WebParam(name = "adresa") String adresa,
            @WebParam(name = "datumOD") final String datumOd, @WebParam(name = "datumDO") final String datumDo,
            @WebParam(name = "user") final String user, @WebParam(name = "pass") final String pass) {
        startTime = (int) System.currentTimeMillis();
        Date datum = new Date();
        HelpForMedeoPodaci hmp = null;

        int authentication = userAuth(user, pass);
        int brojZahtjeva = brojZahtjeva(user, pass);
        int brojOdradenihZahtejva = odradeniZahtjevi(user, pass);
        if (authentication == 0) {
            hmp = new HelpForMedeoPodaci();
            hmp.setPoruka("Niste se dobro prijavili\n  Pravilan upis za datum je: 2016-06-20 18:00:00 ");
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("MP za neki interval SOAP");
                        dnevnik.setResult("Kriva prijava");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return hmp;

        } else if (brojOdradenihZahtejva < brojZahtjeva) {
            String intervalQuery = "Select *from embalint_meteo_prognosticar WHERE ADRESASTANICE = '" + adresa + "' AND (DATUM BETWEEN '\"" + datumOd + "\"' AND '\"" + datumDo + "\"')";
            System.out.println(intervalQuery);

            try {

                boolean hasResult = false;
                hmp = new HelpForMedeoPodaci();
                List<MeteoPodaci> listMp = new ArrayList<>();
                Statement stmt = DatabaseConnect.dbConncect().createStatement();
                ResultSet aa = stmt.executeQuery(intervalQuery);
                while (aa.next()) {
                    hasResult = true;
                    MeteoPodaci mp = new MeteoPodaci();
                    mp.setTemperatureValue(Float.parseFloat(aa.getString("TEMP")));
                    mp.setTemperatureMin(Float.parseFloat(aa.getString("TEMPMIN")));
                    mp.setTemperatureMax(Float.parseFloat(aa.getString("TEMPMAX")));
                    mp.setPressureValue(Float.parseFloat(aa.getString("TLAK")));
                    mp.setWindSpeedValue(Float.parseFloat(aa.getString("VJETAR")));
                    mp.setWeatherNumber(Integer.parseInt(aa.getString("VRIJEME")));
                    mp.setWeatherValue(aa.getString("VRIJEMEOPIS"));
                    mp.setWindDirectionValue(Float.parseFloat(aa.getString("VJETARSMJER")));
                    mp.setHumidityValue(Float.parseFloat(aa.getString("VLAGA")));
                    mp.setDateForecast(aa.getString("DATUM"));
                    listMp.add(mp);

                }
                if (hasResult) {
                    dodajIskoristenZahtjev(user, pass);
                    hmp.setMeteoPodaci(listMp);
                    hmp.setPoruka("Uspjesno vraćeni podaci");
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("MP za neki interval SOAP");
                        dnevnik.setResult("Uspješno");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return hmp;
                } else {
                    hmp.setPoruka("Ne postoje podaci za unesenu adresu ili datum");
                    endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("MP za neki interval SOAP");
                        dnevnik.setResult("Nema adrese");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return hmp;
                }
            } catch (SQLException ex) {
                Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            hmp = new HelpForMedeoPodaci();
            hmp.setPoruka("Nemate više zahtjeva");
            endTime = (int) (System.currentTimeMillis() - startTime);
                    dnevnik = new DnevnikSpremanje();
                    try {
                        dnevnik.setIpAdress(InetAddress.getLocalHost().toString());
                        dnevnik.setUser(user);
                        dnevnik.setOperation("MP za neki interval SOAP");
                        dnevnik.setResult("Korisnik nema zahtjeva");
                        dnevnik.setDuration(endTime);
                        dnevnik.setTime(datum.toString());
                        dnevnik.spremiDnevnik();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return hmp;
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

}
