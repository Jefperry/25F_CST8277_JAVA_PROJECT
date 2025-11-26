/********************************************************************************************************
 * File:  StudentClubController.java
 * Course Materials CST 8277
 * 
 * @author (original) Mike Norman
 * @author Teddy Yap
 *
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.algonquincollege.cst8277.utility.MyConstants;
import com.algonquincollege.cst8277.entity.StudentClub;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("studentClubController")
@SessionScoped
public class StudentClubController implements Serializable, MyConstants {
    private static final long serialVersionUID = 1L;

    @Inject
    protected FacesContext facesContext;
    @Inject
    protected ExternalContext externalContext;
    @Inject
    protected ServletContext sc;
    @Inject
    protected LoginBean loginBean;

    protected List<StudentClub> listOfStudentClubs;
    protected boolean adding;
    protected ResourceBundle bundle;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public StudentClubController() {
    	super();
    }
    
    @PostConstruct
    public void initialize() {
        uri = UriBuilder
                .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
                .scheme(HTTP_SCHEMA)
                .host(HOST)
                .port(PORT)
                .build();
        
        auth = HttpAuthenticationFeature.basic(loginBean.getUsername(), loginBean.getPassword());
        
        client = ClientBuilder.newClient(
                new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        
        webTarget = client.target(uri);
        
        loadStudentClubs();
    }

    public List<StudentClub> getStudentClubs() {
        return listOfStudentClubs;
    }
    
    public void setStudentClubs(List<StudentClub> listOfStudentClubs) {
        this.listOfStudentClubs = listOfStudentClubs;
    }
    
    public void loadStudentClubs() {
    	Response response = webTarget
                .register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .request()
                .get();
        listOfStudentClubs = response.readEntity(new GenericType<List<StudentClub>>(){});
    }

    public boolean isAdding() {
        return adding;
    }
    
    public void setAdding(boolean adding) {
        this.adding = adding;
    }
    
    public void toggleAdding() {
        setAdding(!isAdding());
    }

    public String editStudentClub(StudentClub studentClub) {
        studentClub.setEditable(true);
        return null;
    }

    public String updateStudentClub(StudentClub studentClub) {
        Response response = webTarget
        		.register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME + "/" + studentClub.getId())
                .request()
                .put(Entity.json(studentClub));
        StudentClub updatedStudentClub = response.readEntity(StudentClub.class);
        updatedStudentClub.setEditable(false);
        int idx = listOfStudentClubs.indexOf(studentClub);
        listOfStudentClubs.remove(idx);
        listOfStudentClubs.add(idx, updatedStudentClub);
        return null;
    }

    public String cancelUpdate(StudentClub studentClub) {
        studentClub.setEditable(false);
        return null;
    }

    public String deleteStudentClub(int studentClubId) {
        Response response = webTarget
        		.register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME + "/" + studentClubId)
                .request()
                .get();
        StudentClub studentClubToBeDeleted = response.readEntity(StudentClub.class);
        if (studentClubToBeDeleted != null) {
        	response = webTarget     	
                    .register(auth)
                    .path(STUDENT_CLUB_RESOURCE_NAME + "/" + studentClubToBeDeleted.getId())
                    .request()
                    .delete();
        	StudentClub deletedStudentClub = response.readEntity(StudentClub.class);
            listOfStudentClubs.remove(deletedStudentClub);
        }
        return null;
    }

    public String addNewStudentClub(StudentClub theNewStudentClub) {
        Response response = webTarget
                .register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .request()
                .post(Entity.json(theNewStudentClub));
        StudentClub newStudentClub = response.readEntity(StudentClub.class);
        listOfStudentClubs.add(newStudentClub);
        return null;
    }

    public String refreshStudentClubForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        loadStudentClubs();
        return MAIN_PAGE_REDIRECT;
    }
    
}