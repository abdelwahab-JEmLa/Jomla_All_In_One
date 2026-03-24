# Prompt — Traitement de groupedFatwas

## Utilisation
Colle ce prompt à Claude + fournis les fatwas dans **l'un de ces 3 formats** :
- **Fichier uploadé** (`.txt`, `.json`, `.md`)
- **Texte collé** directement dans le chat
- **JSON collé** dans le chat

Tu peux aussi préciser : **"dernière fatwa = N"** → la numérotation commencera à N+1.
Si rien n'est précisé, la numérotation commence à 1.

---

## LE PROMPT

```
Tu es un processeur de texte. Je peux te donner les fatwas de 3 façons différentes — détecte automatiquement le format et traite en conséquence.

Je peux aussi te préciser : "dernière fatwa = N" → dans ce cas commence la numérotation à N+1.
Si je ne précise rien → commence à 1.

---

### FORMAT A — Fichier uploadé (.txt / .md)
→ Lis le fichier et applique les étapes 1→4 ci-dessous.

### FORMAT B — Texte brut collé dans le chat
→ Peut contenir `الفتوى رقم` comme séparateur, OU des numéros `1.  2.  3.` comme séparateur.
→ Détecte lequel est présent et découpe en conséquence (étape 2B).
→ Applique les étapes 1→4.

### FORMAT C — JSON collé dans le chat
Le JSON peut avoir deux structures possibles :

  Structure 1 — tableau de strings :
  ["texte fatwa 1", "texte fatwa 2", ...]

  Structure 2 — tableau d'objets :
  [{"id": 1, "texte": "..."}, {"id": 2, "texte": "..."}, ...]
  (la clé du contenu peut s'appeler : texte, text, content, fatwa, body — prends celle qui existe)

→ Extraire chaque élément comme un bloc séparé.
→ Sauter les étapes 1 et 2 (déjà découpé) — aller directement à l'étape 3.

---

### 1. NETTOYER (formats A et B uniquement)
- Supprimer toutes les lignes composées uniquement de `═` `─` `=` `-` (séparateurs)
- Supprimer toutes les lignes qui correspondent au pattern `[DD/MM/YYYY ...]` (timestamps Telegram)
- Supprimer l'en-tête avant le premier séparateur de fatwa détecté
- Supprimer toute occurrence de la chaîne `أبو الحازم و عائش(القنوعة) الداعي للتربي عند الاكابر` (partout dans le texte)
- Réduire les sauts de ligne multiples (3+) en double saut (`\n\n`)

### 2. DÉCOUPER (formats A et B uniquement)
**2A** — Si le texte contient `الفتوى رقم` :
- Séparer à chaque occurrence de `الفتوى رقم`

**2B** — Si le texte utilise des numéros `1.  2.  3.` :
- Séparer à chaque ligne qui commence par `\d+\.\s{2,}`

- Ignorer les blocs vides dans tous les cas

### 3. HASHTAG + PADDER
- Soit `offset` = le numéro fourni par l'utilisateur ("dernière fatwa = N") → numérotation commence à N+1
- Soit `offset` = 0 si rien n'est précisé → numérotation commence à 1
- Pour chaque bloc (indexé i à partir de 0) :
  1. Calculer `num = offset + i + 1`
  2. Ajouter `#فتوى_NUM\n\n` **au début** du bloc
  3. Padder le bloc à exactement **4096 caractères** :
     - Si plus court → ajouter des `\n` à la fin jusqu'à 4096
     - Si plus long → le laisser tel quel (ne pas couper)

### 4. SORTIE — IMPORTANT
- Coller tous les blocs paddés bout à bout (sans séparateur entre eux)
- **Retourne UNIQUEMENT le fichier `sortie_groupedFatwas.txt` en texte brut**
- **NE PAS régénérer le script Python** — il est déjà créé
- Juste afficher à la toute fin (hors fichier) : N fatwas · numéros X→Y · taille totale chars
```

---

## Résultat attendu
- **Sortie : `sortie_groupedFatwas.txt` en texte brut uniquement**
- Taille : `N_fatwas × 4096` caractères
- Chaque message Telegram = exactement 4096 chars, commence par `#فتوى_N`

