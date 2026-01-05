# KeikkaDiili – Sääassistentti

KeikkaDiili – Sääassistentti on Android-sovellus (Kotlin + Jetpack Compose), jolla voi tallentaa keikkoja paikallisesti ja tarkistaa keikkapäivälle sääennusteen. Sovellus laskee oman sääpisteytyksen (0–100) ja näyttää suosituksen ulkokeikoille.

## Ajaminen (Android Studio)

1. Avaa projekti Android Studiolla.
2. Odota että Gradle-synkronointi valmistuu.
3. Valitse laite (emulaattori tai puhelin) ja paina Run.

Ei API-avaimia.

## Arkkitehtuuri

- **Data (remote)**: Retrofit + OkHttp + Kotlin Serialization (Open-Meteo Geocoding + Forecast)
- **Data (local)**: Room (keikat)
- **Repositoryt**: yhdistää datalähteet ja mapittaa domain-malleihin
- **ViewModel (MVVM)**: StateFlow/UiState, taustalataus ja virheenkäsittely
- **UI (Compose)**: Navigation Compose, 3 näkymää (lista, detail, info)

Virhe- ja lataustilat:
- Listassa säätiedot haetaan taustalla per kaupungin ennuste; kortissa näkyy placeholder latauksen ajan.
- Verkko-/API-virhe näyttää virhenäkymän ja Retry luo ViewModelin uudelleen.
- Kaupunkia ei löydy tai ennuste ei kata päivää → näytetään informatiivinen teksti (ei Error).

## API ja attribuutio

Säädata: Open-Meteo
- Geocoding API: https://geocoding-api.open-meteo.com/
- Forecast API: https://api.open-meteo.com/

Sama attribuutio näkyy myös Info-näkymässä.

## TODO (siisteys)

- Dependency injection (esim. Hilt/Koin) ViewModelien ja Repositoryjen luontiin (nyt luodaan AppNavHostissa).
- GigDetailViewModel: optimoi keikan haku (nyt haetaan listasta id:llä).
- Lisää yksikkötestit (WeatherScoring, mapperit, repositoryt).
