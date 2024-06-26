package ru.gb.javajun;

public class Person {

    private long id;

    private String name;

    private Integer age;

    private boolean active;

    private Department department;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Person(long id, String name, Integer age, boolean active, long department) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.active = active;
        this.department = new Department(department);
    }
    public Person(String name, long department) {
        this.name = name;
        this.department = new Department(department);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", active=" + active +
                ", department=" + department.getId() +
                '}';
    }
}
