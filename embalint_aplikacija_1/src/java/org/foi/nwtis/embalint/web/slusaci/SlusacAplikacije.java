/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.embalint.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.embalint.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.embalint.obrade.MeteoPodaciObrada;
import org.foi.nwtis.embalint.obrade.SocketServerObrada;
import org.foi.nwtis.embalint.web.ConfigReader;
import org.foi.nwtis.embalint.web.Registry;

/**
 * Slusac koji inicijalizira kontekst, dohvaca konfiguracijsku datoteku koju prosljeđuje u klasu ConfigReader, ciji objekt
 * sprema u Hashmapu, tako da nam je uvijek dostupna. Uz to starta dretvu koja sprema meteo podatke za zadane adrese iz tablice
 * ADRESE
 * @author Emil
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
    
    static private ServletContext context = null;
    MeteoPodaciObrada mpo=null;
    SocketServerObrada sso=null;

    public static ServletContext getContext() {
        return context;
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();
        String dir = context.getRealPath("/WEB-INF");
        String datoteka = dir + File.separator
                + context.getInitParameter("konfig");

        BP_Konfiguracija bp_konfig = null;
        ConfigReader reader = new ConfigReader(datoteka);
        Registry.getInstance().set("ConfigReader", reader);
        
        try {
            bp_konfig = new BP_Konfiguracija(datoteka);
            context.setAttribute("BP_Konfig", bp_konfig);
            Registry.getInstance().set("BP_Konfig", bp_konfig);
            mpo = new MeteoPodaciObrada();
            mpo.start();
            sso = new SocketServerObrada();
            sso.start();
            
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
       
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (mpo != null && !mpo.isInterrupted()) {
            mpo.interrupt();
        }
        if (sso != null && !sso.isInterrupted()) {
            sso.interrupt();
        }
    }
}
