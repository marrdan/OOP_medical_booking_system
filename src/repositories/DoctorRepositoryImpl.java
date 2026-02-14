package repositories;

import db.IDB;
import entities.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorRepositoryImpl implements DoctorRepository {

    private final IDB db;

    public DoctorRepositoryImpl(IDB db) {
        this.db = db;
    }

    @Override
    public void save(Doctor d) {
        String sql = "INSERT INTO doctors(doctor_name, specialization) VALUES (?, ?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, d.getDoctor_name());
            ps.setString(2, d.getSpecialization());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Doctor findById(int id) {
        String sql = "SELECT * FROM doctors WHERE id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return new Doctor(
                    rs.getInt("id"),
                    rs.getString("doctor_name"),
                    rs.getString("specialization")
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Doctor> findAll() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors";

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Doctor(
                        rs.getInt("id"),
                        rs.getString("doctor_name"),
                        rs.getString("specialization")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
