# Autenticazione

## Come funziona

1. L'utente accede con email e password (`POST /api/auth/login`).
2. Il server sceglie la **banda** in cui entri (quella indicata, altrimenti l'ultima usata, altrimenti la prima) e risponde con due cose:
   - un **access token** (JWT firmato, dura 15 minuti) che contiene account, banda e ruoli in quella banda. L'applicazione lo tiene solo in memoria e lo manda a ogni richiesta nell'header `Authorization: Bearer ...`;
   - un **refresh token** (casuale, dura 7 giorni) in un cookie `HttpOnly`, quindi non leggibile dal JavaScript della pagina.
3. Quando l'access token scade, l'applicazione chiede un nuovo token (`POST /api/auth/refresh`) usando il cookie. Se più richieste lo chiedono insieme, parte una sola chiamata.
4. Ogni refresh token si può usare **una volta sola**: ad ogni rinnovo ne esce uno nuovo. Se un token già usato ricompare, il server lo considera un possibile furto e chiude tutte le sessioni dell'account.
5. Al logout (`POST /api/auth/logout`) il refresh token viene revocato e il cookie cancellato.
6. Chi fa parte di più bande può cambiarla (`POST /api/auth/banda`): il server emette un nuovo token con i ruoli di quella banda.

Nel database i refresh token sono salvati solo come hash (SHA-256): una copia del database non permette di usare le sessioni attive. Le password sono salvate con BCrypt.

### Protezioni sul login

- Dopo **5 tentativi sbagliati** l'account si blocca per **15 minuti** (risposta 429).
- Il messaggio di errore è sempre "Email o password non corrette", e il tempo di risposta non rivela se l'email esiste.

## Bande, account e ruoli

Tre concetti distinti:

- **Banda**: la formazione. Tutti i dati di lavoro (soci, partiture, eventi, strumenti...) appartengono a una banda e le altre non li vedono mai.
- **Account**: la persona che accede, con email e password. Non appartiene a una banda in particolare.
- **Appartenenza**: l'account dentro una banda, con i **ruoli che ha in quella banda**. Nella stessa banda si possono avere più ruoli insieme (valgono tutti), e in bande diverse ruoli diversi.

Il **superadmin** gestisce la piattaforma (bande e account amministratore) ed è una proprietà dell'account, non un ruolo di banda: non vede i dati delle bande.

### Separazione dei dati

Ogni dato di lavoro ha la colonna `banda_id`. Hibernate la imposta da solo quando si salva e aggiunge il filtro a ogni ricerca (anche a quelle per id): la banda arriva dal token firmato. Senza banda nel token si lavora nella "banda 0", che non esiste e quindi non contiene dati. Codice fiscale ed email dei soci sono unici dentro una banda, non in tutta la piattaforma.

### Ruoli di banda

| Ruolo | Cosa fa |
|---|---|
| `ADMIN` | l'IT della banda: tutte le funzioni |
| `ARCHIVISTA` | cura l'archivio: partiture, parti, raccolte, autori, strumenti, soci, musicisti, calendari. Elimina dall'archivio |
| `MAESTRO` | aggiunge e modifica partiture e parti, le mette in raccolta, gestisce il calendario. Non elimina |
| `MAESTROALLIEVI` | come il maestro, per la sezione giovanile |
| `DIRETTIVO` | libro soci e musicisti, informazioni e direttivo della banda, calendari |
| `MUSICISTA` | consulta repertorio e raccolte, vede il calendario |
| `ALLIEVO` | come il musicista, per la sezione giovanile |
| `SOCIO` | vede solo il calendario |

### Chi può fare cosa

Leggere = vedere; aggiungere e modificare = `POST`/`PUT`; eliminare = `DELETE`. Un ruolo che non è elencato non può fare l'operazione.

| Area | Leggere | Aggiungere e modificare | Eliminare |
|---|---|---|---|
| Libro soci, musicisti | Admin, Archivista, Direttivo | Admin, Archivista, Direttivo | Admin, Archivista, Direttivo |
| Informazioni della banda, direttivo | Admin, Archivista, Direttivo | Admin, Direttivo | Admin, Direttivo |
| Strumenti, famiglie, voci, autori | tutti tranne Socio | Admin, Archivista, Maestro, Maestro allievi | Admin, Archivista |
| Partiture | Admin, Archivista, Maestri, Musicista, Allievo | Admin, Archivista, Maestri | Admin, Archivista |
| Parti e PDF | Admin, Archivista, Maestri | Admin, Archivista, Maestri | Admin, Archivista |
| Raccolte | Admin, Archivista, Maestri, Musicista, Allievo | Admin, Archivista, Maestri | Admin, Archivista |
| Calendario | tutti | Admin, Archivista, Direttivo, Maestri | Admin, Archivista, Direttivo, Maestri |
| Presenze | Admin, Archivista, Direttivo, Maestri | Admin, Archivista, Direttivo, Maestri | Admin, Archivista, Direttivo, Maestri |

Casi particolari:

- Il **logo** della banda lo legge chiunque sia nella banda.
- L'elenco "dove è usato" (`/{id}/utilizzo`) è per Admin e Archivista, perché mostra i nomi di musicisti e partiture.
- Togliere **una** partitura da una raccolta è una modifica (la fanno anche i maestri); eliminare la raccolta intera no.
- Il superadmin non passa da nessuna di queste regole.

Le regole stanno in `backend/.../auth/PermessiApi.java`; l'interfaccia ne usa una copia (`frontend/src/app/features/auth/permessi.ts`) solo per nascondere ciò che non si può usare. Per controllare che le due non divergano:

```bash
node scripts/verifica-permessi.mjs
```

Lo script confronta le regole del backend, gli endpoint che esistono davvero nei controller e la tabella dell'interfaccia (serve Node 22.6 o più recente). Se cambi un permesso, cambialo in entrambi i posti e rilancia il controllo.

### Cosa manca ancora

- Le sezioni banda e giovanile, e i due calendari: oggi `MAESTROALLIEVI` e `ALLIEVO` hanno gli stessi accessi di `MAESTRO` e `MUSICISTA`.
- Musicisti e allievi che vedono le parti dei propri strumenti e creano raccolte personali (per ora non vedono le parti).
- Il cestino, e le schermate per gestire bande e account.

## Configurazione

Le impostazioni stanno in `backend/src/main/resources/application.properties` (prefisso `app.security`). Ognuna si può sovrascrivere con una variabile d'ambiente (`app.security.cookie-secure` diventa `APP_SECURITY_COOKIE_SECURE`).

| Proprietà | Default | Significato |
|---|---|---|
| `jwt-secret` | variabile `PARTITURAE_JWT_SECRET` | chiave per firmare i token, almeno 32 caratteri. Se manca ne viene generata una a ogni avvio (le sessioni si rinnovano da sole) |
| `access-token-minutes` | 15 | durata dell'access token |
| `refresh-token-days` | 7 | durata della sessione |
| `cookie-secure` | false | `true` quando il sito è in HTTPS |
| `allowed-origins` | `http://localhost:4200` | indirizzi da cui il frontend può chiamare l'API |
| `initial-band-name` | `La mia banda` | nome della prima banda, creata al primo avvio |
| `admin-email` | `admin@partiturae.local` | email del primo account |
| `admin-password` | variabile `PARTITURAE_ADMIN_PASSWORD` | password del primo account; se vuota ne viene generata una |
| `max-failed-attempts` | 5 | tentativi prima del blocco |
| `lock-minutes` | 15 | durata del blocco |

**In produzione** imposta almeno `PARTITURAE_JWT_SECRET`, `APP_SECURITY_COOKIE_SECURE=true` (con HTTPS), `APP_SECURITY_ALLOWED_ORIGINS` con l'indirizzo reale del sito e una password diversa da `root` per il database.

## Primo avvio

Al primo avvio il backend crea la prima banda (`La mia banda`) e, se la tabella `account` è vuota, il primo account (`admin@partiturae.local`), che è **superadmin** e **ADMIN** di quella banda. Inserisce anche gli strumenti di partenza nelle bande con il catalogo vuoto.

- se `PARTITURAE_ADMIN_PASSWORD` è impostata, usa quella;
- altrimenti genera una password e la scrive **una volta sola** nel log, in un riquadro ben visibile.

## Gestire le password da terminale

Finché il cambio password dall'interfaccia non c'è, queste sono le operazioni da terminale. Il database deve essere avviato (`docker compose up -d --wait`). I comandi usano il container `partiturae-db`, utente `root` e password `root`: se hai cambiato i valori in `docker-compose.yaml`, adattali (lo script legge `DB_CONTAINER`, `DB_NAME` e `DB_PASSWORD`).

### Creare un account, o aggiungerlo a un'altra banda

```bash
bash scripts/create-account.sh maestro@esempio.it MAESTRO
bash scripts/create-account.sh segreteria@esempio.it ARCHIVISTA,MUSICISTA
bash scripts/create-account.sh mario@esempio.it MUSICISTA 2      # nella banda con id 2
```

I ruoli ammessi sono `ADMIN`, `ARCHIVISTA`, `MAESTRO`, `MAESTROALLIEVI`, `DIRETTIVO`, `MUSICISTA`, `ALLIEVO`, `SOCIO` (separati da virgola). Senza il terzo parametro si usa la prima banda. Se l'email esiste già, lo script non cambia la password: aggiunge l'account alla banda indicata con i ruoli dati, così la stessa persona può avere ruoli diversi in bande diverse.

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
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "SELECT id, email, superadmin, attivo, tentativi_falliti, bloccato_fino, ultimo_accesso FROM account;"
```

Account, bande e ruoli:
```bash
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "SELECT a.email, b.nome AS banda, r.ruolo FROM appartenenza ap JOIN account a ON a.id = ap.account_id JOIN banda b ON b.id = ap.banda_id LEFT JOIN appartenenza_ruolo r ON r.appartenenza_id = ap.id ORDER BY b.nome, a.email;"
```

### Disattivare un account

Chi è disattivato non può più entrare né rinnovare la sessione. Si chiudono anche le sessioni aperte:
```bash
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "UPDATE account SET attivo = 0 WHERE email = 'nome@esempio.it'; DELETE FROM refresh_token WHERE account_id IN (SELECT id FROM account WHERE email = 'nome@esempio.it');"
```
Per riattivarlo: stesso comando con `attivo = 1` (senza la parte `DELETE`).

### Ripartire da zero (nessuno riesce più ad entrare)

Se non c'è un altro amministratore e vuoi un nuovo account con una password scelta da te, svuota gli account e riavvia il backend con la variabile (la banda e i suoi dati restano; il nuovo primo account diventa superadmin e ADMIN della prima banda):
```bash
docker exec -it partiturae-db mysql -uroot -proot partiturae -e "DELETE FROM refresh_token; DELETE FROM appartenenza_ruolo; DELETE FROM appartenenza; DELETE FROM account;"

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
| "Il tuo account non è collegato a nessuna banda attiva" | l'account non ha appartenenze: aggiungilo a una banda con `scripts/create-account.sh email RUOLO id-banda` |
| Il menu mostra poche voci o una pagina rimanda alla Home con un avviso | il ruolo che hai nella banda corrente non permette quell'area: controlla i ruoli e la banda selezionata nel menu |
| Il login risponde ma non resta la sessione | il cookie non viene accettato: frontend e backend devono essere su `localhost`, e `allowed-origins` deve contenere l'indirizzo del frontend |
| Errore CORS nel browser | `allowed-origins` non contiene l'indirizzo da cui apri il frontend |
| Dopo ogni riavvio del backend ti chiede di nuovo il login | in sviluppo la chiave dei token cambia a ogni avvio: l'app rinnova da sola la sessione; se non succede, imposta `PARTITURAE_JWT_SECRET` |

## Limiti noti

- Aprire il sito in due schede **nello stesso istante** può far chiudere la sessione: il rinnovo con lo stesso cookie viene scambiato per un riuso. Aprendole una dopo l'altra non succede.
- Il cambio password dall'interfaccia, la gestione degli account e quella delle bande non ci sono ancora (si usano i comandi di questa pagina).