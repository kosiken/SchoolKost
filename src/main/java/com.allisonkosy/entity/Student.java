package com.allisonkosy.entity;

import com.google.common.collect.Range;

import javax.persistence.*;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "students")
public class Student implements Loginable {
    @Id
    @GeneratedValue
    private Long id;


    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;


    @ManyToOne(optional = true)
    @JoinColumn(name = "teacher_id", nullable = true)
    private Teacher teacher;

    @OneToMany(mappedBy = "student", orphanRemoval = true)
    List<CourseRegistration> courses;

    @Transient
    private Integer coursesCount = 0;

    @Transient
    private Range<Integer> regRange = Range.closed(5, 7);

    public void setCoursesCount(Integer coursesCount) {
        this.coursesCount = coursesCount;
    }

    public Integer getCoursesCount() {
        return coursesCount;
    }
    public boolean checkLessThan() {
        return coursesCount < 7;

    }
    public boolean checkIfRegistered() {
        return regRange.contains(coursesCount);
    }
    public void incrementCoursesCount() {
        coursesCount++;
    }

    public void setCourses(List<CourseRegistration> courses) {
        this.courses = courses;
    }

    public List<CourseRegistration> getCourses() {
        return courses;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Teacher getTeacher() {
        return teacher;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\''
                + ", guide='" + (teacher != null ? teacher.getId() : "NO GUIDE") + '\'' +
                '}';
    }

    public static String getQuertString() {
        return "Student";
    }
}
