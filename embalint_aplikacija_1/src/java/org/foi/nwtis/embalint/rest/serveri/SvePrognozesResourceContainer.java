/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.embalint.rest.serveri;

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

/**
 * REST Web Service
 *
 * @author Emil
 */
@Path("/svePrognoze")
public class SvePrognozesResourceContainer {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SvePrognozesResourceContainer
     */
    public SvePrognozesResourceContainer() {
    }

    /**
     * Retrieves representation of an instance of org.foi.nwtis.embalint.rest.serveri.SvePrognozesResourceContainer
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * POST method for creating an instance of SvePrognozeResource
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
    @Path("{id}&{user}&{pass}&{pocetnoVrijeme}&{zavrsnoVrijeme}")
    public SvePrognozeResource getSvePrognozeResource(@PathParam("id") String id,@PathParam("user") String user,
    @PathParam("pass") String pass,@PathParam("pocetnoVrijeme") String pocetnoVrijeme,@PathParam("zavrsnoVrijeme") String zavrsnoVrijeme) {
        return SvePrognozeResource.getInstance(id,user,pass,pocetnoVrijeme,zavrsnoVrijeme);
    }
}
