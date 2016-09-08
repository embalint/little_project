/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Emil
 * Created: Jun 19, 2016
 */

CREATE TABLE embalint_adrese (
  idAdresa integer NOT NULL 
                PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                (START WITH 1, INCREMENT BY 1),
  adresa varchar(255) NOT NULL DEFAULT '' UNIQUE,
  latitude varchar(25) NOT NULL DEFAULT '',
  longitude varchar(25) NOT NULL DEFAULT ''
);