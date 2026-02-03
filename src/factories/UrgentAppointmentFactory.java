package factories;

import entities.Appointment;
import java.time.LocalDateTime;

public class UrgentAppointmentFactory implements AppointmentFactory {

    @Override
    public Appointment create(int patientId, int doctorId, LocalDateTime time) {
        return new Appointment.Builder()
                .patientId(patientId)
                .doctorId(doctorId)
                .time(time)
                .status("URGENT")
                .build();
    }
}
