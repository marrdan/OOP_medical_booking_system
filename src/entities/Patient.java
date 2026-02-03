package entities;

public class Patient {
    private int id;
    private String patient_name;
    private String phone;

    public Patient(int id, String patient_name, String phone) {
        this.id = id;
        this.patient_name = patient_name;
        this.phone = phone;
    }

    public int getId() { return id; }


    @Override
    public String toString() {
        return id + " " + patient_name + " " + phone;
    }
}
