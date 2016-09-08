/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.web;

import java.util.Comparator;

/**
 *
 * @author Emil
 */
public class MyComparator implements Comparator<RangList> {

    @Override
    public int compare(RangList o1, RangList o2) {
        
        if (Integer.parseInt(o1.getNumberMeteoData())> Integer.parseInt(o2.getNumberMeteoData())) {
            return -1;
        } else if (Integer.parseInt(o1.getNumberMeteoData()) < Integer.parseInt(o2.getNumberMeteoData())) {
            return 1;
        }
        return 0;
    }
}
