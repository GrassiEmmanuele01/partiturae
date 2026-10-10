# Partiturae

Gestionale per l'archivio di una formazione musicale (banda, orchestra): libro soci e musicisti, catalogo delle partiture con le parti in PDF per ogni strumento, raccolte, calendario con presenze, direttivo e dati della formazione. Pensato per più bande sulla stessa installazione, ognuna con i suoi dati e i suoi ruoli.

## Stato del progetto

*Ultimo aggiornamento: 10 ottobre 2026, branch `feature/login`.* Questa sezione si aggiorna a ogni passo di sviluppo.

Legenda: ✅ fatto · 🚧 in lavorazione · ⏳ da fare

### In breve

- **Fatto**: archivio completo (soci, musicisti, direttivo, strumenti, partiture con parti in PDF, raccolte, calendario con presenze), login sicuro, più bande con dati separati, ruoli per banda con permessi applicati sul server e nell'interfaccia.
- **In lavorazione**: sezioni banda e giovanile (allievi, due calendari, maestro degli allievi).
- **Da fare, nell'ordine previsto**: raccolte (svuota, elimina, personali) → cestino delle partiture → gruppi di strumenti → gestione di account e bande dall'interfaccia → aree per musicista, allievo e socio.

### Funzionalità per area

**Accesso e sicurezza**

| Funzionalità | Stato |
|---|---|
| Login con email e password, sessione con rinnovo automatico, blocco dopo troppi tentativi | ✅ |
| Più bande: ogni dato appartiene a una banda e le altre non lo vedono | ✅ |
| Ruoli per banda (admin, archivista, maestro, maestro allievi, direttivo, musicista, allievo, socio) e superadmin | ✅ |
| Permessi per ruolo applicati dal server (API) e nell'interfaccia (menu, pagine, pulsanti) | ✅ |
| Creazione degli account e reset delle password da terminale (script) | ✅ |
| Cambio password dall'interfaccia, password provvisoria da cambiare al primo accesso | ⏳ |
| Gestione degli account e dei ruoli dall'interfaccia (admin della banda) | ⏳ |
| Gestione delle bande, blocco di un utente o di una banda (superadmin) | ⏳ |

**Libro soci, musicisti, direttivo e formazione**

| Funzionalità | Stato |
|---|---|
| Anagrafica dei soci, iscrizione annuale (libro soci e federazione) | ✅ |
| Profilo musicale: gli strumenti che suona ogni musicista | ✅ |
| Direttivo: cariche con periodo di mandato (le cariche uniche non si sovrappongono) | ✅ |
| Dati e logo della formazione, riepilogo degli associati | ✅ |
| Allievi e sezione giovanile | 🚧 (sezioni) |
| Pagina "informazioni sulla banda" per i soci semplici | ⏳ |

**Strumenti**

| Funzionalità | Stato |
|---|---|
| Famiglie, strumenti e voci (es. Tromba → Tromba 1, Tromba 2) | ✅ |
| Catalogo di partenza per ogni banda (5 famiglie, 80 strumenti, 233 voci) | ✅ |
| Prima di eliminare qualcosa si vede dove è ancora usato | ✅ |
| Gruppi di strumenti (Clarinetti, Sax, Flauti...) oltre alle famiglie | ⏳ |

**Partiture e parti**

| Funzionalità | Stato |
|---|---|
| Autori, tipologie (marcia da libretto o da concerto, inno, valzer, polka, mazurka, altro), partiture | ✅ |
| Parti in PDF, anche un solo PDF per più strumenti (es. "Corno in Fa 1-2") | ✅ |
| Nome standard dei file scaricati, come `Ottavino1_InnoDiMameli.pdf` | ✅ |
| Partiture distinte tra banda e sezione giovanile | 🚧 (sezioni) |
| Cestino: le partiture eliminate si possono ripristinare, solo archivista e admin le eliminano per sempre | ⏳ |
| Musicista e allievo vedono e scaricano le parti dei propri strumenti | ⏳ |
| PDF unico di una raccolta per uno strumento | ⏳ |

**Raccolte**

| Funzionalità | Stato |
|---|---|
| Elenchi ordinati di partiture: creare, aggiungere, togliere, riordinare | ✅ |
| "Svuota tutto" dentro una raccolta | ⏳ |
| Eliminare una raccolta lasciando le partiture in archivio | ⏳ (oggi funziona solo se la raccolta è vuota) |
| Raccolte personali del musicista | ⏳ |

**Calendario**

| Funzionalità | Stato |
|---|---|
| Prove, concerti, assemblee e altri eventi, con le presenze dei soci | ✅ |
| Due calendari: banda e allievi | 🚧 (sezioni) |

**Interfaccia e qualità**

