package entities;

import java.time.LocalDateTime;

public class Appointment {

    private int id;
    private int patientId;
    private int doctorId;
    private LocalDateTime time;
    private String status;

    private Appointment() {
        // приватный конструктор, объекты создаются через Builder
    }

    // --------------------------
    // Геттеры
    // --------------------------
    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public LocalDateTime getTime() { return time; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", time=" + time +
                ", status='" + status + '\'' +
                '}';
    }

    // --------------------------
    // Builder
    // --------------------------
    public static class Builder {
        private final Appointment appointment;

        public Builder() {
            appointment = new Appointment();
        }

        public Builder id(int id) {
            appointment.id = id;
            return this;
        }

        public Builder patientId(int patientId) {
            appointment.patientId = patientId;
            return this;
        }

        public Builder doctorId(int doctorId) {
            appointment.doctorId = doctorId;
            return this;
        }

        public Builder time(LocalDateTime time) {
            appointment.time = time;
            return this;
        }

        public Builder status(String status) {
            appointment.status = status;
            return this;
        }

        public Appointment build() {
            // Проверяем обязательные поля
            if (appointment.patientId == 0 || appointment.doctorId == 0 || appointment.time == null) {
                throw new IllegalStateException("Patient, doctor, and time must be set");
            }
            if (appointment.status == null) {
                appointment.status = "BOOKED";
            }
            return appointment;
        }
    }
}
