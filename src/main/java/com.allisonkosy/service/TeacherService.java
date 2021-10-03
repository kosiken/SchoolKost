package com.allisonkosy.service;

import com.allisonkosy.App;

import com.allisonkosy.entity.Teacher;
import org.hibernate.Session;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TeacherService extends DBService<Teacher>{
    private final String tableName = "Teacher";
    
    public TeacherService(Session s) {
        super(s);
    }

    public Teacher createTeacher(String name, @Nullable String password) {
        Teacher teacher = new Teacher();
        
        String pass = password == null ? "00000" : password;
        
        teacher.setName(name);
        teacher.setPassword(pass);
        
        teacher = persistObject(teacher);
        
        return teacher;
    }
    public Teacher getById(Long id) {
        return getTeacher(id);
    }
    public Teacher getTeacherByName(String name) {
        return getTeacher(name);
    }
    private Teacher getTeacher(Object criteria) {
        Teacher teacher = null;
        try {
            if(criteria instanceof Long) {
                teacher = getItem(tableName, "id", (criteria));
            }
            else {
                teacher = getItem(tableName, "name", (criteria));
            }

        }
        catch (Exception e) {
            App.LOGGER.error(e.getMessage());
        }
        return teacher;
    }

    public Teacher deleteTeacher(Object criteria) {
        Teacher c = getTeacher(criteria);
        if(c != null) {
            super.deleteObject(c.getId(), Teacher.class);
        }
        return c;
    }

    public List<Teacher> createPlentyTeachers(Iterable<String> strings) {
        ArrayList<Teacher> teachers = new ArrayList<>();

        for (String s : strings) {
            Teacher c = new Teacher();
            c.setName(s);
            c.setPassword("0000");
            teachers.add(c);
        }
        return persistObjects(teachers);
    }

    public List<Teacher> getAllTeachers() {
        return getAllObjects(tableName);
    }


    public void dropTable() {
        dropTable(tableName);
    }



}
