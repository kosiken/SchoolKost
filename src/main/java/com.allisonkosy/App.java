// YOU MUST BE CONNECTED TO INTERNET FOR THIS CODE TO WORK


package com.allisonkosy;
import com.allisonkosy.entity.*;

import com.allisonkosy.runner.Server;
import com.google.common.base.Splitter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


public class App

{
    public static final Logger LOGGER = LogManager.getLogger(App.class);
    public static Student currentStudent = null;
    public static Teacher randomTeacher = null;
    public static    String courses = "MATHS,ENGLISH,FINE ART,FURTHER MATHS,AGRIC,BIOLOGY,CHEMISTRY,GOVERNMENT,PHYSICS,LITERATURE,COMPUTER SCIENCE,FRENCH";
    public static final String teachers = "Frank,John,Shaun,Manda,Imande,Yoda,Mikail";
    public static final String studentCsv =
            "Giffard" +
                    ",Maynord" +
                    ",Thornie" +
                    ",Rickie" +
                    ",Reed" +
                    ",Benedetto" +
                    ",Edmund" +
                    ",Rhodie" +
                    ",Vilma" +
                    ",Phaedra";

    public static void main(String[] args) {
//        ObjectMapper mapper = new ObjectMapper();
        Server server = new Server();
        initializeServer(server);
        Scanner scanner = new Scanner(System.in);
        printIntroduction();
        int ans = 1;
        while (ans == 1) {
            if(currentStudent != null) {
               ans = authenticated(server, scanner);
            }
            else ans = unauthenticated(server, scanner);
            System.out.println(ans);

        }
        server.closeAll();
    }

    public static void printIntroduction() {
        System.out.println("Welcome to SchoolKost Portal");

    }

    public static int authenticated(Server server, Scanner in) {

        String name = currentStudent.getName();
        System.out.println("Input 1 to register new course for " + name + "\nInput 2 to see all " + name + "'s courses\n"
                +  "Input 3 to see " + name + "'s teacher\n" + "Input 4 to logout\n" +
                "Input >5 to exit");

        int choice = 0;
        int ans = 1;

        try {

            choice = in.nextInt();
            System.out.println(choice);
            if(choice == 1) {
                registerForCourse(server, in);

            }
            else if(choice == 2) {
                printAllRegisteredCourses(server);
            }

            else if(choice == 3) {
                printStudentTeacher(server);
            }

            else if(choice == 4) {
                currentStudent = null;
            }
            else {
                ans = 0;
            }

        }
        catch (InputMismatchException exception) {
            System.out.println("Incorrect input");
            in.nextLine();

        }

        return ans;
    }

    public static int unauthenticated(Server server, Scanner in) {
//        in.nextLine();
        System.out.println("Input 1 to register new student\nInput 2 to log in as existing student\n"
                + "Input 3 to get the list of every student in the school\n" +
                "Input 4 to get all teachers in the school\n" + "\nInput >5 to exit");
        System.out.print("choice: ");

        int choice = 0;
        int ans = 1;

        try {
            choice = in.nextInt();
            System.out.println(choice);
            if(choice == 1) {
                register(server, in);
            }
            else if(choice == 2) {
                login(server, in);
            }
            else if(choice == 3) {
                printAllStudents( server);
            }
            else if(choice == 4) {
                printAllTeachers(server);
            }
            else  {
                ans = 0;
            }

        }
        catch (InputMismatchException exception) {
            System.out.println("Incorrect input");
            in.nextLine();
        }


        return ans;
    }

    public static void register(Server server, Scanner in) {
        in.nextLine();

        String[] questions = {
                "What is your username: ",
                "What is your password: "
        };
        int index = 0;
        String name = "";
        String password = "";

        while (index <  2) {
            System.out.print(questions[index]);

            if(index<1)  name = in.nextLine();
            else {
                password = in.nextLine();
            }
            index++;
        }
        currentStudent = server.registerStudent(name.toLowerCase(Locale.ROOT), password);
        if (currentStudent == null) {
            System.out.println("must use unique and defined name" );
            System.out.println("must use defined password and greater than 5 characters");
        }
    }

