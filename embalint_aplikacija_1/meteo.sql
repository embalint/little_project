/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Emil
 * Created: Jun 19, 2016
 */

CREATE TABLE embalint_meteo (
  idMeteo integer NOT NULL 
                PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                (START WITH 1, INCREMENT BY 1),
  idAdresa integer NOT NULL,				
  adresaStanice varchar(255) NOT NULL DEFAULT '',
  latitude varchar(25) NOT NULL DEFAULT '',
  longitude varchar(25) NOT NULL DEFAULT '',
  vrijeme varchar(25) NOT NULL DEFAULT '',
  vrijemeOpis varchar(25) NOT NULL DEFAULT '',
  temp float NOT NULL DEFAULT -999,
  tempMin float NOT NULL DEFAULT -999,
  tempMax float NOT NULL DEFAULT -999,
  vlaga float NOT NULL DEFAULT -999,
  tlak float NOT NULL DEFAULT -999,
  vjetar float NOT NULL DEFAULT -999,
  vjetarSmjer float NOT NULL DEFAULT -999,
  preuzeto timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT meteo_FK1 FOREIGN KEY (idAdresa) REFERENCES adrese (idAdresa)
);