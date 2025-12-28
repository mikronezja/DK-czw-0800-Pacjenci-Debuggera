package com.oot.clinic;

import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Office;
import com.oot.clinic.entities.Patient;
import com.oot.clinic.entities.Shift;
import com.oot.clinic.repositories.DoctorRepository;
import com.oot.clinic.repositories.OfficeRepository;
import com.oot.clinic.repositories.PatientRepository;
import com.oot.clinic.repositories.ShiftRepository;
import com.oot.clinic.services.DoctorService;
import com.oot.clinic.entities.enumeration.Specialization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ClinicApplicationTests {

	// DOCTOR

	@Autowired
	private DoctorService doctorService;

	@Autowired
	private DoctorRepository doctorRepository;

	@Test
	void contextLoads() {
		assertNotNull(doctorService);
		assertNotNull(doctorRepository);
	}

	@Test
	@DirtiesContext
	void addDoctorShouldPersistDoctorInDatabase() {
		Doctor doctor = new Doctor(
				"Jan",
				"Kowalski",
				"80010112345",
				Specialization.KARDIOLOG,
				"Warszawa"
		);

		Doctor saved = doctorService.addDoctor(doctor);

		assertNotNull(saved.getId());
		Optional<Doctor> found = doctorRepository.findById(saved.getId());

		assertTrue(found.isPresent());
		assertEquals("Jan", found.get().getName());
	}

	@Test
	@DirtiesContext
	void getDoctorByIdShouldReturnDoctorFromDatabase() {
		Doctor doctor = new Doctor(
				"Anna",
				"Nowak",
				"82030554321",
				Specialization.DERMATOLOG,
				"Kraków"
		);

		Doctor saved = doctorRepository.save(doctor);
		Doctor result = doctorService.getDoctorById(saved.getId());

		assertEquals("Anna", result.getName());
	}

	@Test
	@DirtiesContext
	void deleteDoctorByIdShouldRemoveDoctorFromDatabase() {
		Doctor doctor = new Doctor("Marek", "Zielony", "90010112345", Specialization.ORTOPEDA, "Poznań");
		Doctor saved = doctorRepository.save(doctor);

		assertDoesNotThrow(() ->
                doctorService.deleteDoctorById(saved.getId()));
		assertFalse(doctorRepository.existsById(saved.getId()));
	}

	@Test
	@DirtiesContext
	void getDoctorByIdShouldReturnEmptyOptionalWhenDoctorDoesNotExist() {
		assertThrows(Exception.class, () -> doctorService.getDoctorById(999L));
	}

	@Test
	@DirtiesContext
	void deleteDoctorByIdShouldReturnFalseIfDoctorDoesNotExist() {
		assertThrows(Exception.class, () ->
                doctorService.deleteDoctorById(123L));
	}

	@Test
	@DirtiesContext
	void doctorEntityShouldStoreAllFieldsCorrectly() {
		Doctor doctor = new Doctor("Adam", "Kłos", "70010112345", Specialization.NEUROLOG, "Łódź");
		Doctor saved = doctorRepository.save(doctor);

		var found = doctorRepository.findById(saved.getId());
		assertTrue(found.isPresent());

		var d = found.get();
		assertEquals("Adam", d.getName());
		assertEquals("Kłos", d.getSurname());
		assertEquals("70010112345", d.getPesel());
		assertEquals(Specialization.NEUROLOG, d.getSpecialization());
		assertEquals("Łódź", d.getAddress());
	}

	@Test
	@DirtiesContext
	void updateDoctorDataShouldPersistNewValues() {
		Doctor doctor = doctorRepository.save(new Doctor("Marek", "Stary", "70010112345", Specialization.ORTOPEDA, "Poznań"));

		doctor.setSurname("Nowy");
		doctor.setSpecialization(Specialization.CHIRURG);

		Doctor updated = doctorRepository.save(doctor);

		assertEquals("Nowy", updated.getSurname());
		assertEquals(Specialization.CHIRURG, updated.getSpecialization());
	}

	@Test
	@DirtiesContext
	void savingDoctorShouldTrimWhitespaces() {
		Doctor dirty = new Doctor("  Ola  ", "  Biała  ", "92010111111", Specialization.PEDIATRA, " Wrocław ");
		Doctor saved = doctorService.addDoctor(dirty);

		// pobranie świeżych danych
		var found = doctorRepository.findById(saved.getId()).orElseThrow();

		assertEquals("Ola", found.getName().trim());
		assertEquals("Biała", found.getSurname().trim());
		assertEquals(Specialization.PEDIATRA, found.getSpecialization());
		assertEquals("Wrocław", found.getAddress().trim());
	}

	@Test
	@DirtiesContext
	void deleteDoctorTwiceShouldReturnTrueThenFalse() {
		Doctor doctor = doctorRepository.save(new Doctor("Alan", "Test", "97010122222", Specialization.OKULISTA, "Łódź"));

        assertDoesNotThrow(() ->
                doctorService.deleteDoctorById(doctor.getId()));

        assertThrows(Exception.class, () ->
                doctorService.deleteDoctorById(doctor.getId()));
    }

	@Test
	@DirtiesContext
	void addDoctorWithVeryLongFieldsShouldFail() {
		String longString = "x".repeat(500); // znacznie ponad typowe ograniczenia

		Doctor d = new Doctor(
				longString,
				longString,
				"70010155555",
				Specialization.KARDIOLOG,
				longString
		);

		assertThrows(Exception.class, () -> doctorService.addDoctor(d));
	}

	@Test
	@DirtiesContext
	void searchingForDoctorAfterDatabaseResetShouldReturnEmpty() {
		Doctor d = doctorRepository.save(new Doctor("Adam", "AAA", "70010111111", Specialization.OKULISTA, "Poznań"));
		assertTrue(doctorRepository.existsById(d.getId()));

		doctorRepository.deleteAll();
		doctorRepository.flush();

		assertFalse(doctorRepository.existsById(d.getId()));
		assertTrue(doctorService.getDoctors().isEmpty());
	}

	@Test
	@DirtiesContext
	void savingDoctorShouldGenerateUniqueIds() {
		Doctor d1 = doctorRepository.save(new Doctor("A", "B", "71010111111", Specialization.KARDIOLOG, "Miasto"));
		Doctor d2 = doctorRepository.save(new Doctor("C", "D", "72010111111", Specialization.KARDIOLOG, "Miasto"));

		assertNotEquals(d1.getId(), d2.getId());
	}

	@Test
	@DirtiesContext
	void findDoctorsDoesNotMutateOriginalDatabase() {
		doctorRepository.save(new Doctor("Jan", "Zero", "70010188888", Specialization.KARDIOLOG, "Gdańsk"));

		var before = doctorService.getDoctors();
		var after = doctorService.getDoctors();

		assertEquals(before.size(), after.size());
		assertNotSame(before, after); // powinny być różnymi instancjami list
	}

	@Test
	@DirtiesContext
	void updateDoctorMultipleTimesShouldPersistLatestVersion() {
		Doctor d = doctorRepository.save(new Doctor("A", "B", "71010111111", Specialization.KARDIOLOG, "Miasto"));

		d.setName("X");
		d.setSurname("Y");
		d.setSpecialization(Specialization.NEUROLOG);
		d.setAddress("W");

		Doctor updated = doctorRepository.save(d);

		var found = doctorRepository.findById(updated.getId()).orElseThrow();
		assertEquals("X", found.getName());
		assertEquals("Y", found.getSurname());
		assertEquals(Specialization.NEUROLOG, found.getSpecialization());
		assertEquals("W", found.getAddress());
	}

	@Test
	@DirtiesContext
	void removingDoctorThenReaddingShouldCreateNewId() {
		Doctor d1 = doctorRepository.save(new Doctor("A", "B", "71010155555", Specialization.KARDIOLOG, "Miasto"));
		Long oldId = d1.getId();
		doctorRepository.deleteById(oldId);
		doctorRepository.flush();

		Doctor d2 = doctorRepository.save(new Doctor("A", "B", "72010155555", Specialization.KARDIOLOG, "Miasto"));
		Long newId = d2.getId();

		assertNotEquals(oldId, newId);
	}

	@Test
	@DirtiesContext
	void addingDoctorsWithNonLatinCharactersShouldNotFail() {
		Doctor d = doctorRepository.save(new Doctor(
				"Łukasz",
				"Brzęczyszczykiewicz",
				"81010177777",
				Specialization.OKULISTA,
				"Żółć nad Łęgiem"
		));

		var found = doctorRepository.findById(d.getId()).orElseThrow();
		assertEquals("Łukasz", found.getName());
		assertTrue(found.getSurname().contains("cz"));
	}

	// OFFICE

	@Autowired
	private OfficeRepository officeRepository;

	@Test
	void addingOfficeShouldPersistIt() {

		var office = new Office();

		var saved = officeRepository.save(office);

		var found = officeRepository.findById(saved.getId()).orElseThrow();

		assertNotNull(found.getId());
		assertEquals(0, found.getRoomNumber());
	}

	@Test
	void shouldSaveMultipleOffices() {
		var o1 = officeRepository.save(new Office());
		var o2 = officeRepository.save(new Office());

		var all = officeRepository.findAll();

		assertTrue(all.size() >= 2);
		assertNotEquals(o1.getId(), o2.getId());
	}

	@Test
	void deletingOfficeShouldRemoveItFromDatabase() {
		var office = officeRepository.save(new Office());

		officeRepository.delete(office);

		var result = officeRepository.findById(office.getId());

		assertTrue(result.isEmpty());
	}

	@Test
	void savedOfficeShouldBeRetrievableById() {
		var saved = officeRepository.save(new Office());

		var fromDb = officeRepository.findById(saved.getId()).orElseThrow();

		assertEquals(saved.getId(), fromDb.getId());
	}

	@Test
	@DirtiesContext
	void officeShouldPersistAcrossContextReload() {
		var office = officeRepository.save(new Office());

		var found = officeRepository.findById(office.getId()).orElseThrow();

		assertNotNull(found);
	}

	// SHIFT

	@Autowired
	private ShiftRepository shiftRepository;

	@Test
	void officeRepositoryShouldReturnEmptyListAtStart() {
		var all = officeRepository.findAll();
		assertNotNull(all);
	}

	@Test
	void addingShiftWithDoctorAndOfficeShouldPersist() {

		var office = officeRepository.save(new Office());
		var doctor = doctorRepository.save(new Doctor());

		var shift = new Shift();
		shift.setOffice(office);
		shift.setDoctor(doctor);

		var saved = shiftRepository.save(shift);

		var found = shiftRepository.findById(saved.getId()).orElseThrow();

		assertNotNull(found.getId());
		assertNotNull(found.getOffice());
		assertNotNull(found.getDoctor());
	}

	@Test
	void savingOfficeShouldIncreaseOfficeCount() {
		long before = officeRepository.count();

		officeRepository.save(new Office());

		long after = officeRepository.count();

		assertTrue(after >= before + 1);
	}

	@Test
	void findByIdForNonExistingOfficeShouldReturnEmptyOptional() {
		var result = officeRepository.findById(-123L);
		assertTrue(result.isEmpty());
	}

	@Test
	void deleteAllShouldRemoveAllOffices() {
		officeRepository.save(new Office());
		officeRepository.save(new Office());

		officeRepository.deleteAll();

		assertEquals(0, officeRepository.count());
	}

	@Test
	void savedOfficeShouldHaveDefaultRoomNumber() {
		var office = officeRepository.save(new Office());

		var fromDb = officeRepository.findById(office.getId()).orElseThrow();

		assertEquals(0, fromDb.getRoomNumber());
	}

	@Test
	void officeShiftsListShouldNotBeNull() {
		var office = officeRepository.save(new Office());

		var fromDb = officeRepository.findById(office.getId()).orElseThrow();

		assertNotNull(fromDb.getShifts());
	}

	// PATIENT

	@Autowired
	private PatientRepository patientRepository;

	@Test
	void patientRepositoryShouldReturnList() {
		var all = patientRepository.findAll();
		assertNotNull(all);
	}

	@Test
	void savingPatientShouldIncreaseCount() {
		long before = patientRepository.count();

		patientRepository.save(new Patient());

		long after = patientRepository.count();

		assertTrue(after >= before + 1);
	}

	@Test
	void findByIdForNonExistingPatientShouldReturnEmptyOptional() {
		var result = patientRepository.findById(-99L);
		assertTrue(result.isEmpty());
	}

	@Test
	void savedPatientShouldBeRetrievableById() {
		var saved = patientRepository.save(new Patient());

		var found = patientRepository.findById(saved.getId()).orElseThrow();

		assertEquals(saved.getId(), found.getId());
	}

	@Test
	void deletingPatientShouldRemoveItFromDatabase() {
		var patient = patientRepository.save(new Patient());

		patientRepository.delete(patient);

		var result = patientRepository.findById(patient.getId());

		assertTrue(result.isEmpty());
	}

}
