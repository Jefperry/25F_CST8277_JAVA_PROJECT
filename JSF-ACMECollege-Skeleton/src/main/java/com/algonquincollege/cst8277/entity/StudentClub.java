/********************************************************************************************************
 * File:  StudentClub.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import com.algonquincollege.cst8277.entity.Academic;
import com.algonquincollege.cst8277.entity.NonAcademic;

@SuppressWarnings("unused")

/**
 * The persistent class for the student_club database table.
 */
// TODO SC01 - Add the missing annotations.
@Entity(name = "StudentClub")
@Table(name = "student_club")
// Add Named Queries - IMPORTANT: Use LEFT JOIN FETCH to load studentMembers
// This prevents LazyInitializationException when accessing the collection
@NamedQuery(name = StudentClub.ALL_STUDENT_CLUBS_QUERY, 
    query = "SELECT DISTINCT sc FROM StudentClub sc LEFT JOIN FETCH sc.studentMembers")
@NamedQuery(name = StudentClub.STUDENT_CLUB_BY_ID, 
    query = "SELECT DISTINCT sc FROM StudentClub sc LEFT JOIN FETCH sc.studentMembers WHERE sc.id = :id")
// TODO SC02 - StudentClub has subclasses Academic and NonAcademic.  Look at lecture slides for InheritanceType.
// Use SINGLE_TABLE inheritance strategy - all classes in one table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// Use the "academic" column to discriminate between Academic (1) and NonAcademic (0)
@DiscriminatorColumn(name = "academic", discriminatorType = DiscriminatorType.INTEGER)
// TODO SC03 - Do we need a mapped super class?  If so, which one?
// Yes! StudentClub extends PojoBase to inherit id, version, created, updated
// However, the primary key column is named "club_id" instead of "id"
@AttributeOverride(name = "id", column = @Column(name = "club_id"))
// Jackson annotations to handle polymorphism in JSON
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = Academic.class, name = "academic"),
    @Type(value = NonAcademic.class, name = "non-academic")
})
public abstract class StudentClub extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String ALL_STUDENT_CLUBS_QUERY = "StudentClub.findAll";
	public static final String STUDENT_CLUB_BY_ID = "StudentClub.findById";

	// TODO SC04 - Add the missing annotations.
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 100)
	protected String name;

	// TODO SC05 - Add the missing annotations.
	// Note: Column is named "description" in DB but field is "desc" in Java
	@Basic(optional = true)
	@Column(name = "description", nullable = true, length = 100)
	protected String desc;

	// TODO SC06 - Add the missing annotations.
	// This field is used as the discriminator column, so it's not explicitly mapped
	// It will be populated by JPA based on the @DiscriminatorValue in subclasses
	@Column(name = "academic", insertable = false, updatable = false)
	protected boolean isAcademic;

	// TODO SC07 - Add the M:N annotation.  What should be the cascade and fetch types?
	// This is the inverse side of the M:N relationship (Student owns it with @JoinTable)
	// Cascade: MERGE to synchronize changes
	// Fetch: LAZY to avoid loading all students unnecessarily
	@ManyToMany(mappedBy = "studentClubs", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	// TODO SC08 - Add other missing annotations.
	// Use @JsonIgnore to prevent infinite loops (StudentClub -> Student -> StudentClub)
	@JsonIgnore
	protected Set<Student> studentMembers = new HashSet<Student>();
	
	// TODO SC09 - Add the missing annotations.
	@Transient
	protected boolean editable = false;

	public StudentClub() {
		super();
	}

    public StudentClub(boolean isAcademic) {
        this();
        this.isAcademic = isAcademic;
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
	
	public boolean getAcademic() {
		return this.isAcademic;
	}

	public void setAcademic(boolean isAcademic) {
		this.isAcademic = isAcademic;
	}

	// TODO SC10 - Is an annotation needed here?
	// No additional annotation needed - @JsonIgnore is already on the field
	public Set<Student> getStudentMembers() {
		return studentMembers;
	}

	public void setStudentMembers(Set<Student> studentMembers) {
		this.studentMembers = studentMembers;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	//Inherited hashCode/equals is sufficient for this Entity class

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StudentClub[id = ").append(getId()).append(", name = ").append(name).append(", desc = ")
				.append(desc).append(", isAcademic = ").append(isAcademic)
				.append(", created = ").append(getCreated()).append(", updated = ").append(getUpdated()).append(", version = ").append(getVersion()).append("]");
		return builder.toString();
	}
	
}