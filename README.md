# ✅ Status / Tjekliste – Streamingplatform

## Milepæle

### Milepæl A – Database klar
- [x] Tabeller oprettet (users, movies, favorites)
- [x] Testdata indsat (DbInit seeder users/movies/favorites)
- [x] SELECT kan køres og giver rækker tilbage

### Milepæl B – JDBC forbindelse virker
- [x] JDBC connection virker (DatabaseConfig + repositories kører queries)
- [x] SELECT COUNT(*) på movies fungerer

### Milepæl C – Repository virker
- [x] MovieRepository.findAll()/getMovies() returnerer en liste
- [x] Kan printe mindst 10 film i console

### Milepæl D – UI kan vise film
- [x] UI viser film i ListView
- [x] Refresh/sort funktion virker (sortér efter rating i Java)

### Milepæl E – Favorites virker
- [x] Find favoritter via email (live søgning på users via email)
- [x] Tilføj favorit (vælg film + aktiv user)
- [x] Fjern favorit
- [x] UI opdaterer korrekt efter ændringer


## Fejlscenarier (skal kunne vises i program)
- [x] Ugyldig email input → brugervenlig besked
- [x] Email findes ikke → “ingen bruger fundet” (placeholder i user-listen)
- [x] Bruger har ingen favoritter → tom liste + status/placeholder
- [x] Database nede / forkert login → venlig besked i UI + detaljer i console


## Krav til JDBC og SQL
- [x] PreparedStatement i repositories
- [x] Ingen SQL bygget via string concat med user input
- [x] Repository har mapping/hjælpemetoder til ResultSet → objekter


## Ekstraopgaver
### Ekstra 1 – Filter på genre
- [ ] Combobox til genre i UI
- [x] Filtrering i service eller SQL (Det sker i service her)

### Ekstra 2 – Top 5
- [x] Knap der viser top 5 film efter rating (popup)

### Ekstra 3 – Validering
- [ ] Rating skal være mellem 1 og 10
- [x] Email skal indeholde @

### Ekstra 4 – Refaktorering til bedre SRP
- [ ] Split et stort repository op i flere mindre
- [x] Service refaktorering: StreamingService splittet op i 3 services (MovieService, FavoriteService, UserService)
  - Formål: bedre læsbarhed og mindre risiko for at blande modeller/repos sammen

## Refleksionsspørgsmål

**Hvorfor må der ikke ligge SQL i controlleren?**  
Controlleren skal kun håndtere UI-logik. SQL hører til i repository-laget for at overholde SRP og gøre koden lettere at vedligeholde.

**Hvor ligger validering, og hvorfor?**  
Validering ligger i service-laget, fordi det er "system" logik og ikke UI-logik.

**Hvad er fordelen ved repository-interfaces?**  
De gør koden mere fleksibel og testbar og adskiller datatilgang fra "system" logik. (jeg bemærker først "interface" delen nu - var det meningen at vi skulle lave et repository-interface som de alle skulle implement?)

**Nævn to eksempler på SRP fra projektet**  
- Repositories håndterer kun dataadgang
- Services håndterer kun regler og koordinering

**Hvad er fordelen ved PreparedStatement?**  
- Beskytter mod SQL-injection  
- Gør SQL mere robust og læsbar
