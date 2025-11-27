/********************************************************************************************************
 * File:  NewProfessorView.java
 * Course Materials CST 8277
 * 
 * @author Mike Norman
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.Professor;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("newProfessor")
@ViewScoped
public class NewProfessorView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String firstName;
    protected String lastName;
    protected String degree;
    
    @Inject
    @ManagedProperty("#{professorController}")
    protected ProfessorController professorController;

    public NewProfessorView() {
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getDegree() {
        return degree;
    }
    
    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void addProfessor() {
        Professor theNewProfessor = new Professor();
        theNewProfessor.setFirstName(getFirstName());
        theNewProfessor.setLastName(getLastName());
        theNewProfessor.setDegree(getDegree());
        professorController.addNewProfessor(theNewProfessor);
        
        professorController.toggleAdding();
        setFirstName(null);
        setLastName(null);
        setDegree(null);
    }
    
}