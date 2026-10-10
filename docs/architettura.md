# Architettura

## Visione d'insieme

Partiturae è composta da un'API REST (Spring Boot) e da un'applicazione web (Angular) che la usa. I dati stanno in MySQL.

```
Browser (Angular, :4200)  ──HTTP/JSON──>  API Spring Boot (:9000)  ──JPA──>  MySQL (:3307)
```

## Backend

Il codice è organizzato **per funzionalità**, non per tipo di classe: ogni funzionalità ha un package con tutto quello che le serve (entità, repository, servizio, controller, DTO).

| Package | Contenuto |
|---|---|
| `socio` | anagrafica soci, iscrizioni annuali |
| `musicista` | profilo musicale di un socio (strumenti suonati) |
| `direttivo` | cariche del direttivo e relativi mandati |
| `formazione` | dati e logo della formazione (riga unica) |
| `famiglia`, `strumento`, `strumentofiglio` | catalogo strumenti: famiglia → strumento → voce |
| `autore`, `partitura` | autori e partiture |
| `parte` | parti: PDF di una partitura collegato a uno o più strumenti |
| `raccolta` | raccolte ordinate di partiture |
| `evento` | calendario e presenze |
| `banda` | le bande e la separazione dei dati: ogni dato di lavoro appartiene a una banda |
| `auth` | login, account, appartenenze (ruoli per banda), sessioni, permessi |
| `common` | eccezioni, gestione errori e DTO condivisi |
| `seed` | strumenti inseriti al primo avvio |

Regole seguite:

- Un package usa gli altri tramite le loro classi pubbliche (di solito i servizi); i metodi condivisi come `findEntityById` sono pubblici.
- Gli errori diventano risposte JSON con `GlobalExceptionHandler` (404, 400, 409 per elementi ancora in uso, 401/429 per l'autenticazione).
- `spring.jpa.hibernate.ddl-auto=update` crea e aggiorna le tabelle da solo; per un database nuovo basta avviare il backend.

### Scelte di progetto

- **Iscrizione**: `iscrizione_socio` è l'unica fonte di verità. `iscritto = true` per un anno significa sia iscritto al libro soci sia tesserato.
- **Musicista**: estensione facoltativa di un socio. Un socio può stare nel libro soci senza suonare.
- **Formazione**: una sola riga (le impostazioni della formazione).
- **Direttivo**: alcune cariche (presidente, vicepresidente, segretario, tesoriere, maestro concertatore) sono uniche nello stesso periodo; il controllo è nel servizio.
- **Parti**: una parte è un PDF di una partitura legato a uno o più strumenti (tabella `parte_strumento`). Il PDF sta in una tabella a parte (`parte_documento`) e viene letto solo quando serve, così elencare le parti resta veloce. Il nome del file scaricato si calcola da strumenti e titolo (`Strumento_NomePartitura.pdf`, senza spazi) e quindi resta coerente se qualcosa viene rinominato.
- **Più bande**: ogni tabella di lavoro ha la colonna `banda_id` (campo `@TenantId` di Hibernate): Hibernate filtra da solo tutte le ricerche per la banda del token e la scrive in ogni inserimento. Account e bande non hanno `banda_id`. I permessi per ruolo sono in `auth/PermessiApi.java` (vedi [autenticazione](autenticazione.md)).
- **Eliminazioni sicure**: autori, famiglie, strumenti e voci hanno un endpoint `/{id}/utilizzo` che elenca dove sono usati; l'interfaccia lo mostra prima di eliminare.
- **Catalogo di partenza**: gli strumenti iniziali sono in `seed/InstrumentCatalog.java` (solo dati). Ogni banda ha il suo catalogo: viene inserito per le bande che non hanno ancora nessuna famiglia.

## Modello dati

| Tabella | Contenuto |
|---|---|
| `socio` | codice fiscale, nome, cognome, mail, telefono, "aggiunto" |
| `iscrizione_socio` | socio + anno (univoco) e se è iscritto |
| `musicista`, `musicista_strumento` | profilo musicale e strumenti suonati |
| `membro_direttivo` | carica, anno di inizio e fine (fine vuota = in carica) |
| `formazione` | dati e logo della formazione |
| `famiglia`, `strumento`, `strumento_figlio` | catalogo strumenti |
| `autore`, `partitura` | repertorio |
| `parte`, `parte_strumento`, `parte_documento` | parti, strumenti collegati, PDF |
| `raccolta`, `raccolta_partitura` | raccolte e ordine delle partiture |
| `evento`, `presenza` | calendario e presenze (univoche per evento e socio) |
| `banda` | le bande (nome, attiva o bloccata) |
| `account`, `appartenenza`, `appartenenza_ruolo`, `refresh_token` | persone che accedono, banda e ruoli in quella banda, sessioni |

## API

Tutte le risorse stanno sotto `/api`. Le operazioni standard sono `GET` (elenco e dettaglio), `POST`, `PUT`, `DELETE`.

| Risorsa | Note |
|---|---|
| `/api/soci` | in più: `/{id}/iscrizioni` (per anno) e `/{id}/iscrizioni/summary` |
| `/api/musicisti` | `PUT /{id}/strumenti` imposta gli strumenti suonati |
| `/api/direttivo` | `?inCarica=true` filtra i mandati in corso |
| `/api/formazione` | riga unica; `/logo` per caricare e leggere il logo |
| `/api/famiglie`, `/api/strumenti`, `/api/strumenti-figli` | filtri `?famigliaId=` e `?strumentoId=`; `/{id}/utilizzo` |
| `/api/autori`, `/api/partiture` | `/{id}/utilizzo` per gli autori |
| `/api/parti` | filtri `?partituraId=`, `?strumentoFiglioId=`, `?strumentoId=`; `PUT /{id}/strumenti`; `POST` e `GET /{id}/pdf` |
| `/api/raccolte` | `POST /{id}/partiture`, `DELETE /{id}/partiture/{partituraId}`, `PUT /{id}/partiture/ordine` |
| `/api/eventi` | `/{eventoId}/presenze` e `PUT /{eventoId}/presenze/{socioId}` |
| `/api/auth` | `login`, `refresh`, `logout`, `me`, `banda` per cambiare banda (vedi [autenticazione](autenticazione.md)) |

## Frontend

- Componenti standalone, rilevamento delle modifiche senza zone (`provideZonelessChangeDetection`), stato con i signals.
- `src/app/features/<funzionalità>/` contiene modelli, servizio HTTP e pagine di ogni funzionalità.
- `src/styles.scss` contiene i colori e i componenti grafici condivisi (bottoni, tabelle, chip, form, avvisi): le pagine usano quelle classi invece di ridefinirle.
- `features/auth/` gestisce login, sessione, banda corrente, intercettore HTTP (aggiunge il token, rinnova la sessione se scade e avvisa se manca un permesso), protezione delle pagine per ruolo e la tabella dei permessi usata per nascondere le voci non permesse.
- I PDF e il logo si scaricano con richieste autenticate (un normale link non può mandare il token): ogni link verso l'API viene intercettato e gestito da `shared/file.service.ts`.
- L'indirizzo dell'API sta in `src/environments/`.