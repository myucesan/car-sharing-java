package carsharing.dao;

import carsharing.dao.model.Car;

import java.util.List;

public interface CarDAO {
    List<Car> getAllCarsByCompanyId(int companyId);
    void createCarByCompanyId(String name, int companyId);
    List<Car> getAllAvailableCarsByCompanyId(int companyId);
}
