/********************************************************************************************************
 * File:  StudentClubResource.java Course Materials CST 8277
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
import com.algonquincollege.cst8277.entity.StudentClub;

/**
 * REST Resource for StudentClub entity
 * Provides CRUD operations for student clubs
 */
@Path("studentclub")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentClubResource {
    
    private static final Logger LOG = LogManager.getLogger();
    
    @EJB
    protected ACMECollegeService service;
    
    @Inject
    protected SecurityContext sc;
    
    /**
     * Get all student clubs
     * @return Response with list of all student clubs
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllStudentClubs() {
        LOG.debug("Retrieving all student clubs");
        List<StudentClub> clubs = service.getAllStudentClubs();
        return Response.ok(clubs).build();
    }
    
    /**
     * Get a specific student club by id
     * @param id - student club id
     * @return Response with the student club or NOT_FOUND status
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getStudentClubById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Retrieving student club with id = {}", id);
        StudentClub club = service.getStudentClubById(id);
        Response response = Response.status(club == null ? Status.NOT_FOUND : Status.OK)
            .entity(club).build();
        return response;
    }
    
    /**
     * Create a new student club
     * @param newClub - student club to create
     * @return Response with created student club
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addStudentClub(StudentClub newClub) {
        LOG.debug("Adding a new student club: {}", newClub);
        StudentClub persistedClub = service.persistStudentClub(newClub);
        return Response.ok(persistedClub).build();
    }
    
    /**
     * Update an existing student club
     * @param id - student club id to update
     * @param updatingClub - student club with updated information
     * @return Response with updated student club
     */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateStudentClub(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, StudentClub updatingClub) {
        LOG.debug("Updating student club with id = {}", id);
        StudentClub updatedClub = service.updateStudentClubById(id, updatingClub);
        return Response.ok(updatedClub).build();
    }
    
    /**
     * Delete a student club by id
     * @param id - student club id to delete
     * @return Response with deleted student club
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteStudentClub(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Deleting student club with id = {}", id);
        StudentClub deletedClub = service.deleteStudentClubById(id);
        return Response.ok(deletedClub).build();
    }
}