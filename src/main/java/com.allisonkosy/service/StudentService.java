package com.allisonkosy.service;

import com.allisonkosy.App;
import com.allisonkosy.entity.Course;
import com.allisonkosy.entity.Student;
import com.allisonkosy.entity.Student;
import com.allisonkosy.entity.Teacher;
import com.google.common.base.Preconditions;
import org.hibernate.Session;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StudentService extends DBService<Student> {


    private final String tableName = "Student";
    public static final Integer passwordLength = 5;
    public StudentService(Session s) {
        super(s);
    }

    public Student createStudent(String name, String password) {
        Student student = new Student();

        Preconditions.checkNotNull(password, "Password cannot be empty");
        Preconditions.checkArgument(password.length() > passwordLength - 1, "Password Length must be > " + (passwordLength + 1));

        student.setName(name);
        student.setPassword(password);

        student = persistObject(student);

        return student;
    }

    public Student getById(Long id) {
        return getStudent(id);
    }
    public Student getStudentByName(String name) {
        return getStudent(name);
    }

    private Student getStudent(Object crieteria) {
        Student student = null;
        try {
            if(crieteria instanceof Long) {
                student = getItem(tableName, "id", (crieteria));
            }
            else {
                student = getItem(tableName, "name", (crieteria));
            }

        }
        catch (Exception e) {
            App.LOGGER.error(e.getMessage());
        }
        return student;
    }

    public Student deleteStudent(Object criteria) {
        Student c = getStudent(criteria);
        if(c != null) {
            super.deleteObject(c.getId(), Student.class);
        }
        return c;
    }

    public List<Student> createPlentyStudents(String[] strings, Teacher teacher) {
        ArrayList<Student> students = new ArrayList<>();

        for (String s : strings) {
            Student c = new Student();
            c.setName(s);
            c.setTeacher(teacher);
            c.setPassword("000000");
            students.add(c);
        }
        return persistObjects(students);
    }

    public List<Student> createPlentyStudents(Iterable<String> strings) {
        ArrayList<Student> students = new ArrayList<>();

        for (String s : strings) {
            Student c = new Student();
            c.setName(s);
//            c.setTeacher(teacher);
            c.setPassword("000000");
            students.add(c);
        }
        return persistObjects(students);
    }

    public List<Student> getAllStudents() {
        return getAllObjects(tableName);
    }


    public void dropTable() {
        dropTable(tableName);
    }

    public boolean assignTeacher(Teacher teacher, Long id) {
        Preconditions.checkNotNull(teacher, "Teacher cannot be null");
        Student s = getStudent(id);
       if(s==null){
           return false;
       }
       s.setTeacher(teacher);
        s = updateRecord(s);
        App.LOGGER.info("Updated " + s);
        return s != null;
    }

    public boolean assignTeacher(Teacher teacher, Student student) {
        Preconditions.checkNotNull(teacher, "Teacher cannot be null");
        Preconditions.checkNotNull(student, "Student cannot be null");
        Student s = student;
        s.setTeacher(teacher);
        s = updateRecord(s);
        App.LOGGER.info("assigned  " + teacher.getName() + " To " +  s.getName());
        return s != null;
    }

    public List<Student> getStudentsByGuide(Teacher teacher) {
        List<Student> ret;
        try{
            ret = getAllObjects(tableName, "teacher", teacher);
        }
        catch (Exception e) {
            ret = null;
            App.LOGGER.error(e.getMessage());
        }
        return ret;
    }


}
