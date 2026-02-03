package repositories;

import entities.Appointment;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository {
    void save(Appointment appointment);
    void cancel(int id);
    Appointment findById(int id);
    List<Appointment> findByDoctor(int doctorId);
    List<Appointment> findByPatient(int patientId);
    boolean exists(int doctorId, LocalDateTime time);
}

