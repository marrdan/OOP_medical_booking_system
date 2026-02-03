package repositories;

import db.IDB;
import entities.Appointment;
import exceptions.AppointmentNotFoundException;
import exceptions.DoctorUnavailableException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentRepositoryImpl implements AppointmentRepository, Repository<Appointment> {

    private final IDB db;

    public AppointmentRepositoryImpl(IDB db) {
        this.db = db;
    }

    // AppointmentRepository методы

    @Override
    public void save(Appointment a) {
        saveAppointment(a);
    }

    @Override
    public void cancel(int id) {
        String sql = "UPDATE appointments SET status='CANCELLED' WHERE id=?";

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0)
                throw new AppointmentNotFoundException("Appointment not found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Appointment findById(int id) {
        return findAppointmentById(id);
    }

    @Override
    public List<Appointment> findByDoctor(int doctorId) {
        List<Appointment> list = findByField("doctor_id", doctorId);

        if (list.isEmpty()) {
            throw new DoctorUnavailableException("No appointments found for doctor with id " + doctorId);
        }

        return list;
    }

    @Override
    public List<Appointment> findByPatient(int patientId) {
        return findByField("patient_id", patientId);
    }

    @Override
    public boolean exists(int doctorId, LocalDateTime time) {
        String sql = """
            SELECT 1 FROM appointments
            WHERE doctor_id=? AND appointment_time=? AND status='BOOKED'
        """;

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setTimestamp(2, Timestamp.valueOf(time));
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Repository<Appointment> methods

    @Override
    public void delete(int id) {
        cancel(id);
    }

    @Override
    public List<Appointment> findAll() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments";

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Appointment appointment = new Appointment.Builder()
                        .id(rs.getInt("id"))
                        .patientId(rs.getInt("patient_id"))
                        .doctorId(rs.getInt("doctor_id"))
                        .time(rs.getTimestamp("appointment_time").toLocalDateTime())
                        .status(rs.getString("status"))
                        .build();

                list.add(appointment);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list.stream()
                .filter(a -> "BOOKED".equals(a.getStatus()))
                .sorted((a1, a2) -> a1.getTime().compareTo(a2.getTime()))
                .collect(Collectors.toList());
    }



    private void saveAppointment(Appointment a) {
        String sql = """
            INSERT INTO appointments(patient_id, doctor_id, appointment_time, status)
            VALUES (?, ?, ?, ?)
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setTimestamp(3, Timestamp.valueOf(a.getTime()));
            ps.setString(4, a.getStatus());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                a.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Appointment findAppointmentById(int id) {
        String sql = "SELECT * FROM appointments WHERE id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new AppointmentNotFoundException("Appointment not found");

            return new Appointment.Builder()
                    .id(rs.getInt("id"))
                    .patientId(rs.getInt("patient_id"))
                    .doctorId(rs.getInt("doctor_id"))
                    .time(rs.getTimestamp("appointment_time").toLocalDateTime())
                    .status(rs.getString("status"))
                    .build();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Appointment> findByField(String field, int id) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE " + field + "=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Appointment appointment = new Appointment.Builder()
                        .id(rs.getInt("id"))
                        .patientId(rs.getInt("patient_id"))
                        .doctorId(rs.getInt("doctor_id"))
                        .time(rs.getTimestamp("appointment_time").toLocalDateTime())
                        .status(rs.getString("status"))
                        .build();

                list.add(appointment);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
