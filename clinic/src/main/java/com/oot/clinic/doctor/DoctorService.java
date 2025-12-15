package com.oot.clinic.doctor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * Adds a new doctor to the database
     * @param doctor the Doctor object that's being added
     */
    public Doctor addDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    /**
     * Returns a list of doctors mapped as DoctorDTO objects
     * (to not give out personal information like PESEL)
     */
    public List<DoctorDTO> getDoctors() {
        return doctorRepository.findAll().stream()
                .map(DoctorDTO::new)
                .toList();
    }

    /**
     * Returns an optional Doctor object based on the given id
     * @param id id of the doctor
     */
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    /**
     * Deletes the doctor from the database with specific id if he exists
     * @param id id of a doctor that's to be deleted
     * @return boolean if he was successfully deleted or not
     */
    public void deleteDoctorById(Long id) throws Exception {
        if (!doctorRepository.existsById(id)) {
            throw new Exception("Doctor does not exist.");
        }
        doctorRepository.deleteById(id);
    }
}
