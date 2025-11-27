/********************************************************************************************************
 * File:  CourseResource.java Course Materials CST 8277
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
import com.algonquincollege.cst8277.entity.Course;

/**
 * REST Resource for Course entity
 * Provides CRUD operations for courses
 */
@Path("course")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {
    
    private static final Logger LOG = LogManager.getLogger();
    
    @EJB
    protected ACMECollegeService service;
    
    @Inject
    protected SecurityContext sc;
    
    /**
     * Get all courses
     * @return Response with list of all courses
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllCourses() {
        LOG.debug("Retrieving all courses");
        List<Course> courses = service.getAllCourses();
        return Response.ok(courses).build();
    }
    
    /**
     * Get a specific course by id
     * @param id - course id
     * @return Response with the course or NOT_FOUND status
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Retrieving course with id = {}", id);
        Course course = service.getCourseById(id);
        Response response = Response.status(course == null ? Status.NOT_FOUND : Status.OK)
            .entity(course).build();
        return response;
    }
    
    /**
     * Create a new course
     * @param newCourse - course to create
     * @return Response with created course
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourse(Course newCourse) {
        LOG.debug("Adding a new course: {}", newCourse);
        Course persistedCourse = service.persistCourse(newCourse);
        return Response.ok(persistedCourse).build();
    }
    
    /**
     * Update an existing course
     * @param id - course id to update
     * @param updatingCourse - course with updated information
     * @return Response with updated course
     */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Course updatingCourse) {
        LOG.debug("Updating course with id = {}", id);
        Course updatedCourse = service.updateCourseById(id, updatingCourse);
        return Response.ok(updatedCourse).build();
    }
    
    /**
     * Delete a course by id
     * @param id - course id to delete
     * @return Response with deleted course
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Deleting course with id = {}", id);
        Course deletedCourse = service.deleteCourseById(id);
        return Response.ok(deletedCourse).build();
    }
}