    public static void printAllCourses(){
        Iterable<String> strings = Splitter.on(',').trimResults().omitEmptyStrings().split(courses);
       printGrid(strings);
    }
    public static void printGridLoginable(List<Loginable> values) {
        StringBuilder builder = new StringBuilder();
        Integer i = 0;
        for (Loginable v: values) {
            String s  = v.getName();
            if(i % 2 == 1) {
                builder.append("        " )
                        .append((i +1 ) + ". "+ s+ '\n');
            }
            else {
                builder.append((i +1 ) + ". "+ s );
            }
            i++;
        }

        System.out.println(builder);
    }
    public static void printGrid(Iterable<String> strings) {
        StringBuilder builder = new StringBuilder();
        Integer i = 0;
        for (String s: strings) {
            if(i % 2 == 1) {
                builder.append("        " )
                        .append((i +1 ) + ". "+ s+ '\n');
            }
            else {
                builder.append((i +1 ) + ". "+ s );
            }
            i++;
        }

        System.out.println(builder);
    }


    public static void initializeServer(Server server) {



        server.addBulkCourses(courses);
        server.addBulkTeachers(teachers);
        randomTeacher = server.getRandomTeacher();
        server.addBulkStudents(studentCsv);



    }

    public static void addStudentsCourse(Server server, Student student, String[] courses) {

        for (String s : courses) {
            server.registerForCourse(student, s);
            if(!student.checkLessThan()) break;
        }
    }


    public static void printAllStudents(Server server) {
        List students = server.getAllStudents();
        System.out.println("Showing " +  students.size() + " student(s) default password = '000000' ");
        printGridLoginable(students);

    }


    public static void printAllTeachers(Server server) {
        List teachers = server.getAllTeachers();
        System.out.println( "Showing " + teachers.size() + " teacher(s)");
        printGridLoginable(teachers);

    }

    public static void login(Server server, Scanner in) {

        printAllStudents(server);
        System.out.println("Input one of the following username and password '000000' to log in or use yours");
        Student user = null;
        String[] questions = {
                "What is your username: ",
                "What is your password: "
        };
        int index = 0;
        String name = "";
        String password = "";
        in.nextLine();

        while (index <  2) {
            System.out.print(questions[index]);

            if(index<1) {
                name = in.nextLine();


            }
            else {
                password = in.nextLine();

            }
            index++;

        }
        user = (Student) server.login("students", name, password);
        if( user != null ) {

            currentStudent = user;
            LOGGER.info(currentStudent);

        }
        else {
            System.out.println("Invalid username or password");
        }

    }
    public static void getTeacherAssignedToStudent(Server server) {
        if(currentStudent == null) return;
//       System.out.println(server.getStudentTeacherAsJson());
    }

    public static void printAllCourses(Server server) {
        List<Course> courses = server.getAllCourses();
        StringBuilder stringBuilder = new StringBuilder();
        int size = courses.size();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(courses.get(i).getTitle());
            if(i < (size - 1)) {
                stringBuilder.append(',');
            }

        }
        Iterable<String> strings = Splitter.on(',').trimResults().omitEmptyStrings().split(stringBuilder.toString());

        printGrid(strings);
    }

    public static void registerForCourse(Server server, Scanner in) {
        printAllCourses(server);
        System.out.println("Input a name of a course to select or names of courses separated by commas ',' ");
        System.out.print("Course(s): ");
        in.nextLine();
        String courseAns = in.nextLine();
        Iterable<String> strings = Splitter.on(',').trimResults().omitEmptyStrings().split(courseAns.toUpperCase(Locale.ROOT));

        for (String s: strings) {
            server.registerForCourse(currentStudent, s);
            if(!currentStudent.checkLessThan()) {
                LOGGER.error(currentStudent.getName() + " has already registerd 7 courses");
                break;
            }
        }

    }

    public static void printAllRegisteredCourses(Server server) {
        List<CourseRegistration> courses = server.getAllRegisteredCourses(currentStudent);
        for (CourseRegistration c: courses) {
            System.out.println(c.getCourse().getTitle());
        }

    }

    public static void printStudentTeacher(Server server) {
        if(!currentStudent.checkIfRegistered()) {
            LOGGER.info("You do not have the required number of courses");
            return;
        }
        String s = server.getStudentTeacherAsJson(currentStudent);
        System.out.println(s);
    }



}
