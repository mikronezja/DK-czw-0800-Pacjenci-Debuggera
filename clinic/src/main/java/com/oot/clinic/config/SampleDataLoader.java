package com.oot.clinic.config;

import com.oot.clinic.entities.Doctor;
import com.oot.clinic.entities.Office;
import com.oot.clinic.entities.Patient;
import com.oot.clinic.entities.Shift;
import com.oot.clinic.entities.enumeration.Specialization;
import com.oot.clinic.repositories.DoctorRepository;
import com.oot.clinic.repositories.OfficeRepository;
import com.oot.clinic.repositories.PatientRepository;
import com.oot.clinic.repositories.ShiftRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final OfficeRepository officeRepository;
    private final ShiftRepository shiftRepository;
    private final DoctorRepository doctorRepository;

    public SampleDataLoader(
            PatientRepository patientRepository,
            OfficeRepository officeRepository,
            ShiftRepository shiftRepository,
            DoctorRepository doctorRepository
    ) {
        this.patientRepository = patientRepository;
        this.officeRepository = officeRepository;
        this.shiftRepository = shiftRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public void run(String... args) {

        // zapobiega duplikowaniu danych przy każdym starcie
        if (patientRepository.count() > 0) {
            return;
        }

        // ---------- PACJENCI ----------
        Patient p1 = new Patient();
        p1.setName("Jan");
        p1.setSurname("Kowalski");
        p1.setPesel("80010112345");
        p1.setAddress("Warszawa, ul. Kwiatowa 12");

        Patient p2 = new Patient();
        p2.setName("Anna");
        p2.setSurname("Nowak");
        p2.setPesel("90070755555");
        p2.setAddress("Kraków, ul. Długa 4");

        Patient p3 = new Patient();
        p3.setName("Piotr");
        p3.setSurname("Zieliński");
        p3.setPesel("75010199999");
        p3.setAddress("Gdańsk, ul. Morska 21");

        Patient p4 = new Patient();
        p4.setName("Karolina");
        p4.setSurname("Wiśniewska");
        p4.setPesel("86031588888");
        p4.setAddress("Poznań, ul. Zielona 8");

        patientRepository.saveAll(List.of(p1, p2, p3, p4));

        // ------------------ GABINETY ------------------
        Office o1 = new Office();
        Office o2 = new Office();
        Office o3 = new Office();

        o1.setRoomNumber(101);
        o2.setRoomNumber(102);
        o3.setRoomNumber(103);

        officeRepository.saveAll(List.of(o1, o2, o3));

        // ---------- LEKARZE ----------
        Doctor d1 = new Doctor(
                "Marek",
                "Lekarski",
                "72010112345",
                Specialization.KARDIOLOG,
                "Warszawa, ul. Szpitalna 10"
        );

        Doctor d2 = new Doctor(
                "Katarzyna",
                "Medyczna",
                "83021298765",
                Specialization.DERMATOLOG,
                "Kraków, ul. Zdrowa 5"
        );

        Doctor d3 = new Doctor(
                "Andrzej",
                "Nowicki",
                "90050544444",
                Specialization.NEUROLOG,
                "Gdańsk, ul. Kliniczna 3"
        );

        Doctor d4 = new Doctor(
                "Anna",
                "Lis",
                "95010166666",
                Specialization.PEDIATRA,
                "Poznań, ul. Maluchów 12"
        );

        doctorRepository.saveAll(List.of(d1, d2, d3, d4));

        // ------------------ DYŻURY ------------------
        Shift s1 = new Shift();
        s1.setDoctor(d1);
        s1.setOffice(o1);
        s1.setDayOfWeek(DayOfWeek.MONDAY);
        s1.setStartTime(LocalTime.of(8, 0));
        s1.setEndTime(LocalTime.of(14, 0));

        Shift s2 = new Shift();
        s2.setDoctor(d1);
        s2.setOffice(o2);
        s2.setDayOfWeek(DayOfWeek.TUESDAY);
        s2.setStartTime(LocalTime.of(10, 0));
        s2.setEndTime(LocalTime.of(18, 0));

        Shift s3 = new Shift();
        s3.setDoctor(d2);
        s3.setOffice(o3);
        s3.setDayOfWeek(DayOfWeek.WEDNESDAY);
        s3.setStartTime(LocalTime.of(7, 30));
        s3.setEndTime(LocalTime.of(15, 30));

        shiftRepository.saveAll(List.of(s1, s2, s3));
    }
}
