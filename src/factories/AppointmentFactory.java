package factories;

import entities.Appointment;
import java.time.LocalDateTime;

public interface AppointmentFactory {
    Appointment create(int patientId, int doctorId, LocalDateTime time);
}
