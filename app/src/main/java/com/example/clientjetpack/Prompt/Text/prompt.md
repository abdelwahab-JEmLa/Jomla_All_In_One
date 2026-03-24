# Prompt — Traitement de groupedFatwas.txt

## Utilisation
Colle ce prompt à Claude + soit le texte brut dans le chat, soit le fichier uploadé — traite ce qui est fourni.

---

## LE PROMPT

```
Tu es un processeur de texte. Parfois je te colle le texte directement dans le chat, parfois je te fournis un fichier uploadé — dans les deux cas, traite ce que je te donne et fais exactement ces étapes :

### 1. NETTOYER
- Supprimer toutes les lignes composées uniquement du caractère `═` (séparateurs)
- Supprimer toutes les lignes qui correspondent au pattern `[DD/MM/YYYY ...]` (timestamps Telegram)
- Supprimer l'en-tête avant la première occurrence de `الفتوى رقم`
- Réduire les sauts de ligne multiples (3+) en double saut (`\n\n`)

### 2. DÉCOUPER
- Chaque fatwa commence par `الفتوى رقم`
- Séparer le texte en blocs individuels à chaque occurrence de `الفتوى رقم`
- Ignorer les blocs vides

### 3. PADDER
- Chaque bloc doit faire exactement **4096 caractères**
- Si le bloc est plus court → ajouter des `\n` à la fin jusqu'à 4096
- Si le bloc est plus long → le laisser tel quel (ne pas couper)

### 4. SORTIE — IMPORTANT
- Coller tous les blocs paddés bout à bout (sans séparateur entre eux)
- **Retourne UNIQUEMENT le fichier `sortie_groupedFatwas.txt` en texte brut**
- **NE PAS régénérer le script Python** — il est déjà créé
- Juste afficher à la toute fin (hors fichier) : N fatwas · taille totale chars
```

---

## Résultat attendu
- **Sortie : `sortie_groupedFatwas.txt` en texte brut uniquement**
- Taille : `N_fatwas × 4096` caractères
- Chaque message Telegram = exactement 4096 chars

---

## Script Python de référence (déjà créé — NE PAS recréer)
Le script `pad_fatawa.py` ci-dessous est le modèle optimisé à utiliser tel quel :

```python
#!/usr/bin/env python3
import re, sys, time
from pathlib import Path

INPUT_PATH  = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("groupedFatwas.txt")
OUTPUT_PATH = Path(sys.argv[2]) if len(sys.argv) > 2 else Path("sortie_groupedFatwas.txt")
PAD_SIZE    = 4096

RE_SEP   = re.compile(r'^[═=─\-]{3,}\s*$', re.MULTILINE)
RE_TS    = re.compile(r'^\[?\d{2}/\d{2}/\d{4}[^\n]*\n?', re.MULTILINE)
RE_NL    = re.compile(r'\n{3,}')
RE_FATWA = re.compile(r'(?=الفتوى رقم)')

print("🚀 Démarrage...")
t0 = time.perf_counter()
text = INPUT_PATH.read_text(encoding="utf-8")
t1 = time.perf_counter()
print(f"✅ Lecture       — {(t1-t0)*1000:.2f}ms  ({len(text):,} chars)")

t2s = time.perf_counter()
text = text.replace('\r\n','\n').replace('\r','\n')
text = RE_SEP.sub('', text)
text = RE_TS.sub('', text)
idx = text.find('الفتوى رقم')
if idx != -1: text = text[idx:]
text = RE_NL.sub('\n\n', text)
t2e = time.perf_counter()
print(f"✅ Nettoyage     — {(t2e-t2s)*1000:.2f}ms")

t3s = time.perf_counter()
blocks = [b for b in RE_FATWA.split(text) if b.strip()]
n = len(blocks)
t3e = time.perf_counter()
print(f"✅ Découpage ({n} fatwas) — {(t3e-t3s)*1000:.2f}ms")

t4s = time.perf_counter()
padded = [b.ljust(PAD_SIZE,'\n') if len(b) < PAD_SIZE else b for b in blocks]
t4e = time.perf_counter()
print(f"✅ Padding       — {(t4e-t4s)*1000:.2f}ms")

t5s = time.perf_counter()
output = ''.join(padded)
OUTPUT_PATH.write_text(output, encoding="utf-8")
t5e = time.perf_counter()
print(f"✅ Écriture      — {(t5e-t5s)*1000:.2f}ms")
print("━"*40)
print(f"🏁 TOTAL : {(t5e-t0)*1000:.2f}ms | {n} fatwas × {PAD_SIZE} = {len(output):,} chars")
print(f"   Fichier : {OUTPUT_PATH.resolve()}")
```
