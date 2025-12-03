# DK-czw-0800-Pacjenci-Debuggera

## Zaimplementowane funkcjonalności m1

### Zarządzanie lekarzami

1. **Dodanie lekarza** - `POST /api/lekarze`
   - Pola: imię, nazwisko, PESEL, specjalizacja, adres

2. **Wyświetlenie listy lekarzy** - `GET /api/lekarze`
   - Zwraca: imię, nazwisko, specjalizacja

3. **Wyświetlenie szczegółów lekarza** - `GET /api/lekarze/{id}`
   - Zwraca: imię, nazwisko, specjalizacja, adres

4. **Usunięcie lekarza** - `DELETE /api/lekarze/{id}`

## Uruchomienie backendu

```bash
cd clinic
./gradlew bootRun
```

Backend: `http://localhost:8080`

## Uruchomienie frontendu

```bash
npm install -g npm
npm i
npm run dev
```

Frontend: `http://localhost:5173`