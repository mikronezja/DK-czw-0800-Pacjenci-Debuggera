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

            repo.save(new Doctor(null, "Jan", "Kowalski", "Kardiolog", "Poznań"));
            repo.save(new Doctor(null, "Anna", "Nowak", "Kardiolog", "Poznań"));
            repo.save(new Doctor(null, "Piotr", "Wiśniewski", "Kardiolog", "Poznań"));

            repo.save(new Doctor(null, "Maria", "Lewandowska", "Dermatolog", "Warszawa"));
            repo.save(new Doctor(null, "Jakub", "Król", "Dermatolog", "Warszawa"));

            repo.save(new Doctor(null, "Ewa", "Zielińska", "Neurolog", "Kraków"));
            repo.save(new Doctor(null, "Tomasz", "Sikora", "Okulista", "Gdańsk"));

            System.out.println("Sample doctors loaded into H2 database.");
        };
    }
}
