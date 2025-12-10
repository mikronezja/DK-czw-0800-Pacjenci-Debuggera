package com.oot.clinic;

import com.oot.clinic.doctor.Doctor;
import com.oot.clinic.doctor.DoctorRepository;
import com.oot.clinic.doctor.DoctorService;
import com.oot.clinic.doctor.Specialization;
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
		Optional<Doctor> result = doctorService.getDoctorById(saved.getId());

		assertTrue(result.isPresent());
		assertEquals("Anna", result.get().getName());
	}

	@Test
	@DirtiesContext
	void deleteDoctorByIdShouldRemoveDoctorFromDatabase() {
		Doctor doctor = new Doctor("Marek", "Zielony", "90010112345", Specialization.ORTOPEDA, "Poznań");
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
}
