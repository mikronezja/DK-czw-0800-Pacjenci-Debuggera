package com.oot.clinic.config;

import com.oot.clinic.doctor.Doctor;
import com.oot.clinic.doctor.DoctorRepository;
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

            repo.save(new Doctor("Jan", "Kowalski", "80010112345", "Kardiolog", "Poznań, ul. Słoneczna 10"));
            repo.save(new Doctor("Anna", "Nowak", "82030554321", "Kardiolog", "Poznań, ul. Lipowa 5"));
            repo.save(new Doctor("Piotr", "Wiśniewski", "75071298765", "Kardiolog", "Poznań, ul. Norwida 2"));

            repo.save(new Doctor("Maria", "Lewandowska", "90022311223", "Dermatolog", "Warszawa, ul. Długa 15"));
            repo.save(new Doctor("Jakub", "Król", "85091833211", "Dermatolog", "Warszawa, ul. Krótka 3"));

            repo.save(new Doctor("Ewa", "Zielińska", "78043099800", "Neurolog", "Kraków, ul. Wawelska 8"));
            repo.save(new Doctor("Tomasz", "Sikora", "83061277654", "Okulista", "Gdańsk, ul. Morska 20"));


            System.out.println("Sample doctors loaded into H2 database.");
        };
    }
}