| Funzionalità | Stato |
|---|---|
| Stile unico (colori e caratteri), avvisi per gli errori e i permessi mancanti | ✅ |
| Menu, pagine e pulsanti che si adattano ai ruoli e alla banda scelta | ✅ |
| Home con riepilogo (oggi è solo un messaggio di benvenuto) | ⏳ |
| Stile uniforme anche nei moduli di partiture, soci, direttivo, musicisti e nel dettaglio raccolta | ⏳ |
| Test automatici del frontend (login, permessi, pulsanti, download dei file) | ✅ |
| Test automatici delle API del backend (oggi c'è solo il controllo dell'avvio) | ⏳ |

### Limiti noti

- Eliminare una raccolta con partiture dentro, o una partitura che ha delle parti o sta in una raccolta, dà l'avviso "ancora utilizzato altrove". Si risolve con i passi sulle raccolte e sul cestino.
- Musicisti e allievi per ora non vedono le parti: serve un filtro "solo i miei strumenti", previsto dopo le sezioni.
- Finché non ci sono le sezioni, il maestro degli allievi ha gli stessi accessi del maestro e l'allievo quelli del musicista.
- Non c'è ancora una schermata per creare altre bande o altri account: si fa da terminale (vedi "Comandi utili").
- Aprire il sito in due schede nello stesso istante può far chiudere la sessione (dettagli in [docs/autenticazione.md](docs/autenticazione.md)).

## Stack

- **Backend**: Spring Boot 4.1.1 (Java 17), Spring Data JPA, Spring Security (JWT)
- **Frontend**: Angular 21 (componenti standalone, zoneless, signals), test con Vitest
- **Database**: MySQL 8.0 via Docker Compose

## Prerequisiti

- JDK 17+
- Node.js 22.12 o più recente (Angular 21 accetta anche la 20.19; lo script di controllo dei permessi richiede la 22.6) e npm
- Docker Desktop

## Avvio del progetto

Servono **tre terminali** separati.

### 1. Database

Dalla radice del progetto:
```bash
docker compose up -d --wait
```
Il flag `--wait` aspetta che MySQL sia pronto prima di restituire il controllo, così il backend non fallisce per una connessione troppo anticipata.

