#!/usr/bin/env node
/**
 * Controlla che i permessi siano coerenti in tutto il progetto:
 *   - le regole del backend (PermessiApi.java)
 *   - gli endpoint che esistono davvero nei controller
 *   - la tabella usata dall'interfaccia (permessi.ts)
 *
 * Uso, dalla radice del progetto:   node scripts/verifica-permessi.mjs
 * Esce con errore se trova una differenza, quindi si può usare anche prima di un commit.
 */
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { spawnSync } from 'node:child_process';

const JAVA = path.join('backend', 'src', 'main', 'java');
const PERMESSI_JAVA = path.join(JAVA, 'com', 'grassi', 'partiturae', 'auth', 'PermessiApi.java');
const PERMESSI_TS = path.join('frontend', 'src', 'app', 'features', 'auth', 'permessi.ts');
// Aree personali: mostrano solo i dati di chi le apre, quindi non sono per tutti i ruoli (nemmeno per l'admin)
const PERSONALI = ['/api/mie-parti'];
const RUOLI = ['ADMIN', 'ARCHIVISTA', 'MAESTRO', 'MAESTROALLIEVI', 'DIRETTIVO', 'MUSICISTA', 'ALLIEVO', 'SOCIO'];

for (const file of [PERMESSI_JAVA, PERMESSI_TS]) {
  if (!fs.existsSync(file)) {
    console.error('File non trovato: ' + file + ' (lancia lo script dalla radice del progetto)');
    process.exit(2);
  }
}

// ---------- 1) regole del backend ----------
const java = fs.readFileSync(PERMESSI_JAVA, 'utf8').replace(/\/\/[^\n]*/g, '');
const costanti = {};
for (const m of java.matchAll(/private static final String (\w+) = Ruolo\.(\w+)\.name\(\);/g)) costanti[m[1]] = m[2];

const gruppi = {};
for (const m of java.matchAll(/private static final String\[\] (\w+) = \{([^}]*)\};/g)) {
  gruppi[m[1]] = m[2].split(',').map((x) => x.trim()).filter(Boolean).map((x) => costanti[x]);
}

const corpo = java.slice(java.indexOf('auth\n'));
const regole = [];
const regex = /\.requestMatchers\(([^)]*)\)\s*\.(hasAnyRole\((\w+)\)|permitAll\(\)|authenticated\(\)|denyAll\(\))/g;
for (const m of corpo.matchAll(regex)) {
  const argomenti = m[1].split(',').map((a) => a.trim());
  const metodo = ['GET', 'POST', 'PUT', 'DELETE'].includes(argomenti[0]) ? argomenti[0] : null;
  const pattern = argomenti.filter((a) => a.startsWith('"')).map((a) => a.replaceAll('"', ''));
  const decisione = m[3] ? { tipo: 'ruoli', ruoli: gruppi[m[3]] } : { tipo: m[2].split('(')[0] };
  regole.push({ metodo, pattern, decisione });
}
regole.push({ metodo: null, pattern: ['/**'], decisione: { tipo: 'denyAll' } }); // anyRequest().denyAll()

function corrisponde(pattern, percorso) {
  const p = pattern.split('/').filter(Boolean);
  const q = percorso.split('/').filter(Boolean);
  for (let i = 0; i < p.length; i++) {
    if (p[i] === '**') return true;
    if (i >= q.length) return false;
    if (p[i] !== '*' && p[i] !== q[i]) return false;
  }
  return p.length === q.length;
}

function consentito(metodo, percorso, ruolo) {
  for (const regola of regole) {
    if (regola.metodo && regola.metodo !== metodo) continue;
    if (!regola.pattern.some((pt) => corrisponde(pt, percorso))) continue;
    const { tipo, ruoli } = regola.decisione;
    if (tipo === 'permitAll' || tipo === 'authenticated') return true;
    if (tipo === 'denyAll') return false;
    return ruoli.includes(ruolo);
  }
  return false;
}

// ---------- 2) endpoint reali dei controller ----------
function trovaControllers(dir) {
  const out = [];
  for (const voce of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, voce.name);
    if (voce.isDirectory()) out.push(...trovaControllers(p));
    else if (voce.name.endsWith('Controller.java')) out.push(p);
  }
  return out;
}

