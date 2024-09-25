package carsharing.dao;
import carsharing.dao.model.Car;
import carsharing.dao.model.Company;

import java.sql.SQLException;
import java.util.List;

public interface CompanyDAO {
    void create(String name);
    Company getById(int id);
    List<Company> getAll();
}
