# Autenticazione

## Come funziona

1. L'utente accede con email e password (`POST /api/auth/login`).
2. Il server risponde con due cose:
   - un **access token** (JWT firmato, dura 15 minuti), che l'applicazione tiene solo in memoria e manda a ogni richiesta nell'header `Authorization: Bearer ...`;
   - un **refresh token** (casuale, dura 7 giorni) in un cookie `HttpOnly`, quindi non leggibile dal JavaScript della pagina.
3. Quando l'access token scade, l'applicazione chiede un nuovo token (`POST /api/auth/refresh`) usando il cookie. Se più richieste lo chiedono insieme, parte una sola chiamata.
4. Ogni refresh token si può usare **una volta sola**: ad ogni rinnovo ne esce uno nuovo. Se un token già usato ricompare, il server lo considera un possibile furto e chiude tutte le sessioni dell'account.
5. Al logout (`POST /api/auth/logout`) il refresh token viene revocato e il cookie cancellato.

Nel database i refresh token sono salvati solo come hash (SHA-256): una copia del database non permette di usare le sessioni attive. Le password sono salvate con BCrypt.

### Protezioni sul login

- Dopo **5 tentativi sbagliati** l'account si blocca per **15 minuti** (risposta 429).
- Il messaggio di errore è sempre "Email o password non corrette", e il tempo di risposta non rivela se l'email esiste.

## Ruoli

| Ruolo | Scopo |
|---|---|
| `ADMIN_BANDA` | gestisce tutto, compresi gli account |
| `MAESTRO` | direzione musicale: repertorio, raccolte, eventi |
| `ARCHIVISTA` | cura l'archivio: partiture, parti e PDF |
| `MUSICISTA` | utente standard: vede solo le parti dei propri strumenti |

Un account può avere più ruoli. I ruoli sono già nel token; **i permessi per ruolo sulle singole operazioni non sono ancora attivi** (per ora basta essere entrati). Arrivano nei prossimi passi, insieme alla gestione degli account e al cambio password dall'interfaccia.

## Configurazione

Le impostazioni stanno in `backend/src/main/resources/application.properties` (prefisso `app.security`). Ognuna si può sovrascrivere con una variabile d'ambiente (`app.security.cookie-secure` diventa `APP_SECURITY_COOKIE_SECURE`).

| Proprietà | Default | Significato |
|---|---|---|
| `jwt-secret` | variabile `PARTITURAE_JWT_SECRET` | chiave per firmare i token, almeno 32 caratteri. Se manca ne viene generata una a ogni avvio (le sessioni si rinnovano da sole) |
| `access-token-minutes` | 15 | durata dell'access token |
| `refresh-token-days` | 7 | durata della sessione |
| `cookie-secure` | false | `true` quando il sito è in HTTPS |
| `allowed-origins` | `http://localhost:4200` | indirizzi da cui il frontend può chiamare l'API |
| `admin-email` | `admin@partiturae.local` | email del primo amministratore |
| `admin-password` | variabile `PARTITURAE_ADMIN_PASSWORD` | password del primo amministratore; se vuota ne viene generata una |
| `max-failed-attempts` | 5 | tentativi prima del blocco |
| `lock-minutes` | 15 | durata del blocco |

**In produzione** imposta almeno `PARTITURAE_JWT_SECRET`, `APP_SECURITY_COOKIE_SECURE=true` (con HTTPS), `APP_SECURITY_ALLOWED_ORIGINS` con l'indirizzo reale del sito e una password diversa da `root` per il database.

## Primo amministratore

Al primo avvio, se la tabella `account` è vuota, il backend crea l'amministratore (`admin@partiturae.local`):

- se `PARTITURAE_ADMIN_PASSWORD` è impostata, usa quella;
- altrimenti genera una password e la scrive **una volta sola** nel log, in un riquadro ben visibile.

## Gestire le password da terminale

Finché il cambio password dall'interfaccia non c'è, queste sono le operazioni da terminale. Il database deve essere avviato (`docker compose up -d --wait`). I comandi usano il container `partiturae-db`, utente `root` e password `root`: se hai cambiato i valori in `docker-compose.yaml`, adattali (lo script legge `DB_CONTAINER`, `DB_NAME` e `DB_PASSWORD`).

### Cambiare la password di un account (consigliato)

