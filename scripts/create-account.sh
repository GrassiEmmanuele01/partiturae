#!/usr/bin/env bash
#
# Crea un account direttamente nel database e lo inserisce in una banda con i ruoli indicati.
# Se l'account esiste già (stessa email) non cambia la password: lo aggiunge alla banda indicata,
# così la stessa persona può avere ruoli diversi in bande diverse.
#
# Uso (dalla radice del progetto, con il database avviato):
#     bash scripts/create-account.sh maestro@esempio.it MAESTRO
#     bash scripts/create-account.sh segreteria@esempio.it ARCHIVISTA,MUSICISTA
#     bash scripts/create-account.sh mario@esempio.it MUSICISTA 2      # nella banda con id 2
#
# Ruoli ammessi: ADMIN, ARCHIVISTA, MAESTRO, MAESTROALLIEVI, DIRETTIVO, MUSICISTA, ALLIEVO, SOCIO
# (si possono unire con la virgola). Senza il terzo parametro si usa la prima banda.
# La password viene chiesta due volte, senza mostrarla (solo per un account nuovo).
#
# Variabili opzionali: DB_CONTAINER (default partiturae-db), DB_NAME (default partiturae),
# DB_PASSWORD (default root).

set -euo pipefail

CONTAINER="${DB_CONTAINER:-partiturae-db}"
DB_NAME="${DB_NAME:-partiturae}"
DB_PASSWORD="${DB_PASSWORD:-root}"

if [ "$#" -lt 2 ] || [ "$#" -gt 3 ]; then
  echo "Uso: $0 email RUOLO[,RUOLO...] [id-banda]" >&2
  echo "Ruoli: ADMIN, ARCHIVISTA, MAESTRO, MAESTROALLIEVI, DIRETTIVO, MUSICISTA, ALLIEVO, SOCIO" >&2
  exit 1
fi

EMAIL="$(printf '%s' "$1" | tr 'A-Z' 'a-z')"
RUOLI="$2"
BANDA="${3:-}"

# Email, ruoli e banda finiscono dentro una query SQL: si accettano solo valori sicuri.
if ! [[ "$EMAIL" =~ ^[a-z0-9._%+-]+@[a-z0-9.-]+$ ]]; then
  echo "Email non valida: $1" >&2
  exit 1
fi

IFS=',' read -r -a LISTA_RUOLI <<< "$RUOLI"
for RUOLO in "${LISTA_RUOLI[@]}"; do
  case "$RUOLO" in
    ADMIN|ARCHIVISTA|MAESTRO|MAESTROALLIEVI|DIRETTIVO|MUSICISTA|ALLIEVO|SOCIO) ;;
    *)
      echo "Ruolo non valido: $RUOLO" >&2
      echo "Ammessi: ADMIN, ARCHIVISTA, MAESTRO, MAESTROALLIEVI, DIRETTIVO, MUSICISTA, ALLIEVO, SOCIO" >&2
      exit 1
      ;;
  esac
done

mysql_exec() {
  docker exec -i -e MYSQL_PWD="$DB_PASSWORD" "$CONTAINER" mysql -uroot "$DB_NAME" "$@"
}

conta() {
  mysql_exec -N -B -e "$1" | tr -d '\r\n'
}

if [ -z "$BANDA" ]; then
  BANDA="$(conta "SELECT MIN(id) FROM banda")"
  if [ -z "$BANDA" ] || [ "$BANDA" = "NULL" ]; then
    echo "Non esiste nessuna banda (o il database non è raggiungibile): avvia prima il backend." >&2
    exit 1
  fi
fi

if ! [[ "$BANDA" =~ ^[0-9]+$ ]]; then
  echo "L'id della banda deve essere un numero: $BANDA" >&2
  exit 1
fi

if [ "$(conta "SELECT COUNT(*) FROM banda WHERE id = $BANDA")" != "1" ]; then
  echo "Non esiste la banda con id $BANDA." >&2
  exit 1
fi

ACCOUNT_ESISTE="$(conta "SELECT COUNT(*) FROM account WHERE email = '$EMAIL'")"
SQL=""

if [ "$ACCOUNT_ESISTE" = "0" ]; then
  read -r -s -p "Password per $EMAIL (almeno 8 caratteri): " PASSWORD
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

  # Hash BCrypt calcolato in un container temporaneo (come in reset-password.sh).
  HASH="$(docker run --rm httpd:2.4-alpine htpasswd -nbBC 10 "" "$PASSWORD" | tr -d ':\r\n' | sed 's/^\$2y/$2a/')"

  case "$HASH" in
    '$2a$10$'*) ;;
    *)
      echo "Non sono riuscito a calcolare l'hash della password (Docker è avviato?)." >&2
      exit 1
      ;;
  esac

  SQL="INSERT INTO account (email, password_hash, attivo, deve_cambiare_password, tentativi_falliti, superadmin) VALUES ('$EMAIL', '$HASH', 1, 0, 0, 0);"
else
  GIA_NELLA_BANDA="$(conta "SELECT COUNT(*) FROM appartenenza ap JOIN account ac ON ac.id = ap.account_id WHERE ac.email = '$EMAIL' AND ap.banda_id = $BANDA")"
  if [ "$GIA_NELLA_BANDA" != "0" ]; then
    echo "$EMAIL fa già parte della banda $BANDA." >&2
    exit 1
  fi
  echo "L'account esiste già: lo aggiungo alla banda $BANDA (la password non cambia)."
fi

SQL="$SQL
INSERT INTO appartenenza (account_id, banda_id, attiva) SELECT id, $BANDA, 1 FROM account WHERE email = '$EMAIL';"
for RUOLO in "${LISTA_RUOLI[@]}"; do
  SQL="$SQL
INSERT INTO appartenenza_ruolo (appartenenza_id, ruolo) SELECT ap.id, '$RUOLO' FROM appartenenza ap JOIN account ac ON ac.id = ap.account_id WHERE ac.email = '$EMAIL' AND ap.banda_id = $BANDA;"
done

printf '%s\n' "$SQL" | mysql_exec

echo "Fatto: $EMAIL è nella banda $BANDA con i ruoli $RUOLI."
