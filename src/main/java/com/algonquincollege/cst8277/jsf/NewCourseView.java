/********************************************************************************************************
 * File:  NewCourseView.java
 * Course Materials CST 8277
 * 
 * @author Mike Norman
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.Course;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("newCourse")
@ViewScoped
public class NewCourseView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String courseCode;
    protected String courseTitle;
    protected Integer creditUnits;
    protected Short online;
    
    @Inject
    @ManagedProperty("#{courseController}")
    protected CourseController courseController;

    public NewCourseView() {
    }
    
    public String getCourseCode() {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }
    
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
    
    public Integer getCreditUnits() {
        return creditUnits;
    }
    
    public void setCreditUnits(Integer creditUnits) {
        this.creditUnits = creditUnits;
    }

    public Short getOnline() {
        return online;
    }
    
    public void setOnline(Short online) {
        this.online = online;
    }

    public void addCourse() {
        Course theNewCourse = new Course();
        theNewCourse.setCourseCode(getCourseCode());
        theNewCourse.setCourseTitle(getCourseTitle());
        theNewCourse.setCreditUnits(getCreditUnits());
        theNewCourse.setOnline(getOnline());
        courseController.addNewCourse(theNewCourse);
        
        courseController.toggleAdding();
        setCourseCode(null);
        setCourseTitle(null);
        setCreditUnits(null);
        setOnline(null);
    }
    
}