Lo script chiede la nuova password due volte senza mostrarla. Aggiorna l'account, lo sblocca e chiude tutte le sue sessioni:

```bash
bash scripts/reset-password.sh admin@partiturae.local
```

Serve Docker: il calcolo dell'hash BCrypt avviene in un container temporaneo (`httpd:2.4-alpine`, scaricato la prima volta). Non serve installare altro. Dopo il reset la password è quella scelta da te e l'account non deve più cambiarla.

Se vedi errori strani tipo `$'\r': command not found`, il file ha le righe in stile Windows. Correggi con:
```bash
sed -i 's/\r$//' scripts/reset-password.sh
```

### Cambiarla a mano, senza lo script

```bash
HASH="$(docker run --rm httpd:2.4-alpine htpasswd -nbBC 10 "" 'NuovaPassword123' | tr -d ':\r\n' | sed 's/^\$2y/$2a/')"

docker exec -i -e MYSQL_PWD=root partiturae-db mysql -uroot partiturae <<SQL
UPDATE account
   SET password_hash = '$HASH', deve_cambiare_password = 0, tentativi_falliti = 0, bloccato_fino = NULL
 WHERE email = 'admin@partiturae.local';
DELETE FROM refresh_token WHERE account_id IN (SELECT id FROM account WHERE email = 'admin@partiturae.local');
SQL
```
Attenzione: la password scritta nel comando resta nella cronologia del terminale. Preferisci lo script.

### Sbloccare un account bloccato

Dopo troppi tentativi sbagliati il blocco passa da solo in 15 minuti. Per toglierlo subito:
```bash
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "UPDATE account SET tentativi_falliti = 0, bloccato_fino = NULL WHERE email = 'admin@partiturae.local';"
```

### Vedere gli account

```bash
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "SELECT id, email, attivo, tentativi_falliti, bloccato_fino, ultimo_accesso FROM account;"
```

### Disattivare un account

Chi è disattivato non può più entrare né rinnovare la sessione. Si chiudono anche le sessioni aperte:
```bash
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "UPDATE account SET attivo = 0 WHERE email = 'nome@esempio.it'; DELETE FROM refresh_token WHERE account_id IN (SELECT id FROM account WHERE email = 'nome@esempio.it');"
```
Per riattivarlo: stesso comando con `attivo = 1` (senza la parte `DELETE`).

### Ripartire da zero (nessuno riesce più ad entrare)

Se non c'è un altro amministratore e vuoi un nuovo account con una password scelta da te, svuota gli account e riavvia il backend con la variabile:
```bash
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "DELETE FROM refresh_token; DELETE FROM account_ruolo; DELETE FROM account;"

cd backend
export PARTITURAE_ADMIN_PASSWORD='la-tua-password'
./mvnw spring-boot:run
```
(in PowerShell: `$env:PARTITURAE_ADMIN_PASSWORD='la-tua-password'`). Questo cancella **tutti** gli account, non solo il tuo.

## Risoluzione dei problemi

| Sintomo | Cosa controllare |
|---|---|
| `Could not connect to server` con curl | il backend non è partito: guarda il terminale e aspetta `Started PartituraeApplication`; controlla che MySQL sia acceso (`docker ps`) |
| "Email o password non corrette" | password sbagliata o account disattivato: reimposta con lo script |
| "Troppi tentativi falliti" | account bloccato: attendi 15 minuti o sbloccalo come sopra |
| Il login risponde ma non resta la sessione | il cookie non viene accettato: frontend e backend devono essere su `localhost`, e `allowed-origins` deve contenere l'indirizzo del frontend |
| Errore CORS nel browser | `allowed-origins` non contiene l'indirizzo da cui apri il frontend |
| Dopo ogni riavvio del backend ti chiede di nuovo il login | in sviluppo la chiave dei token cambia a ogni avvio: l'app rinnova da sola la sessione; se non succede, imposta `PARTITURAE_JWT_SECRET` |

## Limiti noti

- Aprire il sito in due schede **nello stesso istante** può far chiudere la sessione: il rinnovo con lo stesso cookie viene scambiato per un riuso. Aprendole una dopo l'altra non succede.
- Il cambio password dall'interfaccia e la gestione degli account non ci sono ancora (si usano i comandi di questa pagina).