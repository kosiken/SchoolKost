// YOU MUST BE CONNECTED TO INTERNET FOR THIS CODE TO WORK

package com.allisonkosy;

import com.allisonkosy.entity.CourseRegistration;
import com.allisonkosy.entity.Student;
import com.allisonkosy.entity.Teacher;
import com.allisonkosy.runner.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Test class")
@Testcontainers
public class AppTest {

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.13")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    public static Server server = null;

    @BeforeAll
    public static void setUpServer() {
        String dbUrl =  postgreSQLContainer.getJdbcUrl();
        String dbDriver = "org.postgresql.Driver";
        server = new Server( dbDriver, dbUrl, "sa", "sa");
        App.initializeServer(server);

    }

    @Test
    @DisplayName("Test register student")
    public void shouldRegisterStudent() {
        Student student = server.addStudent("Daniel Maldini", "0000000");
        assertNotNull(student);
        assertEquals("Daniel Maldini".toLowerCase(Locale.ROOT), student.getName());
    }

    @Test
    @DisplayName("Test register Course")
    public void shouldRegisterCourse() {
        Student student = server.addStudent("Pauolo Maldini", "0000000");

        CourseRegistration registration = server.registerForCourse(student, "MATHS");

        assertNotNull(registration);
        assertEquals("Pauolo Maldini".toLowerCase(Locale.ROOT), registration.getStudent().getName());
        assertEquals("MATHS", registration.getCourse().getTitle());

    }

    @Test
    @DisplayName("Test assign Teacher")
    public void shouldAssignTeacher(){
        Student student = server.addStudent("Pauolo Maldini", "0000000");
        App.addStudentsCourse(server, student, "MATHS,ENGLISH,FINE ART,FURTHER MATHS,AGRIC".split(","));
        Teacher t = student.getTeacher();
        assertNotNull(t);

    }

    @Test
    @DisplayName("Get All Students")
    public void shouldGetAllStudents() {
        Student s = server.addStudent("Cesare Maldini", "000000");

        List<Student> students = server.getAllStudents();
        assertNotNull(students);
        assertFalse(students.isEmpty());
    }



    @Test
    @DisplayName("Get All Student's Courses")
    public void shouldGetAllStudentsCourses() {
        Student student = server.addStudent("Pauolo Maldini", "0000000");
        App.addStudentsCourse(server, student, "MATHS,ENGLISH,FINE ART,FURTHER MATHS,AGRIC".split(","));

        List<CourseRegistration> courses = server.getAllRegisteredCourses(student);

        assertNotNull(courses);
        assertFalse(courses.isEmpty());
        assertEquals(5, courses.size());


    }

    @Test
    @DisplayName("Get All Teachers")
    public void shouldGetAllTeachers() {
      List<Teacher> teachers = server.getAllTeachers();
      assertNotNull(teachers);
      assertEquals(7, teachers.size());
      assertEquals("Frank", teachers.get(0).getName());
    }

    @Test
    @DisplayName("Should get student Teacher")
    public void shouldGetTeacher() {
        Student student = server.addStudent("Pauolo Maldini", "0000000");
        App.addStudentsCourse(server, student, "MATHS,ENGLISH,FINE ART,FURTHER MATHS,AGRIC".split(","));
        Teacher teacher = student.getTeacher();
        assertNotNull(teacher);

    }

    @Test
    @DisplayName("Should get student Teacher as JSON")
    public void shouldGetTeacherAsJson() {
        Student student = server.addStudent("Pauolo Maldini", "0000000");
       if(!student.checkIfRegistered()) App.addStudentsCourse(server, student, "MATHS,ENGLISH,FINE ART,FURTHER MATHS,AGRIC".split(","));
        String teacher = server.getStudentTeacherAsJson(student);
        assertNotNull(teacher);

    }


    @AfterAll
    public static void closeServer() {
        server.closeAll();
    }

}
