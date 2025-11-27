/********************************************************************************************************
 * File:  ProfessorResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.ejb.ACMECollegeService;
import com.algonquincollege.cst8277.entity.Professor;

/**
 * REST Resource for Professor entity
 * Provides CRUD operations for professors
 */
@Path("professor")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {
    
    private static final Logger LOG = LogManager.getLogger();
    
    @EJB
    protected ACMECollegeService service;
    
    @Inject
    protected SecurityContext sc;
    
    /**
     * Get all professors
     * @return Response with list of all professors
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllProfessors() {
        LOG.debug("Retrieving all professors");
        List<Professor> professors = service.getAllProfessors();
        return Response.ok(professors).build();
    }
    
    /**
     * Get a specific professor by id
     * @param id - professor id
     * @return Response with the professor or NOT_FOUND status
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Retrieving professor with id = {}", id);
        Professor professor = service.getProfessorById(id);
        Response response = Response.status(professor == null ? Status.NOT_FOUND : Status.OK)
            .entity(professor).build();
        return response;
    }
    
    /**
     * Create a new professor
     * @param newProfessor - professor to create
     * @return Response with created professor
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addProfessor(Professor newProfessor) {
        LOG.debug("Adding a new professor: {}", newProfessor);
        Professor persistedProfessor = service.persistProfessor(newProfessor);
        return Response.ok(persistedProfessor).build();
    }
    
    /**
     * Update an existing professor
     * @param id - professor id to update
     * @param updatingProfessor - professor with updated information
     * @return Response with updated professor
     */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Professor updatingProfessor) {
        LOG.debug("Updating professor with id = {}", id);
        Professor updatedProfessor = service.updateProfessorById(id, updatingProfessor);
        return Response.ok(updatedProfessor).build();
    }
    
    /**
     * Delete a professor by id
     * @param id - professor id to delete
     * @return Response with deleted professor
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Deleting professor with id = {}", id);
        Professor deletedProfessor = service.deleteProfessorById(id);
        return Response.ok(deletedProfessor).build();
    }
}