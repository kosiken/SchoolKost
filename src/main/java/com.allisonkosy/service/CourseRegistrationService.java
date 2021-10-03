package com.allisonkosy.service;

import com.allisonkosy.App;
import com.allisonkosy.entity.Course;
import com.allisonkosy.entity.CourseRegistration;
import com.allisonkosy.entity.Model;
import com.allisonkosy.entity.Student;
import com.google.common.base.Preconditions;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CourseRegistrationService extends DBService<CourseRegistration>{
    public static class Combo {

        public final Course course;
        public final Student student;

        public Combo(@Nonnull Course c, @Nonnull Student s) {
            this.course = c;
            this.student = s;
        }
    }
    private final String tableName = "CourseRegistration";
    public CourseRegistrationService(Session s) {
        super(s);
    }
    public CourseRegistration createCourseRegistration(Student student, Course course) {
        CourseRegistration courseRegistration = new CourseRegistration();
        courseRegistration.setStudent(student);
        courseRegistration.setCourse(course);
        courseRegistration = super.persistObject(courseRegistration);

        return courseRegistration;
    }
    public CourseRegistration getById(Long id) {
        return getCourseRegistration(id);
    }

    public CourseRegistration getCourseRegistration(Student student, Course course) {
        Preconditions.checkNotNull(student, "student cannot be null");
        Preconditions.checkNotNull(course, "course cannot be null");
        String builder = "from " + tableName +
                " item where item.student" +

                " = :student and item.course = :course";
        Query query = createQuery(builder);
        query.setParameter("student", student);
        query.setParameter("course", course);
        return (CourseRegistration) query.uniqueResult();



    }

    private CourseRegistration getCourseRegistration(Object crieteria) {
        CourseRegistration courseRegistration = null;
        try {
            if(crieteria instanceof Student) {
                Student s = (Student) crieteria;
                courseRegistration = getItem(tableName, "student", s);
            }
            else if(crieteria instanceof Course) {
                Course c = (Course) crieteria;
                courseRegistration = getItem(tableName, "course", c);
            }

        }
        catch (Exception e) {
            App.LOGGER.error(e.getMessage());
        }
        return courseRegistration;
    }

    public CourseRegistration deleteCourseRegistration(Object criteria) {
        CourseRegistration c = getCourseRegistration(criteria);
        if(c != null) {
            super.deleteObject(c.getId(), CourseRegistration.class);
        }
        return c;
    }

    public List<CourseRegistration> createPlentyCourseRegistrations(List<Combo> combos) {
        ArrayList<CourseRegistration> courseRegistrations = new ArrayList<>();

        for (Combo s : combos) {
            CourseRegistration c = new CourseRegistration();
            c.setCourse(s.course);
            c.setStudent(s.student);
            courseRegistrations.add(c);
        }
        return persistObjects(courseRegistrations);
    }

    public List<CourseRegistration> getAllCourseRegistrations() {
        return getAllObjects(tableName);
    }


    public void dropTable() {
        dropTable(tableName);
    }

    public List<CourseRegistration> getAllCourseRegistrations(Model o) {
        List<CourseRegistration> ret = null;
        try {
            if(o instanceof Student) {
            ret = getAllObjects(tableName, "student", o);
            }
            else {
                ret = getAllObjects(tableName, "course", o);
            }
        }
        catch (Exception e) {
            App.LOGGER.error(e.getMessage());
        }

        return ret;

    }



}
