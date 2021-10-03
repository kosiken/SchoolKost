package com.allisonkosy.entity;

import javax.persistence.*;

@Entity
@Table(name = "registrations")
public class CourseRegistration implements Model {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    public void setStudent(Student student) {
        this.student = student;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public String toString() {
        return "CourseRegistration{" +
                "id=" + id +
                ", student=" + student.getName() +
                ", course=" + course.getTitle() +
                '}';
    }

    public static String getQuertString() {
        return "CourseRegistration";
    }
}
