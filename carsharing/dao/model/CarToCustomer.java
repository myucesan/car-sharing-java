package carsharing.dao.model;

public class CarToCustomer {
    String name;
    String companyName;

    public CarToCustomer(String name, String companyName) {
        this.name = name;
        this.companyName = companyName;
    }

    public String getName() {
        return name;
    }

    public String getCompanyName() {
        return companyName;
    }
}
