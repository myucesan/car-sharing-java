package carsharing.dao.impl;

import carsharing.dao.CarDAO;
import carsharing.dao.model.Car;
import carsharing.dao.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDAOImpl implements CarDAO {
    DatabaseManager dm;

    public CarDAOImpl(DatabaseManager dm) {
        this.dm = dm;
    }

    @Override
    public List<Car> getAllCarsByCompanyId(int companyId) {
        Connection conn = dm.getConn();
        ResultSet result;
        try {
            PreparedStatement ps = conn.prepareStatement("""
    SELECT * FROM Car WHERE company_id = ?
    ORDER BY ID
    """);

            ps.setInt(1, companyId);
            result = ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    return dm.carMapper(result);
    }

    @Override
    public void createCarByCompanyId(String carName, int companyId) {
        Connection conn = dm.getConn();

        try {
            PreparedStatement ps = conn.prepareStatement("""
        INSERT INTO Car (name, company_id)
        VALUES(?, ?)
""");
            ps.setString(1, carName);
            ps.setInt(2, companyId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public List<Car> getAllAvailableCarsByCompanyId(int companyId) {
        Connection conn = dm.getConn();
        ResultSet rs;
        try {
            PreparedStatement ps = conn.prepareStatement("""
            SELECT * FROM Car LEFT JOIN Customer ON Car.ID = Customer.rented_car_id
            WHERE Car.company_id = ? AND Customer.rented_car_id IS NULL
    """);
            ps.setInt(1, companyId);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dm.carMapper(rs);
    }
}
