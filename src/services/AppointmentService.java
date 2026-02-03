package services;

import entities.Appointment;
import repositories.AppointmentRepository;

import java.util.List;

public class AppointmentService {

    private final AppointmentRepository repo;
    private final DoctorAvailabilityService availability;


    public AppointmentService(AppointmentRepository repo, DoctorAvailabilityService availability) {
        this.repo = repo;
        this.availability = availability;
    }

    public void book(Appointment a) {
        availability.check(a.getDoctorId(), a.getTime());
        repo.save(a);
    }

    public void cancel(int id) {
        repo.cancel(id);
        System.out.println("Appointment with "+ id+ "id" + " cancelled successfully");
    }

    public List<Appointment> doctorSchedule(int doctorId) {
        return repo.findByDoctor(doctorId);
    }

    public List<Appointment> patientVisits(int patientId) {

        return repo.findByPatient(patientId);
    }

}
