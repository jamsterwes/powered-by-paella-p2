package eu.poweredbypaella.paellapos.data;

public class Employee {
    public String name;
    public boolean isAdmin;

    public Employee(String name, boolean isAdmin) {
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