const endpoint = new Set();
for (const file of trovaControllers(JAVA)) {
  const testo = fs.readFileSync(file, 'utf8');
  const base = testo.match(/@RequestMapping\("([^"]+)"\)/);
  if (!base) continue;
  for (const m of testo.matchAll(/@(Get|Post|Put|Delete)Mapping(?:\(\s*(?:value\s*=\s*)?"([^"]*)"[^)]*\))?/g)) {
    const percorso = (base[1] + (m[2] ?? '')).replace(/\{[^}]+\}/g, '1');
    endpoint.add(m[1].toUpperCase() + ' ' + percorso);
  }
}
const elenco = [...endpoint].sort().map((e) => e.split(' ')).filter(([, p]) => !p.startsWith('/api/auth'));

// ---------- 3) tabella dell'interfaccia (permessi.ts) ----------
const cartella = fs.mkdtempSync(path.join(os.tmpdir(), 'permessi-'));
fs.writeFileSync(
  path.join(cartella, 'permessi.ts'),
  fs.readFileSync(PERMESSI_TS, 'utf8').replace(/^import \{ Ruolo \}[^\n]*\n/m, '')
);
fs.writeFileSync(path.join(cartella, 'dump.ts'), "import { PERMESSI } from './permessi.ts';\nconsole.log(JSON.stringify(PERMESSI));\n");
const esito = spawnSync(process.execPath, ['--experimental-strip-types', path.join(cartella, 'dump.ts')], { encoding: 'utf8' });
if (esito.status !== 0) {
  console.error('Non riesco a leggere permessi.ts (serve Node 22.6 o più recente):\n' + esito.stderr);
  process.exit(2);
}
const interfaccia = JSON.parse(esito.stdout);

function areaAzione(metodo, percorso) {
  if (metodo === 'GET' && percorso === '/api/formazione/logo') return null; // aperto a tutta la banda
  if (metodo === 'GET' && /^\/api\/[^/]+\/1\/utilizzo$/.test(percorso)) return ['catalogo', 'eliminare'];
  if (metodo === 'DELETE' && percorso === '/api/raccolte/1/partiture/1') return ['raccolte', 'scrivere'];
  const risorsa = percorso.split('/')[2];
  const aree = {
    soci: 'soci', musicisti: 'musicisti', direttivo: 'direttivo', formazione: 'formazione', famiglie: 'catalogo',
    strumenti: 'catalogo', 'strumenti-figli': 'catalogo', autori: 'catalogo', partiture: 'partiture', parti: 'parti',
    raccolte: 'raccolte', 'mie-parti': 'mieParti', eventi: percorso.includes('/presenze') ? 'presenze' : 'calendario'
  };
  const azioni = { GET: 'leggere', POST: 'scrivere', PUT: 'scrivere', DELETE: 'eliminare' };
  return [aree[risorsa], azioni[metodo]];
}

// ---------- confronti ----------
const problemi = [];
for (const [metodo, percorso] of elenco) {
  const personale = PERSONALI.some((p) => percorso.startsWith(p));
  if (!personale && !consentito(metodo, percorso, 'ADMIN')) problemi.push(`ADMIN bloccato su ${metodo} ${percorso}`);
  if (consentito(metodo, percorso, 'SUPERADMIN')) problemi.push(`SUPERADMIN ammesso su ${metodo} ${percorso}`);
}
for (const percorso of ['/api/non-esiste', '/api/soci/1/segreto/2', '/altro']) {
  if (consentito('GET', percorso, 'ADMIN') && !percorso.startsWith('/api/soci')) problemi.push(`percorso sconosciuto aperto: ${percorso}`);
}

let confronti = 0;
for (const [metodo, percorso] of elenco) {
  const aa = areaAzione(metodo, percorso);
  for (const ruolo of RUOLI) {
    const backend = consentito(metodo, percorso, ruolo);
    const front = aa === null ? true : interfaccia[aa[0]][aa[1]].includes(ruolo);
    confronti++;
    if (backend !== front) {
      problemi.push(`backend e interfaccia non coincidono: ${ruolo} ${metodo} ${percorso} (backend: ${backend ? 'sì' : 'no'}, interfaccia: ${front ? 'sì' : 'no'})`);
    }
  }
}

console.log(`Regole del backend: ${regole.length - 1}, endpoint nei controller: ${elenco.length}, confronti ruolo x endpoint: ${confronti}`);
if (problemi.length > 0) {
  console.error('\nPROBLEMI TROVATI:');
  for (const p of problemi) console.error('  - ' + p);
  process.exit(1);
}
console.log('OK: backend, endpoint reali e interfaccia coincidono.');