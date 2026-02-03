package services;

import exceptions.TimeSlotAlreadyBookedException;
import repositories.AppointmentRepository;

import java.time.LocalDateTime;

public class DoctorAvailabilityService {

    private final AppointmentRepository repo;

    public DoctorAvailabilityService(AppointmentRepository repo) {
        this.repo = repo;
    }

    public void check(int doctorId, LocalDateTime time) {
        if (repo.exists(doctorId, time)) {
            throw new TimeSlotAlreadyBookedException("Time slot already booked");
        }
    }
}
