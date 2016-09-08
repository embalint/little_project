/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.obrade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.embalint.rest.klijenti.GMKlijent;
import org.foi.nwtis.embalint.baza.DatabaseConnect;
import org.foi.nwtis.embalint.web.podaci.Lokacija;
import org.foi.nwtis.embalint.ws.serveri.GeoMeteoWS;

/**
 *
 * @author Emil
 */
public class PozadinskaDretvaObrada extends Thread {

    private final String sintaksa2Provjera = "^USER ([\\w-_]+); PASSWD ([\\w-_#!]+);$";
    private final String sintaksa3AdminPause = "^USER ([\\w-_]+); PASSWD ([\\w-_#!]+); PAUSE;$";
    private final String sintaksa3AdminStart = "^USER ([\\w-_]+); PASSWD ([\\w-_#!]+); START;$";
    private final String sintaksa3AdminStop = "^USER ([\\w-_]+); PASSWD ([\\w-_#!]+); STOP;$";
    private final String sintaksa3UserAdd = "^USER ([\\w-_]+); PASSWD ([\\w-_#!]+); ADD \"([^\"]*)\";$";
    private final String sintaksa3UserTest = "^USER ([\\w-_]+); PASSWD ([\\w-_#!]+); TEST \"([^\"]*)\";$";
    private final String sintaksa3UserGet = "^USER ([\\w-_]+); PASSWD ([\\w-_#!]+); GET \"([^\"]*)\";$";
    String komanda = "";
    Socket socket;
    private Matcher m = null;
    private String odgovor = "";
    MeteoPodaciObrada mpo=null;

    PozadinskaDretvaObrada(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;

        try {
           // is = socket.getInputStream();
            //os = socket.getOutputStream();

            StringBuilder sb = new StringBuilder();

            try {
                sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(PozadinskaDretvaObrada.class.getName()).log(Level.SEVERE, null, ex);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(is, "ISO-8859-2"));

            String linija;
            List<String> linije = new ArrayList<>();

            while ((linija = in.readLine()) != null) {
                linije.add(linija);
            }
            //komanda = linije.get(0);
            komanda="USER embalint;PASSWD emil1;";

            System.out.println(komanda);
            String[] zahtjev = komanda.split(";");
            int duzina = zahtjev.length;
            if (duzina == 2) {
                if (zahtjev[0].startsWith("USER")) {

                    autentikacija();

                } 
            } else if (duzina == 3) {
                if (zahtjev[2].equals(" PAUSE")) {
                    dretvaPauza();
                } else if (zahtjev[2].equals(" START")) {
                    dretvaStart();

                } else if (zahtjev[2].equals(" STOP")) {
                    dretvaStop();

                } else if (zahtjev[2].startsWith(" ADD")) {
                    obradaSintaksaAdd();

                } 
            }

            if (m == null) {
                odgovor = "ERR 99; Neipsravna komanda";
            }

            os.write(odgovor.getBytes());

            System.out.println("Odgovor servera: " + odgovor);

        } catch (IOException ex) {
            //exception za is i os
            Logger.getLogger(PozadinskaDretvaObrada.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


@Override
        public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    private void obradaSintaksaAdd() {
         m = provjeraParametara(komanda, sintaksa3UserAdd);
         String adresa = m.group(3);
        Lokacija l = dajLokacijuRest(adresa);
    }
    public Matcher provjeraParametara(String p, String sintaksa) {
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = m.matches();
        if (status) {
            return m;
        } else {
            return null;
        }
    }
    private Lokacija dajLokacijuRest(String adresa) {
        GMKlijent gmk = new GMKlijent();
        Lokacija l = gmk.getGeoLocation(adresa);
        return l;
    }

    private void autentikacija() {
        m = provjeraParametara(komanda, sintaksa2Provjera);

        if (m != null) {
            boolean autentikacija = checkUser(m.group(1), m.group(2));

            if (autentikacija) {
                odgovor = "OK 10;";
            } else {
                odgovor = "ERR 20;";
            }
    }
    }

    private boolean checkUser(String korisnik, String lozinka) {
       try {
            Statement stmt = DatabaseConnect.dbConncect().createStatement();
            String sqlUpit = "SELECT ULOGA FROM embalint_korisnici "
                    + "WHERE korisnicko_ime='" + korisnik + "' AND password='" + lozinka + "'";
            System.out.println(sqlUpit);
            ResultSet aa = stmt.executeQuery(sqlUpit);
            if (aa.next()) {
                //int uloga = Integer.parseInt(aa.getString("ULOGA"));
                return true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private void dretvaPauza() {
         if (mpo.isPaused()) {
                        odgovor = "ERR 30;";
                    } else {
                        mpo.setPaused(true);
                        odgovor = "OK 10;";
                    }
    }

    private void dretvaStop() {
    
    }

    private void dretvaStart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
}
