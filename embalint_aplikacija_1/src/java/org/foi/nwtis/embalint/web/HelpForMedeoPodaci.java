/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.web;

import java.util.List;
import org.foi.nwtis.embalint.web.podaci.MeteoPodaci;

/**
 *
 * @author Emil
 */
public class HelpForMedeoPodaci {
    String poruka;

    public List<String> getListaSvihAdresa() {
        return listaSvihAdresa;
    }

    public void setListaSvihAdresa(List<String> listaSvihAdresa) {
        this.listaSvihAdresa = listaSvihAdresa;
    }
    List<String> listaSvihAdresa;
    private List<RangList> rangList;

    public List<RangList> getRangList() {
        return rangList;
    }

    public void setRangList(List<RangList> rangList) {
        this.rangList = rangList;
    }
    private List<MeteoPodaci> meteoPodaci = null;
    private MeteoPodaci mp;

    public MeteoPodaci getMp() {
        return mp;
    }

    public void setMp(MeteoPodaci mp) {
        this.mp = mp;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    public List<MeteoPodaci> getMeteoPodaci() {
        return meteoPodaci;
    }

    public void setMeteoPodaci(List<MeteoPodaci> meteoPodaci) {
        this.meteoPodaci = meteoPodaci;
    }
    
}
