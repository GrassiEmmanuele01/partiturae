#!/usr/bin/env bash
#
# Reimposta la password di un account direttamente nel database.
# Serve quando non si riesce più ad entrare (password dimenticata) e non c'è un altro
# amministratore che possa cambiarla dall'applicazione.
#
# Uso (dalla radice del progetto, con il database avviato):
#     ./scripts/reset-password.sh admin@partiturae.local
#
# Cosa fa:
#   1. chiede la nuova password (due volte, senza mostrarla)
#   2. ne calcola l'hash BCrypt con uno strumento dentro Docker (non serve installare nulla)
#   3. aggiorna l'account, lo sblocca e chiude tutte le sue sessioni aperte
#
# Variabili opzionali: DB_CONTAINER (default partiturae-db), DB_NAME (default partiturae),
# DB_PASSWORD (default root).

set -euo pipefail

CONTAINER="${DB_CONTAINER:-partiturae-db}"
DB_NAME="${DB_NAME:-partiturae}"
DB_PASSWORD="${DB_PASSWORD:-root}"

if [ "$#" -ne 1 ]; then
  echo "Uso: $0 email-dell-account" >&2
  exit 1
fi

EMAIL="$1"

# L'email finisce dentro una query SQL: si accettano solo caratteri sicuri.
if ! [[ "$EMAIL" =~ ^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+$ ]]; then
  echo "Email non valida: $EMAIL" >&2
  exit 1
fi

mysql_exec() {
  docker exec -i -e MYSQL_PWD="$DB_PASSWORD" "$CONTAINER" mysql -uroot "$DB_NAME" "$@"
}

ESISTE="$(mysql_exec -N -B -e "SELECT COUNT(*) FROM account WHERE email = '$EMAIL'" | tr -d '\r\n')"
if [ "$ESISTE" != "1" ]; then
  echo "Nessun account con email $EMAIL (o il database non è raggiungibile)." >&2
  exit 1
fi

read -r -s -p "Nuova password (almeno 8 caratteri): " PASSWORD
echo
read -r -s -p "Ripeti la password: " PASSWORD2
echo

if [ "$PASSWORD" != "$PASSWORD2" ]; then
  echo "Le due password non coincidono." >&2
  exit 1
fi
if [ "${#PASSWORD}" -lt 8 ]; then
  echo "La password deve avere almeno 8 caratteri." >&2
  exit 1
fi

# htpasswd (incluso nell'immagine httpd) produce un hash BCrypt compatibile con Spring Security.
# L'output è ":$2y$10$..." -> si toglie il ":" iniziale e si usa il prefisso $2a.
HASH="$(docker run --rm httpd:2.4-alpine htpasswd -nbBC 10 "" "$PASSWORD" | tr -d ':\r\n' | sed 's/^\$2y/$2a/')"

case "$HASH" in
  '$2a$10$'*) ;;
  *)
    echo "Non sono riuscito a calcolare l'hash della password (Docker è avviato?)." >&2
    exit 1
    ;;
esac

mysql_exec <<SQL
UPDATE account
   SET password_hash = '$HASH',
       deve_cambiare_password = 0,
       tentativi_falliti = 0,
       bloccato_fino = NULL
 WHERE email = '$EMAIL';
DELETE FROM refresh_token WHERE account_id IN (SELECT id FROM account WHERE email = '$EMAIL');
SQL

echo "Password aggiornata per $EMAIL. Le sessioni aperte sono state chiuse."