/********************************************************************************************************
 * File:  NewCourseRegistrationView.java
 * Course Materials CST 8277
 * 
 * @author Mike Norman
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.entity.Student;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("newCourseRegistration")
@ViewScoped
public class NewCourseRegistrationView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int studentId;
    protected int courseId;
    protected int professorId;
    protected int year;
    protected String semester;
    protected String letterGrade;
    
    @Inject
    @ManagedProperty("#{courseRegistrationController}")
    protected CourseRegistrationController courseRegistrationController;

    public NewCourseRegistrationView() {
    }
    
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }
    
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    
    public int getProfessorId() {
        return professorId;
    }
    
    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getLetterGrade() {
        return letterGrade;
    }
    
    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }

    public void addCourseRegistration() {
        CourseRegistration theNewCourseRegistration = new CourseRegistration();
        
        Student student = new Student();
        student.setId(getStudentId());
        theNewCourseRegistration.setStudent(student);
        
        Course course = new Course();
        course.setId(getCourseId());
        theNewCourseRegistration.setCourse(course);
        
        if (getProfessorId() > 0) {
            Professor professor = new Professor();
            professor.setId(getProfessorId());
            theNewCourseRegistration.setProfessor(professor);
        }
        
        theNewCourseRegistration.setYear(getYear());
        theNewCourseRegistration.setSemester(getSemester());
        theNewCourseRegistration.setLetterGrade(getLetterGrade());
        
        courseRegistrationController.addNewCourseRegistration(theNewCourseRegistration);
        
        courseRegistrationController.toggleAdding();
        setStudentId(0);
        setCourseId(0);
        setProfessorId(0);
        setYear(0);
        setSemester(null);
        setLetterGrade(null);
    }
    
}