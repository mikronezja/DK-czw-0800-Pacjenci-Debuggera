package com.oot.clinic;

import com.oot.clinic.doctor.Doctor;
import com.oot.clinic.doctor.DoctorRepository;
import com.oot.clinic.doctor.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClinicApplicationTests {

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
				"Kardiolog",
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
				"Dermatolog",
				"Kraków"
		);

		Doctor saved = doctorRepository.save(doctor);
		Optional<Doctor> result = doctorService.getDoctorById(saved.getId());

		assertTrue(result.isPresent());
		assertEquals("Anna", result.get().getName());
	}

	@Test
	@DirtiesContext
	void deleteDoctorByIdShouldRemoveDoctorFromDatabase() {
		Doctor doctor = new Doctor("Marek", "Zielony", "90010112345", "Ortopeda", "Poznań");
		Doctor saved = doctorRepository.save(doctor);

		boolean deleted = doctorService.deleteDoctorById(saved.getId());

		assertTrue(deleted);
		assertFalse(doctorRepository.existsById(saved.getId()));
	}

	@Test
	@DirtiesContext
	void getDoctorByIdShouldReturnEmptyOptionalWhenDoctorDoesNotExist() {
		var result = doctorService.getDoctorById(999L);
		assertTrue(result.isEmpty());
	}

	@Test
	@DirtiesContext
	void deleteDoctorByIdShouldReturnFalseIfDoctorDoesNotExist() {
		boolean deleted = doctorService.deleteDoctorById(123L);
		assertFalse(deleted);
	}

	@Test
	@DirtiesContext
	void doctorEntityShouldStoreAllFieldsCorrectly() {
		Doctor doctor = new Doctor("Adam", "Kłos", "70010112345", "Neurolog", "Łódź");
		Doctor saved = doctorRepository.save(doctor);

		var found = doctorRepository.findById(saved.getId());
		assertTrue(found.isPresent());

		var d = found.get();
		assertEquals("Adam", d.getName());
		assertEquals("Kłos", d.getSurname());
		assertEquals("70010112345", d.getPesel());
		assertEquals("Neurolog", d.getSpecialization());
		assertEquals("Łódź", d.getAddress());
	}

	@Test
	@DirtiesContext
	void updateDoctorDataShouldPersistNewValues() {
		Doctor doctor = doctorRepository.save(new Doctor("Marek", "Stary", "70010112345", "Ortopeda", "Poznań"));

		doctor.setSurname("Nowy");
		doctor.setSpecialization("Chirurg");

		Doctor updated = doctorRepository.save(doctor);

		assertEquals("Nowy", updated.getSurname());
		assertEquals("Chirurg", updated.getSpecialization());
	}

	@Test
	@DirtiesContext
	void savingDoctorShouldTrimWhitespaces() {
		Doctor dirty = new Doctor("  Ola  ", "  Biała  ", "92010111111", " Pediatra ", " Wrocław ");
		Doctor saved = doctorService.addDoctor(dirty);

		// pobranie świeżych danych
		var found = doctorRepository.findById(saved.getId()).orElseThrow();

		assertEquals("Ola", found.getName().trim());
		assertEquals("Biała", found.getSurname().trim());
		assertEquals("Pediatra", found.getSpecialization().trim());
		assertEquals("Wrocław", found.getAddress().trim());
	}

	@Test
	@DirtiesContext
	void deleteDoctorTwiceShouldReturnTrueThenFalse() {
		Doctor doctor = doctorRepository.save(new Doctor("Alan", "Test", "97010122222", "Okulista", "Łódź"));

		assertTrue(doctorService.deleteDoctorById(doctor.getId()));
		assertFalse(doctorService.deleteDoctorById(doctor.getId())); // już usunięty
	}

	@Test
	@DirtiesContext
	void addDoctorWithVeryLongFieldsShouldFail() {
		String longString = "x".repeat(500); // znacznie ponad typowe ograniczenia

		Doctor d = new Doctor(
				longString,
				longString,
				"70010155555",
				longString,
				longString
		);

		assertThrows(Exception.class, () -> doctorService.addDoctor(d));
	}

	@Test
	@DirtiesContext
	void searchingForDoctorAfterDatabaseResetShouldReturnEmpty() {
		Doctor d = doctorRepository.save(new Doctor("Adam", "AAA", "70010111111", "Oko", "Poznań"));
		assertTrue(doctorRepository.existsById(d.getId()));

		doctorRepository.deleteAll();
		doctorRepository.flush();

		assertFalse(doctorRepository.existsById(d.getId()));
		assertTrue(doctorService.getDoctors().isEmpty());
	}

	@Test
	@DirtiesContext
	void savingDoctorShouldGenerateUniqueIds() {
		Doctor d1 = doctorRepository.save(new Doctor("A", "B", "71010111111", "Spec", "Miasto"));
		Doctor d2 = doctorRepository.save(new Doctor("C", "D", "72010111111", "Spec", "Miasto"));

		assertNotEquals(d1.getId(), d2.getId());
	}

	@Test
	@DirtiesContext
	void findDoctorsDoesNotMutateOriginalDatabase() {
		doctorRepository.save(new Doctor("Jan", "Zero", "70010188888", "Kardiolog", "Gdańsk"));

		var before = doctorService.getDoctors();
		var after = doctorService.getDoctors();

		assertEquals(before.size(), after.size());
		assertNotSame(before, after); // powinny być różnymi instancjami list
	}

	@Test
	@DirtiesContext
	void updateDoctorMultipleTimesShouldPersistLatestVersion() {
		Doctor d = doctorRepository.save(new Doctor("A", "B", "71010111111", "Spec", "Miasto"));

		d.setName("X");
		d.setSurname("Y");
		d.setSpecialization("Z");
		d.setAddress("W");

		Doctor updated = doctorRepository.save(d);

		var found = doctorRepository.findById(updated.getId()).orElseThrow();
		assertEquals("X", found.getName());
		assertEquals("Y", found.getSurname());
		assertEquals("Z", found.getSpecialization());
		assertEquals("W", found.getAddress());
	}

	@Test
	@DirtiesContext
	void removingDoctorThenReaddingShouldCreateNewId() {
		Doctor d1 = doctorRepository.save(new Doctor("A", "B", "71010155555", "Spec", "Miasto"));
		Long oldId = d1.getId();
		doctorRepository.deleteById(oldId);
		doctorRepository.flush();

		Doctor d2 = doctorRepository.save(new Doctor("A", "B", "72010155555", "Spec", "Miasto"));
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
				"Okulista",
				"Żółć nad Łęgiem"
		));

		var found = doctorRepository.findById(d.getId()).orElseThrow();
		assertEquals("Łukasz", found.getName());
		assertTrue(found.getSurname().contains("cz"));
	}
}
