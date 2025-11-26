/********************************************************************************************************
 * File:  ACMECollegeService.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.ejb;

import static com.algonquincollege.cst8277.entity.Student.ALL_STUDENTS_QUERY_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_KEY_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_SALT_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PREFIX;
import static com.algonquincollege.cst8277.utility.MyConstants.PARAM1;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_KEY_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_SALT_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.PU_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.CourseRegistrationPK;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.entity.SecurityRole;
import com.algonquincollege.cst8277.entity.SecurityUser;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.StudentClub;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMECollegeService
 */
@Singleton
public class ACMECollegeService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    private static final String READ_ALL_PROGRAMS = "SELECT name FROM program";
    
    // TODO ACMECS01 - Add your query constants here.
    // These are not strictly necessary since we use Criteria API and Named Queries,
    // but can be useful for reference
    private static final String READ_ALL_COURSES = "Course.findAll";
    private static final String READ_ALL_PROFESSORS = "Professor.findAll";
    private static final String READ_ALL_STUDENT_CLUBS = "StudentClub.findAll";
    private static final String READ_ALL_COURSE_REGISTRATIONS = "CourseRegistration.findAll";
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    // =====================================================
    // STUDENT CRUD METHODS (Already implemented)
    // =====================================================
    
    public List<Student> getAllStudents() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        cq.select(cq.from(Student.class));
        return em.createQuery(cq).getResultList();
    }

    public Student getStudentById(int id) {
        return em.find(Student.class, id);
    }

    @Transactional
    public Student persistStudent(Student newStudent) {
        em.persist(newStudent);
        return newStudent;
    }

    @Transactional
    public void buildUserForNewStudent(Student newStudent) {
        SecurityUser userForNewStudent = new SecurityUser();
        userForNewStudent.setUsername(
            DEFAULT_USER_PREFIX + "_" + newStudent.getFirstName() + "." + newStudent.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewStudent.setPwHash(pwHash);
        userForNewStudent.setStudent(newStudent);
        
        // TODO ACMEMS01 - Use NamedQuery on SecurityRole to find USER_ROLE
        SecurityRole userRole = null;
        try {
            TypedQuery<SecurityRole> roleQuery = em.createNamedQuery(
                SecurityRole.SECURITY_ROLE_BY_NAME, 
                SecurityRole.class
            );
            roleQuery.setParameter("roleName", USER_ROLE);
            userRole = roleQuery.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("USER_ROLE not found in database. Please ensure security roles are initialized.");
        }
        
        if (userRole != null) {
            userForNewStudent.getRoles().add(userRole);
            userRole.getUsers().add(userForNewStudent);
        }
        em.persist(userForNewStudent);
    }

    @Transactional
    public Student updateStudentById(int id, Student studentWithUpdates) {
    	Student studentToBeUpdated = getStudentById(id);
        if (studentToBeUpdated != null) {
            em.refresh(studentToBeUpdated);
            em.merge(studentWithUpdates);
            em.flush();
        }
        return studentWithUpdates;
    }

    @Transactional
    public Student deleteStudentById(int id) {
        Student student = getStudentById(id);
        if (student != null) {
            em.refresh(student);
            // TODO ACMEMS02 - Already implemented
            try {
                TypedQuery<SecurityUser> findUser = em.createNamedQuery(
                    SecurityUser.SECURITY_USER_BY_STUDENT_ID, 
                    SecurityUser.class
                ).setParameter(PARAM1, id);
                SecurityUser sUser = findUser.getSingleResult();
                em.remove(sUser);
            } catch (NoResultException e) {
                LOG.debug("No SecurityUser found for student id: {}", id);
            }
            em.remove(student);
        }
        return student;
    }
    
	@SuppressWarnings("unchecked")
    public List<String> getAllPrograms() {
		List<String> programs = new ArrayList<>();
		try {
			programs = (List<String>) em.createNativeQuery(READ_ALL_PROGRAMS).getResultList();
		}
		catch (Exception e) {
		}
		return programs;
    }

    // =====================================================
    // COURSE CRUD METHODS
    // =====================================================
    
    /**
     * Get all courses
     * @return List of all courses
     */
    public List<Course> getAllCourses() {
        LOG.debug("Retrieving all courses");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Course> cq = cb.createQuery(Course.class);
        cq.select(cq.from(Course.class));
        return em.createQuery(cq).getResultList();
    }
    
    /**
     * Get a course by its id
     * @param id - course id
     * @return Course entity
     */
    public Course getCourseById(int id) {
        LOG.debug("Retrieving course with id = {}", id);
        return em.find(Course.class, id);
    }
    
    /**
     * Create a new course
     * @param newCourse - course to create
     * @return Created course
     */
    @Transactional
    public Course persistCourse(Course newCourse) {
        LOG.debug("Creating new course: {}", newCourse);
        em.persist(newCourse);
        return newCourse;
    }
    
    /**
     * Update a course by id
     * @param id - course id
     * @param courseWithUpdates - course with updated information
     * @return Updated course
     */
    @Transactional
    public Course updateCourseById(int id, Course courseWithUpdates) {
        LOG.debug("Updating course with id = {}", id);
        Course courseToBeUpdated = getCourseById(id);
        if (courseToBeUpdated != null) {
            em.refresh(courseToBeUpdated);
            // Copy the updated fields to the existing entity
            courseToBeUpdated.setCourseCode(courseWithUpdates.getCourseCode());
            courseToBeUpdated.setCourseTitle(courseWithUpdates.getCourseTitle());
            courseToBeUpdated.setCreditUnits(courseWithUpdates.getCreditUnits());
            courseToBeUpdated.setOnline(courseWithUpdates.getOnline());
            em.merge(courseToBeUpdated);
            em.flush();
            return courseToBeUpdated;
        }
        return null;
    }
    
    /**
     * Delete a course by id
     * @param id - course id to delete
     * @return Deleted course
     */
    @Transactional
    public Course deleteCourseById(int id) {
        LOG.debug("Deleting course with id = {}", id);
        Course course = getCourseById(id);
        if (course != null) {
            em.refresh(course);
            em.remove(course);
        }
        return course;
    }

    // =====================================================
    // PROFESSOR CRUD METHODS
    // =====================================================
    
    /**
     * Get all professors
     * @return List of all professors
     */
    public List<Professor> getAllProfessors() {
        LOG.debug("Retrieving all professors");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Professor> cq = cb.createQuery(Professor.class);
        cq.select(cq.from(Professor.class));
        return em.createQuery(cq).getResultList();
    }
    
    /**
     * Get a professor by its id
     * @param id - professor id
     * @return Professor entity
     */
    public Professor getProfessorById(int id) {
        LOG.debug("Retrieving professor with id = {}", id);
        return em.find(Professor.class, id);
    }
    
    /**
     * Create a new professor
     * @param newProfessor - professor to create
     * @return Created professor
     */
    @Transactional
    public Professor persistProfessor(Professor newProfessor) {
        LOG.debug("Creating new professor: {}", newProfessor);
        em.persist(newProfessor);
        return newProfessor;
    }
    
    /**
     * Update a professor by id
     * @param id - professor id
     * @param professorWithUpdates - professor with updated information
     * @return Updated professor
     */
    @Transactional
    public Professor updateProfessorById(int id, Professor professorWithUpdates) {
        LOG.debug("Updating professor with id = {}", id);
        Professor professorToBeUpdated = getProfessorById(id);
        if (professorToBeUpdated != null) {
            em.refresh(professorToBeUpdated);
            // Copy the updated fields to the existing entity
            professorToBeUpdated.setFirstName(professorWithUpdates.getFirstName());
            professorToBeUpdated.setLastName(professorWithUpdates.getLastName());
            professorToBeUpdated.setDegree(professorWithUpdates.getDegree());
            em.merge(professorToBeUpdated);
            em.flush();
            return professorToBeUpdated;
        }
        return null;
    }
    
    /**
     * Delete a professor by id
     * @param id - professor id to delete
     * @return Deleted professor
     */
    @Transactional
    public Professor deleteProfessorById(int id) {
        LOG.debug("Deleting professor with id = {}", id);
        Professor professor = getProfessorById(id);
        if (professor != null) {
            em.refresh(professor);
            em.remove(professor);
        }
        return professor;
    }

    // =====================================================
    // STUDENT CLUB CRUD METHODS
    // =====================================================
    
    /**
     * Get all student clubs
     * @return List of all student clubs
     */
    public List<StudentClub> getAllStudentClubs() {
        LOG.debug("Retrieving all student clubs");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudentClub> cq = cb.createQuery(StudentClub.class);
        cq.select(cq.from(StudentClub.class));
        return em.createQuery(cq).getResultList();
    }
    
    /**
     * Get a student club by its id
     * @param id - student club id
     * @return StudentClub entity
     */
    public StudentClub getStudentClubById(int id) {
        LOG.debug("Retrieving student club with id = {}", id);
        return em.find(StudentClub.class, id);
    }
    
    /**
     * Create a new student club
     * @param newStudentClub - student club to create
     * @return Created student club
     */
    @Transactional
    public StudentClub persistStudentClub(StudentClub newStudentClub) {
        LOG.debug("Creating new student club: {}", newStudentClub);
        em.persist(newStudentClub);
        return newStudentClub;
    }
    
    /**
     * Update a student club by id
     * @param id - student club id
     * @param studentClubWithUpdates - student club with updated information
     * @return Updated student club
     */
    @Transactional
    public StudentClub updateStudentClubById(int id, StudentClub studentClubWithUpdates) {
        LOG.debug("Updating student club with id = {}", id);
        StudentClub clubToBeUpdated = getStudentClubById(id);
        if (clubToBeUpdated != null) {
            em.refresh(clubToBeUpdated);
            // Copy the updated fields to the existing entity
            clubToBeUpdated.setName(studentClubWithUpdates.getName());
            clubToBeUpdated.setDesc(studentClubWithUpdates.getDesc());
            // Note: academic field should not be changed after creation (it's the discriminator)
            em.merge(clubToBeUpdated);
            em.flush();
            return clubToBeUpdated;
        }
        return null;
    }
    
    /**
     * Delete a student club by id
     * @param id - student club id to delete
     * @return Deleted student club
     */
    @Transactional
    public StudentClub deleteStudentClubById(int id) {
        LOG.debug("Deleting student club with id = {}", id);
        StudentClub club = getStudentClubById(id);
        if (club != null) {
            em.refresh(club);
            em.remove(club);
        }
        return club;
    }

    // =====================================================
    // COURSE REGISTRATION CRUD METHODS
    // =====================================================
    
    /**
     * Get all course registrations
     * @return List of all course registrations
     */
    public List<CourseRegistration> getAllCourseRegistrations() {
        LOG.debug("Retrieving all course registrations");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CourseRegistration> cq = cb.createQuery(CourseRegistration.class);
        cq.select(cq.from(CourseRegistration.class));
        return em.createQuery(cq).getResultList();
    }
    
    /**
     * Get a course registration by its composite key (studentId + courseId)
     * @param studentId - student id
     * @param courseId - course id
     * @return CourseRegistration entity
     */
    public CourseRegistration getCourseRegistrationById(int studentId, int courseId) {
        LOG.debug("Retrieving course registration with studentId = {} and courseId = {}", studentId, courseId);
        CourseRegistrationPK pk = new CourseRegistrationPK();
        pk.setStudentId(studentId);
        pk.setCourseId(courseId);
        return em.find(CourseRegistration.class, pk);
    }
    
    /**
     * Create a new course registration
     * @param newCourseRegistration - course registration to create
     * @return Created course registration
     */
    @Transactional
    public CourseRegistration persistCourseRegistration(CourseRegistration newCourseRegistration) {
        LOG.debug("Creating new course registration: {}", newCourseRegistration);
        em.persist(newCourseRegistration);
        return newCourseRegistration;
    }
    
    /**
     * Update a course registration by composite key
     * @param studentId - student id
     * @param courseId - course id
     * @param courseRegistrationWithUpdates - course registration with updated information
     * @return Updated course registration
     */
    @Transactional
    public CourseRegistration updateCourseRegistrationById(int studentId, int courseId, 
                                                             CourseRegistration courseRegistrationWithUpdates) {
        LOG.debug("Updating course registration with studentId = {} and courseId = {}", studentId, courseId);
        CourseRegistration registrationToBeUpdated = getCourseRegistrationById(studentId, courseId);
        if (registrationToBeUpdated != null) {
            em.refresh(registrationToBeUpdated);
            // Ensure the composite key is set correctly
            CourseRegistrationPK pk = new CourseRegistrationPK();
            pk.setStudentId(studentId);
            pk.setCourseId(courseId);
            courseRegistrationWithUpdates.setId(pk);
            em.merge(courseRegistrationWithUpdates);
            em.flush();
        }
        return courseRegistrationWithUpdates;
    }
    
    /**
     * Delete a course registration by composite key
     * @param studentId - student id
     * @param courseId - course id
     * @return Deleted course registration
     */
    @Transactional
    public CourseRegistration deleteCourseRegistrationById(int studentId, int courseId) {
        LOG.debug("Deleting course registration with studentId = {} and courseId = {}", studentId, courseId);
        CourseRegistration registration = getCourseRegistrationById(studentId, courseId);
        if (registration != null) {
            em.refresh(registration);
            em.remove(registration);
        }
        return registration;
    }

    // TODO ACMECS02 - All CRUD methods have been added above!
    
}