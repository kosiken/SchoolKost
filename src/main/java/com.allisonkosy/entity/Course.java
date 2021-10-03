package com.allisonkosy.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course implements Model {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "course", orphanRemoval = true)
    List<CourseRegistration> registrations;

    public void setRegistrations(List<CourseRegistration> registrations) {
        this.registrations = registrations;
    }

    public List<CourseRegistration> getRegistrations() {
        return registrations;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    public static String getQuertString() {
        return "Course";
    }
}
