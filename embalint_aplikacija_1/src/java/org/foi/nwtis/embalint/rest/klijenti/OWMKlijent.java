/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.rest.klijenti;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.embalint.web.podaci.MeteoPodaci;

/**
 *
 * @author nwtis_1
 */
public class OWMKlijent {

    String apiKey;
    OWMRESTHelper helper;
    Client client;
    List<MeteoPodaci> listaPodataka = new ArrayList<>();
    public OWMKlijent(String apiKey) {
        this.apiKey = apiKey;
        helper = new OWMRESTHelper(apiKey);
        client = ClientBuilder.newClient();
    }

    public MeteoPodaci getRealTimeWeather(String latitude, String longitude) {
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_Current_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("APIKEY", apiKey);
        
        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        try {
            JsonReader reader = Json.createReader(new StringReader(odgovor));

            JsonObject jo = reader.readObject();

            MeteoPodaci mp = new MeteoPodaci();
            mp.setSunRise(new Date(jo.getJsonObject("sys").getJsonNumber("sunrise").bigDecimalValue().longValue()*1000));
            mp.setSunSet(new Date(jo.getJsonObject("sys").getJsonNumber("sunset").bigDecimalValue().longValue()*1000));
            
            mp.setTemperatureValue(new Double(jo.getJsonObject("main").getJsonNumber("temp").doubleValue()).floatValue());
            mp.setTemperatureMin(new Double(jo.getJsonObject("main").getJsonNumber("temp_min").doubleValue()).floatValue());
            mp.setTemperatureMax(new Double(jo.getJsonObject("main").getJsonNumber("temp_max").doubleValue()).floatValue());
            mp.setTemperatureUnit("celsius");
            
            mp.setHumidityValue(new Double(jo.getJsonObject("main").getJsonNumber("humidity").doubleValue()).floatValue());
            mp.setHumidityUnit("%");
            
            mp.setPressureValue(new Double(jo.getJsonObject("main").getJsonNumber("pressure").doubleValue()).floatValue());
            mp.setPressureUnit("hPa");
            
            mp.setWindSpeedValue(new Double(jo.getJsonObject("wind").getJsonNumber("speed").doubleValue()).floatValue());
            mp.setWindSpeedName("");
            
            mp.setWindDirectionValue(new Double(jo.getJsonObject("wind").getJsonNumber("deg").doubleValue()).floatValue());
            mp.setWindDirectionCode("");
            mp.setWindDirectionName("");
            
            mp.setCloudsValue(jo.getJsonObject("clouds").getInt("all"));
            mp.setCloudsName(jo.getJsonArray("weather").getJsonObject(0).getString("description"));
            mp.setPrecipitationMode("");
            
            mp.setWeatherNumber(jo.getJsonArray("weather").getJsonObject(0).getInt("id"));
            mp.setWeatherValue(jo.getJsonArray("weather").getJsonObject(0).getString("description"));
            mp.setWeatherIcon(jo.getJsonArray("weather").getJsonObject(0).getString("icon"));
            
            mp.setLastUpdate(new Date(jo.getJsonNumber("dt").bigDecimalValue().longValue()*1000));
            return mp;
            
        } catch (Exception ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public List<MeteoPodaci> getForecastWeather(String latitude, String longitude) {
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_Forecast_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("APIKEY", apiKey);

        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        try {
            JsonReader reader = Json.createReader(new StringReader(odgovor));

            JsonObject jo = reader.readObject();

            
            JsonArray listaPrognoza = (JsonArray) jo.get("list");

            
            for (int i = 0; i < listaPrognoza.size(); i++) {
                MeteoPodaci mp = new MeteoPodaci();
                mp.setTemperatureValue(new Double(listaPrognoza.getJsonObject(i).getJsonObject("main").getJsonNumber("temp").doubleValue()).floatValue());
                mp.setTemperatureMin(new Double(listaPrognoza.getJsonObject(i).getJsonObject("main").getJsonNumber("temp_min").doubleValue()).floatValue());
                mp.setTemperatureMax(new Double(listaPrognoza.getJsonObject(i).getJsonObject("main").getJsonNumber("temp_max").doubleValue()).floatValue());
                mp.setTemperatureUnit("celsius");
                mp.setHumidityValue(new Double(listaPrognoza.getJsonObject(i).getJsonObject("main").getJsonNumber("humidity").doubleValue()).floatValue());
                mp.setHumidityUnit("%");
                mp.setPressureValue(new Double(listaPrognoza.getJsonObject(i).getJsonObject("main").getJsonNumber("pressure").doubleValue()).floatValue());
                mp.setPressureUnit("hPa");
                mp.setWindSpeedValue(new Double(listaPrognoza.getJsonObject(i).getJsonObject("wind").getJsonNumber("speed").doubleValue()).floatValue());
                mp.setWindSpeedName("");

                mp.setWindDirectionValue(new Double(listaPrognoza.getJsonObject(i).getJsonObject("wind").getJsonNumber("deg").doubleValue()).floatValue());
                mp.setWindDirectionCode("");
                mp.setWindDirectionName("");

                mp.setCloudsValue(listaPrognoza.getJsonObject(i).getJsonObject("clouds").getInt("all"));
                mp.setCloudsName(listaPrognoza.getJsonObject(i).getJsonArray("weather").getJsonObject(0).getString("description"));
                mp.setCloudsName(listaPrognoza.getJsonObject(i).getJsonArray("weather").getJsonObject(0).getString("description"));
                mp.setPrecipitationMode("");

                mp.setWeatherNumber(listaPrognoza.getJsonObject(i).getJsonArray("weather").getJsonObject(0).getInt("id"));
                mp.setWeatherValue(listaPrognoza.getJsonObject(i).getJsonArray("weather").getJsonObject(0).getString("description"));
                mp.setWeatherIcon(listaPrognoza.getJsonObject(i).getJsonArray("weather").getJsonObject(0).getString("icon"));

                mp.setDateForecast(listaPrognoza.getJsonObject(i).getJsonString("dt_txt").toString());

                listaPodataka.add(mp);
            }

            return listaPodataka;

        } catch (Exception ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
