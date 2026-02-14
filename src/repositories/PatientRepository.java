package repositories;

import entities.Patient;
import java.util.List;

public interface PatientRepository {
    void save(Patient p);
    Patient findById(int id);
    List<Patient> findAll();
}
