package entities;

public class Doctor {
    private int id;
    private String doctor_name;
    private String specialization;

    public Doctor(int id, String doctor_name, String specialization) {
        this.id = id;
        this.doctor_name = doctor_name;
        this.specialization = specialization;
    }

    public int getId() { return id; }
    public String getDoctor_name() { return doctor_name; }
    public String getSpecialization() { return specialization; }

}
