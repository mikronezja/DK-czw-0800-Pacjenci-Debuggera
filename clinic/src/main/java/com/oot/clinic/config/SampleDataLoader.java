package com.oot.clinic.config;

import com.oot.clinic.entities.Doctor;
import com.oot.clinic.repositories.DoctorRepository;
import com.oot.clinic.entities.enumeration.Specialization;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleDataLoader {

    @Bean
    @ConditionalOnProperty(name = "app.load-sample-data", havingValue = "true")
    CommandLineRunner loadSampleData(DoctorRepository repo) {
        return args -> {

            if (repo.count() > 0) {
                System.out.println("Sample data NOT loaded – DB already contains doctors.");
                return;
            }

            repo.save(new Doctor("Jan", "Kowalski", "80010112345", Specialization.KARDIOLOG, "Poznań, ul. Słoneczna 10"));
            repo.save(new Doctor("Anna", "Nowak", "82030554321", Specialization.KARDIOLOG, "Poznań, ul. Lipowa 5"));
            repo.save(new Doctor("Piotr", "Wiśniewski", "75071298765", Specialization.KARDIOLOG, "Poznań, ul. Norwida 2"));

            repo.save(new Doctor("Maria", "Lewandowska", "90022311223", Specialization.DERMATOLOG, "Warszawa, ul. Długa 15"));
            repo.save(new Doctor("Jakub", "Król", "85091833211", Specialization.DERMATOLOG, "Warszawa, ul. Krótka 3"));

            repo.save(new Doctor("Ewa", "Zielińska", "78043099800", Specialization.NEUROLOG, "Kraków, ul. Wawelska 8"));
            repo.save(new Doctor("Tomasz", "Sikora", "83061277654", Specialization.OKULISTA, "Gdańsk, ul. Morska 20"));


            System.out.println("Sample doctors loaded into H2 database.");
        };
    }
}
