package repositories;

import entities.Doctor;
import java.util.List;

public interface DoctorRepository {
    void save(Doctor d);
    Doctor findById(int id);
    List<Doctor> findAll();
}
