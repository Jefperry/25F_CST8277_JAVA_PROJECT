/********************************************************************************************************
 * File:  NewStudentClubView.java
 * Course Materials CST 8277
 * 
 * @author Mike Norman
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.Academic;
import com.algonquincollege.cst8277.entity.NonAcademic;
import com.algonquincollege.cst8277.entity.StudentClub;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("newStudentClub")
@ViewScoped
public class NewStudentClubView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected String desc;
    protected boolean isAcademic;
    
    @Inject
    @ManagedProperty("#{studentClubController}")
    protected StudentClubController studentClubController;

    public NewStudentClubView() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public boolean isAcademic() {
        return isAcademic;
    }
    
    public void setAcademic(boolean isAcademic) {
        this.isAcademic = isAcademic;
    }

    public void addStudentClub() {
        StudentClub theNewStudentClub;
        if (isAcademic) {
            theNewStudentClub = new Academic();
        } else {
            theNewStudentClub = new NonAcademic();
        }
        theNewStudentClub.setName(getName());
        theNewStudentClub.setDesc(getDesc());
        studentClubController.addNewStudentClub(theNewStudentClub);
        
        studentClubController.toggleAdding();
        setName(null);
        setDesc(null);
        setAcademic(false);
    }
    
}