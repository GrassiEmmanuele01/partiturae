# Partiturae

Gestionale per l'archivio partiture di una banda musicale: anagrafica bandisti, catalogo partiture con le relative parti (PDF), strumenti e famiglie di strumenti.

## Stack

- **Backend**: Spring Boot 4.1.1 (Java 17), Spring Data JPA, MySQL
- **Frontend**: Angular 21 (standalone components, zoneless, signals)
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
Il flag `--wait` aspetta che MySQL sia effettivamente pronto prima di restituire il controllo (evita errori di connessione se avvii il backend troppo presto).

Per fermarlo:
```bash
docker compose down
```
Aggiungi `-v` se vuoi anche cancellare i dati (`docker compose down -v`).

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```
Parte su **http://localhost:9000**.

> Nota: la porta di default (8080/8083) può risultare occupata da intervalli riservati da Windows/Hyper-V. Se capita, controlla con `netsh interface ipv4 show excludedportrange protocol=tcp` e scegli una porta libera in `backend/src/main/resources/application.properties` (proprietà `server.port`), aggiornando poi `apiUrl` nei file di `frontend/src/environments/`.

### 3. Frontend

Al primo avvio, installa le dipendenze (su Windows potrebbe servire `--legacy-peer-deps` per un bug noto di npm):
```bash
cd frontend
npm install --legacy-peer-deps
```

Poi, ad ogni avvio:
```bash
ng serve
```
Parte su **http://localhost:4200**.

## Struttura del repository