# Partiturae

Gestionale per l'archivio di una formazione musicale (banda, orchestra): libro soci e musicisti, catalogo delle partiture con le parti in PDF per ogni strumento, raccolte, calendario con presenze, direttivo e dati della formazione.

## Funzionalità

- **Libro soci e musicisti**: anagrafica, iscrizione annuale (libro soci e federazione), profilo musicale con gli strumenti suonati.
- **Direttivo e formazione**: cariche con periodo di mandato, dati e logo della formazione.
- **Strumenti**: famiglie, strumenti e voci (es. Tromba → Tromba 1, Tromba 2). Prima di eliminare qualcosa l'app mostra dove è ancora usato.
- **Partiture e parti**: autori, tipologie, un PDF per ogni parte. Un PDF può valere per più strumenti (es. "Corno in Fa 1-2") e viene scaricato con un nome standard, come `Ottavino1_InnoDiMameli.pdf`.
- **Raccolte**: elenchi ordinati di partiture.
- **Calendario**: prove, concerti e assemblee, con le presenze dei soci.
- **Accesso con login**: sessioni sicure con token. I permessi per ruolo e la gestione degli account dall'interfaccia sono in arrivo (vedi [docs/autenticazione.md](docs/autenticazione.md)).

## Stack

- **Backend**: Spring Boot 4.1.1 (Java 17), Spring Data JPA, Spring Security (JWT)
- **Frontend**: Angular 21 (componenti standalone, zoneless, signals)
- **Database**: MySQL 8.0 via Docker Compose

## Prerequisiti

- JDK 17+
- Node.js 20+ e npm
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
Parte su **http://localhost:9000**. Al primo avvio crea gli strumenti di base e il primo amministratore (vedi "Primo accesso").

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

Al primo avvio, se non esiste nessun account, il backend crea l'amministratore `admin@partiturae.local` e scrive la password **una sola volta** nel log, dentro un riquadro "Creato il primo account amministratore". Copiala da lì.

Se preferisci scegliere tu la password, impostala prima di avviare il backend (funziona solo a tabella `account` vuota):
```bash
export PARTITURAE_ADMIN_PASSWORD='la-tua-password'
./mvnw spring-boot:run
```
(in PowerShell: `$env:PARTITURAE_ADMIN_PASSWORD='la-tua-password'`)

Password dimenticata o account bloccato? Vedi la sezione "Gestire le password da terminale" in [docs/autenticazione.md](docs/autenticazione.md).

## Struttura del repository

```
partiturae/
├── backend/                  Spring Boot
│   └── src/main/java/com/grassi/partiturae/
│       ├── auth/             login, account, ruoli, sessioni
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
├── scripts/                  script di servizio (es. reset della password)
├── docker-compose.yaml       database MySQL
└── README.md
```

Ogni funzionalità del backend ha il suo package con entità, repository, servizio, controller e DTO.

## Documentazione

- [Architettura](docs/architettura.md): organizzazione del codice, modello dati, API e scelte di progetto.
- [Autenticazione](docs/autenticazione.md): come funziona il login, configurazione e gestione delle password da terminale.

## Comandi utili

```bash
# backend: compilazione di controllo
cd backend && ./mvnw clean compile

# frontend: build e test
cd frontend && ng build
cd frontend && ng test --watch=false

# database: elenco account (utile per controllare chi può entrare)
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "SELECT id, email, attivo, ultimo_accesso FROM account;"
```

## Convenzioni di sviluppo

- Commit in stile [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `refactor:`, `docs:`, `chore:`), uno per argomento.
- Le funzionalità più grandi si sviluppano su un branch (`feature/nome`) e poi si uniscono a `main`.