Per fermarlo:
```bash
docker compose down
```
Aggiungi `-v` se vuoi cancellare anche i dati (`docker compose down -v`).

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```
Parte su **http://localhost:9000**. Al primo avvio crea la prima banda, gli strumenti di base e il primo account (vedi "Primo accesso").

> Nota: le porte 8080/8083 possono risultare occupate da intervalli riservati da Windows/Hyper-V. Controlla con `netsh interface ipv4 show excludedportrange protocol=tcp` e, se serve, cambia `server.port` in `backend/src/main/resources/application.properties` aggiornando poi `apiUrl` nei file di `frontend/src/environments/`.

### 3. Frontend

Al primo avvio installa le dipendenze (su Windows può servire `--legacy-peer-deps` per un bug noto di npm):
```bash
cd frontend
npm install --legacy-peer-deps
```
Poi, ad ogni avvio:
```bash
ng serve
```
Parte su **http://localhost:4200**.

## Primo accesso

Al primo avvio il backend crea la prima banda ("La mia banda") e, se non esiste nessun account, il primo account: `admin@partiturae.local`, superadmin e Admin di quella banda. La password viene scritta **una sola volta** nel log, dentro un riquadro "Creato il primo account". Copiala da lì.

Se preferisci scegliere tu la password, impostala prima di avviare il backend (funziona solo a tabella `account` vuota):
```bash
export PARTITURAE_ADMIN_PASSWORD='la-tua-password'
./mvnw spring-boot:run
```
(in PowerShell: `$env:PARTITURAE_ADMIN_PASSWORD='la-tua-password'`)

Password dimenticata o account bloccato? Vedi la sezione "Gestire le password da terminale" in [docs/autenticazione.md](docs/autenticazione.md).

## Test e controlli

### Test automatici

**Frontend** (non serve il backend):
```bash
cd frontend
ng test --watch=false
```
Coprono: login e rinnovo della sessione, cambio banda, tabella dei permessi per ogni ruolo, protezione delle pagine, la direttiva che nasconde i pulsanti, l'elenco partiture visto da archivista, maestro e musicista, e il download dei file con il token.

**Backend**: oggi c'è un solo test, che avvia l'intera applicazione (controlla che la configurazione, compresa la separazione dei dati per banda, sia valida).
```bash
docker compose up -d --wait      # serve il database acceso
cd backend
./mvnw test
```
Il test usa il database di sviluppo: se è vuoto crea la prima banda e il primo account e scrive la password nell'output, come all'avvio normale. Se compare `Communications link failure`, il database non è acceso.

**Permessi**: controlla che le regole del backend (`PermessiApi.java`), gli endpoint che esistono davvero e la tabella dell'interfaccia (`permessi.ts`) coincidano:
```bash
node scripts/verifica-permessi.mjs
```

### Prima di ogni commit

```bash
node scripts/verifica-permessi.mjs
cd backend && ./mvnw clean compile
cd ../frontend && ng test --watch=false
ng build
```

### Prova a mano dei ruoli

Crea un account per ogni ruolo da provare (il database deve essere acceso e il backend avviato almeno una volta):
```bash
bash scripts/create-account.sh maestro@prova.it MAESTRO
bash scripts/create-account.sh direttivo@prova.it DIRETTIVO
bash scripts/create-account.sh musicista@prova.it MUSICISTA
bash scripts/create-account.sh socio@prova.it SOCIO
```
Entra con ciascuno e controlla:

| Ruolo | Cosa deve vedere e fare |
|---|---|
| Admin | tutto |
| Archivista | archivio completo (anche eliminare), soci, musicisti e calendari; i dati della banda solo in lettura |
| Maestro | partiture, parti, raccolte e strumenti: aggiunge e modifica ma non vede "Elimina"; gestisce il calendario; niente libro soci |
| Direttivo | libro soci, musicisti, dati della banda e calendari; niente partiture |
| Musicista | partiture e raccolte in sola lettura e il calendario |
| Socio | solo il calendario |

La tabella completa dei permessi è in [docs/autenticazione.md](docs/autenticazione.md).

### Prova della separazione tra bande

Crea una seconda banda e un suo amministratore, poi riavvia il backend (così riceve il catalogo di strumenti di partenza):
```bash
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "INSERT INTO banda (nome, attiva, creata_il) VALUES ('Banda di prova', 1, NOW(6));"
bash scripts/create-account.sh prova@esempio.it ADMIN 2
```
Entrando con `prova@esempio.it` non si deve vedere nulla di ciò che è stato inserito nella prima banda. Per far entrare la stessa persona in due bande: `bash scripts/create-account.sh admin@partiturae.local MUSICISTA 2` (aggiunge l'account esistente alla banda 2) e poi scegli la banda dal menu.

## Struttura del repository

```
partiturae/
├── backend/                  Spring Boot
│   └── src/main/java/com/grassi/partiturae/
│       ├── banda/            bande e separazione dei dati
│       ├── auth/             login, account, ruoli per banda, permessi
│       ├── autore/ famiglia/ strumento/ strumentofiglio/
│       ├── partitura/ parte/ raccolta/
│       ├── socio/ musicista/ direttivo/ formazione/ evento/
│       ├── common/           eccezioni e classi condivise
│       └── seed/             strumenti di partenza
├── frontend/                 Angular
│   └── src/app/
│       ├── features/         una cartella per funzionalità (modelli, servizi, pagine)
│       ├── pages/            home e impostazioni
│       └── shared/           codice condiviso
├── docs/                     documentazione
├── scripts/                  script di servizio (account, password, controllo dei permessi)
├── docker-compose.yaml       database MySQL
└── README.md
```

Ogni funzionalità del backend ha il suo package con entità, repository, servizio, controller e DTO.

## Documentazione

- [Architettura](docs/architettura.md): organizzazione del codice, modello dati, API e scelte di progetto.
- [Autenticazione](docs/autenticazione.md): come funziona il login, bande e ruoli, tabella dei permessi, configurazione e gestione di account e password da terminale.

## Comandi utili

```bash
# backend: compilazione di controllo
cd backend && ./mvnw clean compile

# frontend: build e test
cd frontend && ng build
cd frontend && ng test --watch=false

# permessi: controlla che regole del backend e dell'interfaccia coincidano
node scripts/verifica-permessi.mjs

# account: crealo (o aggiungilo a un'altra banda) da terminale
bash scripts/create-account.sh maestro@esempio.it MAESTRO
bash scripts/create-account.sh mario@esempio.it MUSICISTA 2

# password: reimposta quella di un account, sbloccalo e chiudi le sue sessioni
bash scripts/reset-password.sh admin@partiturae.local

# database: account, bande e ruoli (utile per controllare chi può entrare)
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "SELECT a.email, b.nome AS banda, r.ruolo FROM appartenenza ap JOIN account a ON a.id = ap.account_id JOIN banda b ON b.id = ap.banda_id LEFT JOIN appartenenza_ruolo r ON r.appartenenza_id = ap.id ORDER BY b.nome, a.email;"
```

## Convenzioni di sviluppo

- Commit in stile [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `refactor:`, `docs:`, `chore:`), uno per argomento.
- Le funzionalità più grandi si sviluppano su un branch (`feature/nome`) e poi si uniscono a `main`.
- A ogni passo di sviluppo si aggiorna la sezione "Stato del progetto" di questo file (fatto, in lavorazione, da fare) e, se cambia qualcosa, la documentazione in `docs/`.