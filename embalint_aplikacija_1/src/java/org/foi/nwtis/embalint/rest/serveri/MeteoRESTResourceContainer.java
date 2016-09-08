/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.rest.serveri;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.embalint.baza.DatabaseConnect;
import org.foi.nwtis.embalint.web.podaci.Adresa;
import org.foi.nwtis.embalint.web.podaci.Lokacija;

/**
 * REST Web Service
 * REST web servis koji vraća popis svih adresa u json formatu
 * @author Emil
 */
@Path("/meteoREST")
public class MeteoRESTResourceContainer {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MeteoRESTResourceContainer
     */
    public MeteoRESTResourceContainer() {
    }

    /**
     * Retrieves representation of an instance of org.foi.nwtis.embalint.rest.serveri.MeteoRESTResourceContainer
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        try {
            List<Adresa> adrese = new ArrayList<>();
            Statement stmt;
            String upit = "SELECT * FROM embalint_adrese";
            stmt = DatabaseConnect.dbConncect().createStatement();
            ResultSet aa = stmt.executeQuery(upit);
            while (aa.next()) {
                Adresa a = new Adresa(Long.parseLong(aa.getString("IDADRESA")), aa.getString("ADRESA"),
                        new Lokacija(aa.getString("LATITUDE"), aa.getString("LONGITUDE")));
                adrese.add(a);
            }
            
            JsonArrayBuilder jab = Json.createArrayBuilder();
            JsonObjectBuilder job = Json.createObjectBuilder();

            for (Adresa a : adrese) {
                try {
                    JsonObjectBuilder jObject = Json.createObjectBuilder();
                    jObject.add("id", Long.toString(a.getIdadresa()));
                    jObject.add("adresa", a.getAdresa());
                    jObject.add("lat", a.getGeoloc().getLatitude());
                    jObject.add("lon", a.getGeoloc().getLongitude());
                    jab.add(jObject);

                } catch (Exception ex) {
                    Logger.getLogger(MeteoRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            
            job.add("Popis svih adresa", jab);
            
            return job.build().toString();
        } catch (SQLException ex) {
            Logger.getLogger(MeteoRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * POST method for creating an instance of MeteoRESTResource
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postJson(String content) {
        //TODO
        return Response.created(context.getAbsolutePath()).build();
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}&{user}&{pass}")
    public MeteoRESTResource getMeteoRESTResource(@PathParam("id") String id,@PathParam("user") String user,@PathParam("pass") String pass) {
        return MeteoRESTResource.getInstance(id,user,pass);
    }
    
   
}
