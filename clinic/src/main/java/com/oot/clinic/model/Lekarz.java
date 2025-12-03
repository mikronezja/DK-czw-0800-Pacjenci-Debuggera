package com.oot.clinic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lekarze")
public class Lekarz {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String imie;
    
    @Column(nullable = false)
    private String nazwisko;
    
    @Column(nullable = false, unique = true)
    private String pesel;
    
    @Column(nullable = false)
    private String specjalizacja;
    
    @Column(nullable = false)
    private String adres;
    
    public Lekarz() {
    }
    
    public Lekarz(String imie, String nazwisko, String pesel, String specjalizacja, String adres) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.pesel = pesel;
        this.specjalizacja = specjalizacja;
        this.adres = adres;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getImie() {
        return imie;
    }
    
    public void setImie(String imie) {
        this.imie = imie;
    }
    
    public String getNazwisko() {
        return nazwisko;
    }
    
    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }
    
    public String getPesel() {
        return pesel;
    }
    
    public void setPesel(String pesel) {
        this.pesel = pesel;
    }
    
    public String getSpecjalizacja() {
        return specjalizacja;
    }
    
    public void setSpecjalizacja(String specjalizacja) {
        this.specjalizacja = specjalizacja;
    }
    
    public String getAdres() {
        return adres;
    }
    
    public void setAdres(String adres) {
        this.adres = adres;
    }
}

