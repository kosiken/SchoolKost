package com.allisonkosy.runner;

import com.allisonkosy.App;
import com.allisonkosy.entity.*;

import com.allisonkosy.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;


import com.google.common.base.Preconditions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.cfg.AnnotationConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Server {

    private Session session = null;
    private SessionFactory sessionFactory = null;
    private TeacherService teacherService= null;
    private StudentService studentService = null;
    private CourseService courseService = null;
    private CourseRegistrationService registrationService = null;
    private final ArrayList<String> teachers = new ArrayList<>();
    public Server() {
        String confFile = "hibernate.cfg.xml";
        ClassLoader classLoader = App.class.getClassLoader();
        File f = new File(classLoader.getResource(confFile).getFile());
        AnnotationConfiguration configuration = new AnnotationConfiguration().configure(f);
        initSession(configuration);

//
    }

    public Server(String dbDriver, String dbUrl, String dbUsername, String dbPassword) {

        String confFile = "hibernate.cfg.xml";
        ClassLoader classLoader = App.class.getClassLoader();
        File f = new File(classLoader.getResource(confFile).getFile());
        AnnotationConfiguration configuration = new AnnotationConfiguration().configure(f);
        configuration.setProperty("hibernate.connection.driver_class", Preconditions.checkNotNull(dbDriver));
        configuration.setProperty("hibernate.connection.url", Preconditions.checkNotNull(dbUrl));
        configuration.setProperty("hibernate.connection.username", Preconditions.checkNotNull(dbUsername));
        configuration.setProperty("hibernate.connection.password", Preconditions.checkNotNull(dbPassword));
        initSession(configuration);
    }

    private void initSession(AnnotationConfiguration configuration) {

        sessionFactory = configuration.buildSessionFactory();

        session = sessionFactory.openSession();
    }

    public Session getSession() {
        return session;
    }
    public void closeAll() {
        if (session != null) {
            session.close();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public Teacher registerTeacher(String name, @Nullable String password) {
        Teacher teacher = null;
        if(password == null) {
            App.LOGGER.info("Using default password 00000");
        }
        try {
         checkTeacherService();
            teacher = teacherService.createTeacher(name, password);
            teachers.add(name);
        }
        catch (Exception e) {
            App.LOGGER.error(e.getMessage());

        }
        return teacher;
    }

    public Student addStudent(String name, String password) {
        checkStudentService();
        Student student = studentService.getStudentByName(name.toLowerCase(Locale.ROOT));
        return student != null ? student : registerStudent(name, password);
    }

    public Student registerStudent(String name,  String password) {
        Student student = null;

        try {
         checkStudentService();
            student = studentService.createStudent(name, password);
        }
        catch (Exception e) {
            App.LOGGER.error(e.getMessage());

        }
        return student;
    }

    public Loginable login(String modelName, String name, String password ) {
        Loginable m = null;
        try {
            if(modelName == "students") {
                checkStudentService();
                m = studentService.getStudentByName(name.toLowerCase(Locale.ROOT));

                Student s = (Student)m;
                checkCourseRegService();
                s.setCoursesCount(registrationService.getAllCourseRegistrations(s).size());

            }
            else {
                checkTeacherService();
                m = teacherService.getTeacherByName(name);
            }

            if(m != null) {

                if(modelName == "students" && ! (m.getPassword().equals(password))) {
                    m = null;
                }

            }
        }
        catch (Exception e) {
            App.LOGGER.error(e.getMessage());
        }
         return m;

    }

   private void checkStudentService() {
        if(studentService == null) {
            studentService = new StudentService(session);
        }
    }

    private void checkTeacherService() {
        if(teacherService == null) {
            teacherService = new TeacherService(session);
        }
    }

    private void checkCourseService() {
        if(courseService == null) {
            courseService = new CourseService(session);
        }
    }
    private void checkCourseRegService() {
        if(registrationService == null) {
           registrationService = new CourseRegistrationService(session);
        }
    }


    public CourseRegistration registerForCourse(Student s, String courseName) {
        Preconditions.checkNotNull(s, "Student cannot be null");
        CourseRegistration registration = null;
        checkCourseService();
        Course course = courseService.getByName(courseName.toUpperCase(Locale.ROOT));
        if(course == null) {
            App.LOGGER.error("No course " + courseName + " found");

        }
        else if(s.checkLessThan()) {
            try {
                checkCourseRegService();
                registration = registrationService.getCourseRegistration(s, course);
                if(registration != null) {
                    App.LOGGER.error("Course " + courseName + " has already been registered by " + s.getName());
                    return registration;
                }
                registration = registrationService.createCourseRegistration(s, course);
                s.incrementCoursesCount();
                if(s.checkIfRegistered() && s.getTeacher() == null) {

                    App.LOGGER.info("Student is now registered");



                        checkStudentService();
                       Teacher teacher = getRandomTeacher();
                        studentService.assignTeacher(teacher, s);

                }
            }
            catch (Exception e) {
               App.LOGGER.error(e.getMessage());
            }

        }
        else {
            App.LOGGER.info("Student has max number of courses");
        }

        return registration;
    }
    public static int randomNumber(int max) {
        Random random = new Random();
        return  random.nextInt(max);
    }
    private int randomIndex() {
        if(teachers.isEmpty()) return -1;
        return randomNumber(teachers.size() - 1);
    }

    public List<CourseRegistration> getAllRegisteredCourses(Student s) {
        List<CourseRegistration> registrations = null;
        checkCourseRegService();

        registrations = registrationService.getAllCourseRegistrations(s);

        return registrations;
    }

    public List<Student> getAllStudents() {
        checkStudentService();
        return studentService.getAllStudents();
    }

    public List<Course> getAllCourses() {
        checkCourseService();
        return courseService.getAllCourses();
    }

    public List<Teacher> getAllTeachers() {
        checkTeacherService();
        return teacherService.getAllTeachers();
    }

    public Teacher getAssignedTeacher(String studentName) {
        checkStudentService();
        Student student = studentService.getStudentByName(studentName);

       if(student != null) {
          Teacher teacher = student.getTeacher();
          if(teacher == null) {
              App.LOGGER.info("Student has not completed registration");
          }
          return teacher;


       }
       else {
           App.LOGGER.error("No such student " +  studentName);
           return null;
       }
    }

    public void flush() {
        teachers.clear();
    }

    public List<Teacher> addBulkTeachers(String names) {
        Preconditions.checkNotNull(names, "names cannot be null");
        checkTeacherService();
        Iterable<String> strings = Splitter.on(',').trimResults().omitEmptyStrings().split(names);
        List<Teacher> teachersList = teacherService.createPlentyTeachers(strings);

        for (String name : strings) {
            teachers.add(name);
        }
        return teachersList;
    }
    public List<Student> addBulkStudents(String names) {
        Preconditions.checkNotNull(names, "names cannot be null");
        checkStudentService();
        Iterable<String> strings = Splitter.on(',').trimResults().omitEmptyStrings().split(names);
        List<Student> studentsList = studentService.createPlentyStudents(strings);


        return studentsList;
    }



    public List<Course> addBulkCourses(String names) {
        Preconditions.checkNotNull(names, "names cannot be null");
        checkCourseService();
        Iterable<String> strings = Splitter.on(',').trimResults().omitEmptyStrings().split(names);


        return courseService.createPlentyCourses(strings);
    }


    public String getStudentTeacherAsJson(Student student ) {
        Preconditions.checkNotNull(student, "student cannot be null");
        String jsonString  = null;
        try {



                Teacher teacher = student.getTeacher();
                if(teacher != null ) {
                    ObjectMapper mapper = new ObjectMapper();
//                    teacher.setStudents(null);

                    jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(teacher);
                }
                else {
                    App.LOGGER.error("Student has no teacher assigned");
                }


        }
        catch (JsonProcessingException exception) {
            App.LOGGER.error(exception.getMessage());
        }
        catch (Exception exception) {
            App.LOGGER.error(exception.getMessage());
        }

        return jsonString;
    }

    public String getStudentTeacherAsJson(String studentName) {
        Preconditions.checkNotNull(studentName, "studentName cannot be null");

        Student student = studentService.getStudentByName(studentName);




        return getStudentTeacherAsJson(student);
    }

    public Teacher getRandomTeacher() {
        checkTeacherService();
        int index = randomIndex();
        if(index == -1 ) {
            throw new IllegalStateException("No teachers yet");
        }
        Teacher teacher = teacherService.getTeacherByName(teachers.get(index));
        return teacher;
    }
}
