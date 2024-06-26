package ru.gb.javajun;

import org.h2.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DB {
    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test")) {
            creatTablePerson(connection);
            creatTableDepartment(connection);
            insertDataPerson(connection, creatTenPerson());
            insertDataDepartment(connection, creatListDepartment());
            selectTablePerson(connection);
            System.out.println("_________________");
            System.out.println(getPersonDepartmentName(connection, 2).getName());
            System.out.println("_________________");
            getPersonDepartments(connection).forEach((k, v) -> System.out.println(k + " " + v));
            System.out.println("______________________");
            getDepartmentPersons(connection).forEach((k, v) -> System.out.println(k + " " + v));
        } catch (SQLException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private static List<Person> creatTenPerson() {
        List<Person> personList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            int age = ThreadLocalRandom.current().nextInt(18, 65);
            boolean active = ThreadLocalRandom.current().nextBoolean();
            int department = ThreadLocalRandom.current().nextInt(1, 4);
            Person person = new Person(i, "Person #" + i, age, active, department);
            personList.add(person);

        }
        return personList;
    }

    private static List<Department> creatListDepartment() {
        List<Department> departments = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {

            Department department = new Department(i);
            departments.add(department);
        }
        return departments;
    }

    private static void creatTablePerson(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    create table person (
                    id bigint primary key,
                    name varchar (256),
                    age integer,
                    active boolean,
                    department bigint
                    )
                    """);
        } catch (SQLException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private static void creatTableDepartment(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    create table department (
                    id bigint primary key,
                    name varchar (256)
                    )
                    """);

        } catch (SQLException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private static void selectTablePerson(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                    select *
                    from person
                    """);
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String name = resultSet.getString(2);
                int age = resultSet.getInt(3);
                boolean active = resultSet.getBoolean(4);
                long department = resultSet.getLong(5);
                System.out.println(new Person(id, name, age, active, department));
            }
        } catch (SQLException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private static void insertDataPerson(Connection connection, List<Person> personList) {
        try (Statement statement = connection.createStatement()) {
            StringBuilder builder = new StringBuilder("insert into person(id, name, age, active, department) values \n");
            for (int i = 0; i < personList.size(); i++) {
                builder.append(String.format("(%s, '%s', %s, %s, %s)", personList.get(i).getId(),
                        personList.get(i).getName(), personList.get(i).getAge(),
                        personList.get(i).isActive(), personList.get(i).getDepartment().getId()));
                if (i != personList.size() - 1) {
                    builder.append(", \n");
                }
            }
            statement.executeUpdate(builder.toString());
        } catch (SQLException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private static void insertDataDepartment(Connection connection, List<Department> departments) {
        try (Statement statement = connection.createStatement()) {
            StringBuilder builder = new StringBuilder("insert into department(id, name) values \n");
            for (int i = 0; i < departments.size(); i++) {
                builder.append(String.format("(%s, '%s')", departments.get(i).getId(),
                        departments.get(i).getName()));
                if (i != departments.size() - 1) {
                    builder.append(", \n");
                }
            }
            statement.executeUpdate(builder.toString());
        } catch (SQLException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private static Department getPersonDepartmentName(Connection connection, long personId) throws SQLException {
        Department department = null;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                    select department
                    from person
                    where id =
                    """ + personId);
            while (resultSet.next()) {
                department = new Department(resultSet.getLong("department"));
            }
        } catch (SQLException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
        return department;
    }

    /**
     * Пункт 5
     */
    private static Map<Person, Department> getPersonDepartments(Connection connection) throws SQLException {
        Map<Person, Department> map = new HashMap<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                    select person.*, department.id
                    from person
                    join department
                    on person.department = department.id
                    """);
            while (resultSet.next()) {
                Person person = new Person(resultSet.getLong("id"),
                        resultSet.getString("person.name"), resultSet.getInt("age"),
                        resultSet.getBoolean("active"),
                        resultSet.getLong("department.id"));
                Department department = new Department(resultSet.getLong("department.id"));
                map.put(person, department);
            }
        } catch (SQLException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
        return map;
    }

    /**
     * Пункт 6
     */
    private static Map<Department, List<Person>> getDepartmentPersons(Connection connection) throws SQLException {
        Map<Department, List<Person>> map = new HashMap<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                    select person.*, department.id
                    from department
                    left join person
                    on department.id = person.department
                    """);
            while (resultSet.next()) {
                Person person = new Person(resultSet.getLong("id"),
                        resultSet.getString("person.name"), resultSet.getInt("age"),
                        resultSet.getBoolean("active"),
                        resultSet.getLong("department.id"));
                Department department = new Department(resultSet.getLong("department.id"));
                if (map.containsKey(department)) {
                    List<Person>personList = new ArrayList<>(List.copyOf(map.remove(department)));
                    personList.add(person);
                    map.put(department, personList);
                } else {
                    List<Person> personList = new ArrayList<>();
                    if (person.getName() != null) {
                        personList.add(person);
                    }
                    map.put(department, personList);
                }
            }
        } catch (SQLException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
        return map;
    }
}

