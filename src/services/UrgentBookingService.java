package services;

import entities.Appointment;
import factories.AppointmentFactory;
import repositories.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;

public class UrgentBookingService {

    private final AppointmentRepository repo;
    private final DoctorAvailabilityService availability;
    private final AppointmentFactory urgentFactory;

    public UrgentBookingService(AppointmentRepository repo, DoctorAvailabilityService availability,
                                AppointmentFactory urgentFactory) {
        this.repo = repo;
        this.availability = availability;
        this.urgentFactory = urgentFactory;
    }

    public Appointment bookNextAvailable(int patientId, int doctorId) {
        LocalDateTime nextSlot = LocalDateTime.now();


        while (repo.exists(doctorId, nextSlot)) {

            nextSlot = nextSlot.plusMinutes(30);

        }


        availability.check(doctorId, nextSlot);

        Appointment appointment = urgentFactory.create(patientId, doctorId, nextSlot);
        repo.save(appointment);

        return appointment;
    }
}
