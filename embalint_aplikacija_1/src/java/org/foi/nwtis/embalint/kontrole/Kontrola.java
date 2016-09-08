/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.kontrole;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.foi.nwtis.embalint.baza.DatabaseConnect;
import org.foi.nwtis.embalint.web.Dnevnik;
import org.foi.nwtis.embalint.web.Korisnik;
import org.foi.nwtis.embalint.web.Registry;
import org.foi.nwtis.embalint.ws.serveri.GeoMeteoWS;

/**
 *
 * @author Emil
 */
@WebServlet(name = "Kontrola", urlPatterns = {"/Kontrola"})
public class Kontrola extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String zahtjev = request.getServletPath();
        String odrediste = null;

        switch (zahtjev) {
            case "/Prijava":
                odrediste = "/login.jsp";
                break;
            case "/Kontrola":
                odrediste = "/index.jsp";
                break;
            case "/LoginConf":
                String user = request.getParameter("user");
                String pass = request.getParameter("pass");

                if (user.isEmpty() || pass.isEmpty()) {
                    out.println("<h1>Niste unesli dobre podatke</h1>");
                } else {
                    int authentication = userAuth(user, pass);
                    if (authentication == 0) {
                        out.println("Niste se dobro prijavili");
                        System.out.println("losa prijava");
                    } else if (authentication == 1) {
                        ResultSet aa = dohvatiSveKorisnike();
                       
                        List<Korisnik> korisnici = new ArrayList();
                        
                        
                        try {
                            while (aa.next()) {
                                Korisnik kor = new Korisnik();
                                kor.setIme(aa.getString("IME"));
                                kor.setPrezime(aa.getString("PREZIME"));
                                kor.setKorIme(aa.getString("KORISNICKO_IME"));
                                kor.setKategorija(aa.getString("KATEGORIJA"));
                                kor.setPassword(aa.getString("PASSWORD"));
                                kor.setUloga(Integer.parseInt(aa.getString("ULOGA")));
                                korisnici.add(kor);
                      
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Kontrola.class.getName()).log(Level.SEVERE, null, ex);
                        }
                            for (int i = 0; i < korisnici.size(); i++) {
                                System.out.println("korisnik "+ korisnici.get(i).getIme());
                        }
                         request.setAttribute("users", korisnici);
                         Registry.getInstance().set("users", korisnici);
                         odrediste = "/pregledKorisnika.jsp";

                    } else {
                        odrediste = "/login.jsp";
                    }
                }

                break;
                case "/PregledKorisnika":
                    ResultSet aa = dohvatiSveKorisnike();
                       
                        List<Korisnik> korisnici = new ArrayList();
                        
                        
                        try {
                            while (aa.next()) {
                                Korisnik kor = new Korisnik();
                                kor.setIme(aa.getString("IME"));
                                kor.setPrezime(aa.getString("PREZIME"));
                                kor.setKorIme(aa.getString("KORISNICKO_IME"));
                                kor.setKategorija(aa.getString("KATEGORIJA"));
                                kor.setPassword(aa.getString("PASSWORD"));
                                kor.setUloga(Integer.parseInt(aa.getString("ULOGA")));
                                korisnici.add(kor);
                      
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Kontrola.class.getName()).log(Level.SEVERE, null, ex);
                        }
                          
                         request.setAttribute("users", korisnici);
                         Registry.getInstance().set("users", korisnici);
                         odrediste = "/pregledKorisnika.jsp";
                    
                    break;
                case "/Dnevnik":
                    ResultSet dnevnici = dohvatiSveDnevnike();
                    List<Dnevnik> dnevnikList = new ArrayList();
                    
                        try {
                            while (dnevnici.next()) {
                                Dnevnik dn = new Dnevnik();

                                dn.setIpAdresa(dnevnici.getString("IPADRESA"));
                                dn.setKorisnik(dnevnici.getString("KORISNIK"));
                                dn.setOperacija(dnevnici.getString("OPERACIJA"));
                                dn.setRezultat(dnevnici.getString("REZULTAT"));
                                dn.setTrajanje(Integer.parseInt(dnevnici.getString("TRAJANJE")));
                                dn.setVrijeme(dnevnici.getString("VRIJEME"));
                                dnevnikList.add(dn);
                      
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Kontrola.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        request.setAttribute("dnevnik", dnevnikList);
                        odrediste = "/dnevnik.jsp";
                    break;
        }
        RequestDispatcher rd = this.getServletContext().getRequestDispatcher(odrediste);
        rd.forward(request, response);
    }

    private int userAuth(String korisnik, String lozinka) {

        try {

            Statement stmt = DatabaseConnect.dbConncect().createStatement();
            String sqlUpit = "SELECT ULOGA FROM embalint_korisnici "
                    + "WHERE korisnicko_ime='" + korisnik + "' AND password='" + lozinka + "'";
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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private ResultSet dohvatiSveKorisnike() {

        ResultSet aa = null;
        try {
            Statement stmt = DatabaseConnect.dbConncect().createStatement();
            String sqlUpit = "SELECT *FROM embalint_korisnici";
            aa = stmt.executeQuery(sqlUpit);
        } catch (SQLException ex) {
            Logger.getLogger(Kontrola.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aa;
    }

    private ResultSet dohvatiSveDnevnike() {
       ResultSet aa = null;
        try {
            Statement stmt = DatabaseConnect.dbConncect().createStatement();
            String sqlUpit = "SELECT *FROM embalint_dnevnik";
            aa = stmt.executeQuery(sqlUpit);
        } catch (SQLException ex) {
            Logger.getLogger(Kontrola.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aa;
    }

}
