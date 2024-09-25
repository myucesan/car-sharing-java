package carsharing.dao.util;

import carsharing.dao.model.Car;
import carsharing.dao.model.CarToCustomer;
import carsharing.dao.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private String dbName;
    private Connection conn;

    public DatabaseManager(String dbName) {
        this.dbName = dbName;
        String conString = String.format("jdbc:h2:./src/carsharing/db/%s", dbName);
        try {
            conn = DriverManager.getConnection(conString);
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("A SQL exception is thrown:" + e.getMessage());
        }
            }

    public Connection getConn() {
        return conn;
    }

    public int prepareDatabase(String db) {

        int affectedRows = 0;
        try {
            Statement st = conn.createStatement();
            affectedRows = st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS COMPANY (
                                ID INT PRIMARY KEY AUTO_INCREMENT,
                                NAME VARCHAR UNIQUE NOT NULL
                            )
                    """);
            affectedRows = st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS CAR (
                                ID INT PRIMARY KEY AUTO_INCREMENT,
                                NAME VARCHAR UNIQUE NOT NULL,
                                COMPANY_ID INT NOT NULL,
                                CONSTRAINT fk_company FOREIGN KEY (company_id) REFERENCES COMPANY(ID)
                            );
                    """);

            affectedRows = st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS CUSTOMER (
                                ID INT PRIMARY KEY AUTO_INCREMENT,
                                NAME VARCHAR UNIQUE NOT NULL,
                                RENTED_CAR_ID INT,
                                FOREIGN KEY (rented_car_id) REFERENCES car(ID)
                            );
                    """);
//            conn.close();
        } catch (SQLException e) {
            System.out.println("A SQL exception is thrown:" + e.getMessage());
        }

        return affectedRows;
    }

    public List<Car> carMapper(ResultSet rs) {
        List<Car> list = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) {
                    break;
                }
                list.add(new Car(rs.getInt("ID"), rs.getString("name"), rs.getInt("company_id")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        return list;
    }

    public List<CarToCustomer> carToCustomerMapper(ResultSet rs) {
        List<CarToCustomer> list = new ArrayList<>();

        while (true) {
            try {
                if (!rs.next()) {
                    break;
                }
                list.add(new CarToCustomer(rs.getString("carName"), rs.getString("companyName")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        return list;
    }
}
