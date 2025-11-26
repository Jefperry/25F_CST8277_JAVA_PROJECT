/********************************************************************************************************
 * File:  PojoBase.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

/**
 * Abstract class that is base of (class) hierarchy for all @Entity classes
 */

@MappedSuperclass // PB01
@Access(AccessType.FIELD) // PB02
@EntityListeners(PojoListener.class) // PB03
public abstract class PojoBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id // PB04
    @Column(name = "id")
    protected int id;

    @Version // PB05
    @Column(name = "version")
    protected int version = 1;

    @Column(name = "created") // PB06 corrigé
    protected LocalDateTime created;

    @Column(name = "updated") // PB07 corrigé
    protected LocalDateTime updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }
    
    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
    
}

    /**
     * Very important:  Use getter's for member variables because JPA sometimes needs to intercept those calls<br/>
     * and go to the database to retrieve the value
     */
