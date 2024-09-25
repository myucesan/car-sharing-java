package carsharing.dao.impl;

import carsharing.dao.CustomerDAO;
import carsharing.dao.model.Car;
import carsharing.dao.model.CarToCustomer;
import carsharing.dao.model.Customer;
import carsharing.dao.util.DatabaseManager;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAOImpl implements CustomerDAO {
    DatabaseManager dm;

    public CustomerDAOImpl(DatabaseManager dm) {
        this.dm = dm;
    }

    @Override
    public void createCustomer(String name) {
        Connection conn = dm.getConn();
        try {
            PreparedStatement query = conn.prepareStatement("""
        INSERT INTO Customer (name, rented_car_id)
        VALUES (?, null)
""");

            query.setString(1, name);
            query.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        Connection conn = dm.getConn();
        List<Customer> list = new ArrayList<>();
        ResultSet rs;
        try {
            Statement s = conn.createStatement();
            rs = s.executeQuery("""
                SELECT * FROM Customer
    """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                if (!rs.next()) {
                    break;
                }
                list.add(new Customer(rs.getInt("ID"), rs.getString("name"), rs.getInt("RENTED_CAR_ID")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        return list;
    }

    @Override
    public List<CarToCustomer> getRentedCarsByCustomerId(int id) {
        Connection conn = dm.getConn();
        ResultSet rs;
        try {
            PreparedStatement ps = conn.prepareStatement("""
            SELECT cr.name as carName, co.name as companyName FROM Customer c JOIN Car cr ON c.rented_car_id = cr.id
            JOIN Company co ON cr.company_id = co.id AND c.id = ?
    """);
            ps.setInt(1, id);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dm.carToCustomerMapper(rs);
    }

    @Override
    public void returnCar(int id) {
        Connection conn = dm.getConn();
        try {
            PreparedStatement ps = conn.prepareStatement("""
            UPDATE Customer
            SET rented_car_id = null
            where id = ?
    """);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isCarRented(int customerId) {
        Connection conn = dm.getConn();
        ResultSet rs;
        boolean hasRows;
        try {
            PreparedStatement ps = conn.prepareStatement("""
            SELECT * FROM Customer
            WHERE id = ? AND rented_car_id IS NOT NULL
""");
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            hasRows = rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return hasRows;
    }

    @Override
    public void rentACar(int customerId, int carId) {
        Connection conn = dm.getConn();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("""
            UPDATE Customer
            SET rented_car_id = ?
            WHERE ID = ?
    """);
            ps.setInt(1, carId);
            ps.setInt(2, customerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
