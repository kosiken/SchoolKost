package com.allisonkosy.service;

import com.allisonkosy.App;
import com.allisonkosy.entity.Course;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CourseService extends DBService<Course> {
    private final String tableName = "Course";
    public CourseService(Session s) {
        super(s);
    }
    public Course createCourse(String title) {
        Course course = new Course();
        course.setTitle(title);
        course = super.persistObject(course);

        return course;
    }

    public Course getById(Long id) {
        return getCourse(id);
    }
    public Course getByName(String name) {
        return getCourse(name);
    }
    private Course getCourse(Object crieteria) {
        Course course = null;
        try {
            if(crieteria instanceof Long) {
                course = getItem(tableName, "id", (crieteria));
            }
            else {
                course = getItem(tableName, "title", (crieteria));
            }

        }
        catch (Exception e) {
            App.LOGGER.error(e.getMessage());
        }
        return course;
    }

    public Course deleteCourse(Object criteria) {
       Course c = getCourse(criteria);
       if(c != null) {
           super.deleteObject(c.getId(), Course.class);
       }
       return c;
    }

    public List<Course> createPlentyCourses(Iterable<String> strings) {
        ArrayList<Course> courses = new ArrayList<>();

        for (String s : strings) {
           Course c = new Course();
           c.setTitle(s.toUpperCase(Locale.ROOT));
           courses.add(c);
        }
        return persistObjects(courses);
    }

    public List<Course> getAllCourses() {
        return getAllObjects(tableName);
    }


    public void dropTable() {
        dropTable(tableName);
    }


}
