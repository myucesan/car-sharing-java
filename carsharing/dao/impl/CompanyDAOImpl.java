package carsharing.dao.impl;

import carsharing.dao.CompanyDAO;
import carsharing.dao.model.Car;
import carsharing.dao.model.Company;
import carsharing.dao.util.DatabaseManager;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAOImpl implements CompanyDAO {
    private DatabaseManager dm;
    List<Company> list = new ArrayList<Company>();
    public CompanyDAOImpl(DatabaseManager dm) {
        this.dm = dm;
    }

    @Override
    public void create(String name) {
        Connection conn = dm.getConn();
        try {
            PreparedStatement addCompanyString = conn.prepareStatement("""
        INSERT INTO Company (name)
            VALUES (?)
""");
            addCompanyString.setString(1, name);
            addCompanyString.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Company getById(int id) {
        return null;
    }

    @Override
    public List<Company> getAll() {
        Connection conn = dm.getConn();
        List<Company> list = new ArrayList<>();
        ResultSet result;
        try {
            Statement query = conn.createStatement();
            result = query.executeQuery("""
                    SELECT * FROM Company
                    """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                if (!result.next()) {
                    break;
                }
                list.add(new Company(result.getInt("ID"), result.getString("name")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return list;
}
}
