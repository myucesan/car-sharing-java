package carsharing.dao;

import carsharing.dao.model.CarToCustomer;
import carsharing.dao.model.Customer;

import java.util.List;

public interface CustomerDAO {
    void createCustomer(String name);
    List<Customer> getAllCustomers();
    List<CarToCustomer> getRentedCarsByCustomerId(int id);
    void returnCar(int id);
    boolean isCarRented(int customerId);
    void rentACar(int customerId, int carId);
}
