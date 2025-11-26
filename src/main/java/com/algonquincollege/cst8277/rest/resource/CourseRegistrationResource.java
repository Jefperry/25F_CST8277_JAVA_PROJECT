/********************************************************************************************************
 * File:  CourseRegistrationResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;

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
import com.algonquincollege.cst8277.entity.CourseRegistration;

/**
 * REST Resource for CourseRegistration entity
 * Provides CRUD operations for course registrations
 * 
 * NOTE: CourseRegistration has a composite key (studentId + courseId)
 */
@Path("courseregistration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseRegistrationResource {
    
    private static final Logger LOG = LogManager.getLogger();
    
    @EJB
    protected ACMECollegeService service;
    
    @Inject
    protected SecurityContext sc;
    
    /**
     * Get all course registrations
     * @return Response with list of all course registrations
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllCourseRegistrations() {
        LOG.debug("Retrieving all course registrations");
        List<CourseRegistration> registrations = service.getAllCourseRegistrations();
        return Response.ok(registrations).build();
    }
    
    /**
     * Get a specific course registration by composite key (studentId + courseId)
     * Path: /courseregistration/student/{studentId}/course/{courseId}
     * 
     * @param studentId - student id
     * @param courseId - course id
     * @return Response with the course registration or NOT_FOUND status
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path("/student/{studentId}/course/{courseId}")
    public Response getCourseRegistrationById(
            @PathParam("studentId") int studentId, 
            @PathParam("courseId") int courseId) {
        LOG.debug("Retrieving course registration with studentId = {} and courseId = {}", studentId, courseId);
        CourseRegistration registration = service.getCourseRegistrationById(studentId, courseId);
        Response response = Response.status(registration == null ? Status.NOT_FOUND : Status.OK)
            .entity(registration).build();
        return response;
    }
    
    /**
     * Create a new course registration
     * @param newRegistration - course registration to create
     * @return Response with created course registration
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourseRegistration(CourseRegistration newRegistration) {
        LOG.debug("Adding a new course registration: {}", newRegistration);
        CourseRegistration persistedRegistration = service.persistCourseRegistration(newRegistration);
        return Response.ok(persistedRegistration).build();
    }
    
    /**
     * Update an existing course registration
     * Path: /courseregistration/student/{studentId}/course/{courseId}
     * 
     * @param studentId - student id
     * @param courseId - course id
     * @param updatingRegistration - course registration with updated information
     * @return Response with updated course registration
     */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path("/student/{studentId}/course/{courseId}")
    public Response updateCourseRegistration(
            @PathParam("studentId") int studentId, 
            @PathParam("courseId") int courseId,
            CourseRegistration updatingRegistration) {
        LOG.debug("Updating course registration with studentId = {} and courseId = {}", studentId, courseId);
        CourseRegistration updatedRegistration = service.updateCourseRegistrationById(studentId, courseId, updatingRegistration);
        return Response.ok(updatedRegistration).build();
    }
    
    /**
     * Delete a course registration by composite key
     * Path: /courseregistration/student/{studentId}/course/{courseId}
     * 
     * @param studentId - student id
     * @param courseId - course id
     * @return Response with deleted course registration
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/student/{studentId}/course/{courseId}")
    public Response deleteCourseRegistration(
            @PathParam("studentId") int studentId, 
            @PathParam("courseId") int courseId) {
        LOG.debug("Deleting course registration with studentId = {} and courseId = {}", studentId, courseId);
        CourseRegistration deletedRegistration = service.deleteCourseRegistrationById(studentId, courseId);
        return Response.ok(deletedRegistration).build();
    }
}