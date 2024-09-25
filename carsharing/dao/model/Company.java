package carsharing.dao.model;

public class Company {
    int ID;
    String name;

    public Company(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }
}
