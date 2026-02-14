package repositories;

import db.IDB;
import entities.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientRepositoryImpl implements PatientRepository {

    private final IDB db;

    public PatientRepositoryImpl(IDB db) {
        this.db = db;
    }

    @Override
    public void save(Patient p) {
        String sql = "INSERT INTO patients(patient_name, phone) VALUES (?, ?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.toString());
            ps.setString(2, p.toString());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Patient findById(int id) {
        String sql = "SELECT * FROM patients WHERE id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            return new Patient(rs.getInt("id"),
                    rs.getString("patient_name"),
                    rs.getString("phone"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Patient> findAll() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Patient(rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("phone")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
