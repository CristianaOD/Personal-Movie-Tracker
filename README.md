# Personal Movie Tracker

Aplicatie Android realizata in Kotlin + Jetpack Compose. Directia aleasa este un `Personal Movie Tracker`, o aplicatie in care utilizatorul se autentifica, vede filme populare dintr-un API public, acceseaza detalii si isi salveaza filmele preferate intr-o lista locala de tip watchlist.

## Obiectiv

Scopul proiectului este crearea unei aplicatii coerente, cu flux complet:

- autentificare `Login/Register`
- navigatie intre ecrane
- request-uri HTTP catre un API public
- persistenta locala cu `Room`
- setari locale cu `SharedPreferences`
- interfata prietenoasa si stabila

## Functionalitati

Functionalitati propuse pentru versiunea finala:

- `Login` si `Register` pentru utilizator
- lista de filme `Trending`
- cautare filme dupa titlu
- ecran cu detalii pentru fiecare film
- `Watchlist` salvat local
- setari locale precum `Dark Mode` si numele utilizatorului

## Cerinte proiectului

- `GIT access`
  Repo-ul este versionat cu Git
- `Activity, Screens & Navigation Component`
  Aplicatia foloseste Activity + ecrane Compose si navigatie intre rute
- `Authentication (Login + Register)`
  Implementare cu `Firebase Authentication`
- `Store data into local database + show data into a scrollable list`
  Watchlist-ul va fi stocat local cu `Room` si afisat intr-un `LazyColumn`
- `Data stored in SharedPreferences`
  Setari precum numele utilizatorului si `Dark Mode`
- `HTTP requests (min. 2)`
  Request-uri catre API-ul TMDB pentru lista de filme si detalii
- `Create shape drawables`
  Butoane, fundaluri si selectii personalizate
- `User friendly app`
  UI clar, responsive si fara overlap
- `No crash`
  Validari, stari de eroare si testare manuala pe emulator

## Tool-uri

- `Kotlin`
- `Jetpack Compose`
- `Navigation Compose`
- `Firebase Authentication`
- `Retrofit`
- `Room`
- `SharedPreferences`
- `Material 3`
- `Coroutines`
- `Coil`

## Echipa

| Nume | GitHub |
|------|--------|
| Ojoc Diana-Cristiana | [@CristianaOD](https://github.com/CristianaOD) |
| Ruka Mirela | [@Mirela89](https://github.com/Mirela89) |
