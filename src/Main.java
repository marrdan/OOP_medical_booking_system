import db.PostegresDB;
import entities.Appointment;
import entities.Doctor;
import entities.Patient;
import exceptions.AppointmentNotFoundException;
import exceptions.DoctorUnavailableException;
import exceptions.TimeSlotAlreadyBookedException;
import factories.BookedAppointmentFactory;
import factories.UrgentAppointmentFactory;
import factories.AppointmentFactory;
import repositories.*;
import services.AppointmentService;
import services.DoctorAvailabilityService;
import services.UrgentBookingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        AppointmentRepository repo = new AppointmentRepositoryImpl(PostegresDB.getInstance());
        DoctorAvailabilityService availability = new DoctorAvailabilityService(repo);
        AppointmentService service = new AppointmentService(repo, availability);

        PatientRepository patientRepo = new PatientRepositoryImpl(PostegresDB.getInstance());
        DoctorRepository doctorRepo = new DoctorRepositoryImpl(PostegresDB.getInstance());

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("""
                    1. Book appointment
                    2. Cancel appointment
                    3. Doctor schedule
                    4. Patient visits
                    5. Book urgent appointment (next available slot)
                    6. Add patient or doctor
                    7. View all patients and doctors
                    0. Exit
                    """);
            System.out.print("Enter your choice: ");
            String input = sc.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {

                case 1 -> {
                    try {
                        System.out.print("Patient ID: ");
                        int p = Integer.parseInt(sc.nextLine());
                        System.out.print("Doctor ID: ");
                        int d = Integer.parseInt(sc.nextLine());

                        Patient patient = patientRepo.findById(p);
                        Doctor doctor = doctorRepo.findById(d);

                        if (patient == null) {
                            System.out.println("Patient not found!");
                            break;
                        }
                        if (doctor == null) {
                            System.out.println("Doctor not found!");
                            break;
                        }

                        System.out.println("Select appointment type: ");
                        System.out.println("1. Normal");
                        System.out.println("2. Urgent");
                        int typeChoice = Integer.parseInt(sc.nextLine());

                        AppointmentFactory factory;
                        if (typeChoice == 1) factory = new BookedAppointmentFactory();
                        else if (typeChoice == 2) factory = new UrgentAppointmentFactory();
                        else {
                            System.out.println("Unknown type! Defaulting to Normal.");
                            factory = new BookedAppointmentFactory();
                        }

                        LocalDateTime time = (typeChoice == 1
                                ? LocalDateTime.now().plusDays(1)
                                : LocalDateTime.now().plusHours(1))
                                .withSecond(0).withNano(0);

                        Appointment appointment = factory.create(p, d, time);

                        service.book(appointment);
                        System.out.println("Appointment booked successfully! Type: " + appointment.getStatus());

                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input! Please enter a number.");
                    } catch (TimeSlotAlreadyBookedException | DoctorUnavailableException e) {
                        System.out.println("Error booking appointment: " + e.getMessage());
                    }
                }

                case 2 -> {
                    try {
                        System.out.print("Appointment ID: ");
                        int id = Integer.parseInt(sc.nextLine());
                        service.cancel(id);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID! Please enter a number.");
                    } catch (AppointmentNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }

                case 3 -> {
                    try {
                        System.out.print("Doctor ID: ");
                        int d = Integer.parseInt(sc.nextLine());
                        List<Appointment> schedule = service.doctorSchedule(d);
                        schedule.stream()
                                .sorted((a1, a2) -> a1.getTime().compareTo(a2.getTime()))
                                .forEach(System.out::println);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID! Please enter a number.");
                    } catch (DoctorUnavailableException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }

                case 4 -> {
                    try {
                        System.out.print("Patient ID: ");
                        int p = Integer.parseInt(sc.nextLine());
                        List<Appointment> visits = service.patientVisits(p);
                        if (visits.isEmpty()) System.out.println("No appointments found.");
                        else visits.forEach(System.out::println);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID! Please enter a number.");
                    }
                }

                case 5 -> {
                    try {
                        System.out.print("Patient ID: ");
                        int p = Integer.parseInt(sc.nextLine());
                        System.out.print("Doctor ID: ");
                        int d = Integer.parseInt(sc.nextLine());

                        Patient patient = patientRepo.findById(p);
                        Doctor doctor = doctorRepo.findById(d);

                        if (patient == null) {
                            System.out.println("Patient not found!");
                            break;
                        }
                        if (doctor == null) {
                            System.out.println("Doctor not found!");
                            break;
                        }

                        UrgentBookingService urgentService = new UrgentBookingService(
                                repo, availability, new UrgentAppointmentFactory()
                        );
                        Appointment urgent = urgentService.bookNextAvailable(p, d);
                        System.out.println("Urgent appointment booked at: " + urgent.getTime());

                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input! Please enter a number.");
                    } catch (TimeSlotAlreadyBookedException | DoctorUnavailableException e) {
                        System.out.println("Error booking appointment: " + e.getMessage());
                    }
                }

                case 6 -> {
                    System.out.println("1. Add Patient");
                    System.out.println("2. Add Doctor");
                    int subChoice = Integer.parseInt(sc.nextLine());

                    switch (subChoice) {
                        case 1 -> {
                            System.out.print("Patient name: ");
                            String name = sc.nextLine();
                            System.out.print("Phone: ");
                            String phone = sc.nextLine();

                            Patient patient = new Patient(0, name, phone);
                            patientRepo.save(patient);
                            System.out.println("Patient added: " + name);
                        }
                        case 2 -> {
                            System.out.print("Doctor name: ");
                            String name = sc.nextLine();
                            System.out.print("Specialization: ");
                            String spec = sc.nextLine();

                            Doctor doctor = new Doctor(0, name, spec);
                            doctorRepo.save(doctor);
                            System.out.println("Doctor added: " + name);
                        }
                        default -> System.out.println("Unknown option!");
                    }
                }

                case 7 -> {
                    System.out.println("All patients:");
                    List<Patient> allPatients = patientRepo.findAll();
                    if (allPatients.isEmpty()) System.out.println("No patients added yet.");
                    else allPatients.forEach(System.out::println);

                    System.out.println("All doctors:");
                    List<Doctor> allDoctors = doctorRepo.findAll();
                    if (allDoctors.isEmpty()) System.out.println("No doctors added yet.");
                    else allDoctors.forEach(System.out::println);
                }

                case 0 -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }

                default -> System.out.println("Unknown option! Please choose 0-7.");
            }
        }
    }
}