---

## Script Python de référence (déjà créé — NE PAS recréer)

```python
#!/usr/bin/env python3
import re, sys, time, json
from pathlib import Path

INPUT_PATH  = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("groupedFatwas.txt")
OUTPUT_PATH = Path(sys.argv[2]) if len(sys.argv) > 2 else Path("sortie_groupedFatwas.txt")
OFFSET      = int(sys.argv[3]) if len(sys.argv) > 3 else 0   # ex: 20 si "dernière fatwa = 20"
PAD_SIZE    = 4096

RE_SEP     = re.compile(r'^[═=─\-]{3,}\s*$', re.MULTILINE)
RE_TS      = re.compile(r'^\[?\d{2}/\d{2}/\d{4}[^\n]*\n?', re.MULTILINE)
RE_CHANNEL = re.compile(r'أبو الحازم و عائش\(القنوعة\) الداعي للتربي عند الاكابر\s*\n?')
RE_NL      = re.compile(r'\n{3,}')
RE_FATWA   = re.compile(r'(?=الفتوى رقم)')
RE_NUM     = re.compile(r'\n(?=\d+\.\s{2,})')

print("🚀 Démarrage...")
t0 = time.perf_counter()
raw = INPUT_PATH.read_text(encoding="utf-8")
t1 = time.perf_counter()
print(f"✅ Lecture       — {(t1-t0)*1000:.2f}ms  ({len(raw):,} chars)")

t2s = time.perf_counter()
try:
    data = json.loads(raw)
    if isinstance(data, list):
        keys = ["texte", "text", "content", "fatwa", "body"]
        blocks = []
        for item in data:
            if isinstance(item, str):
                blocks.append(item.strip())
            elif isinstance(item, dict):
                for k in keys:
                    if k in item:
                        blocks.append(str(item[k]).strip())
                        break
        blocks = [b for b in blocks if b]
        print(f"✅ Format JSON   — {len(blocks)} blocs détectés")
    else:
        raise ValueError("JSON non-liste")
except (json.JSONDecodeError, ValueError):
    text = raw.replace('\r\n', '\n').replace('\r', '\n')
    text = RE_SEP.sub('', text)
    text = RE_TS.sub('', text)
    text = RE_CHANNEL.sub('', text)
    text = RE_NL.sub('\n\n', text)
    if 'الفتوى رقم' in text:
        idx = text.find('الفتوى رقم')
        text = text[idx:]
        blocks = [b for b in RE_FATWA.split(text) if b.strip()]
        print(f"✅ Format texte (الفتوى رقم) — {len(blocks)} blocs")
    else:
        parts = RE_NUM.split(text.strip())
        blocks = [p.strip() for p in parts if p.strip()]
        print(f"✅ Format texte (numéros) — {len(blocks)} blocs")

t2e = time.perf_counter()
print(f"✅ Nettoyage/découpage — {(t2e-t2s)*1000:.2f}ms")

n = len(blocks)

t4s = time.perf_counter()
padded = []
for i, b in enumerate(blocks):
    num = OFFSET + i + 1
    b = f"#فتوى_{num}\n\n" + b
    padded.append(b.ljust(PAD_SIZE, '\n') if len(b) < PAD_SIZE else b)
t4e = time.perf_counter()
print(f"✅ Hashtag+Padding — {(t4e-t4s)*1000:.2f}ms")

t5s = time.perf_counter()
output = ''.join(padded)
OUTPUT_PATH.write_text(output, encoding="utf-8")
t5e = time.perf_counter()
print(f"✅ Écriture      — {(t5e-t5s)*1000:.2f}ms")
print("━"*40)
first, last = OFFSET+1, OFFSET+n
print(f"🏁 TOTAL : {(t5e-t0)*1000:.2f}ms | {n} fatwas · numéros {first}→{last} · {len(output):,} chars")
print(f"   Fichier : {OUTPUT_PATH.resolve()}")
```
