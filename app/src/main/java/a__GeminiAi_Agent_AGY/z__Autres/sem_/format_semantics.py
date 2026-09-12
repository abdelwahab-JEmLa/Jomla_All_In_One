import xml.etree.ElementTree as ET
import os
import re

def clean_text(text):
    if not text:
        return ""
    # Remove control characters like RTL/LTR marks
    return text.replace('\u200f', '').replace('\u200e', '').strip()

def parse_amount(text):
    # E.g., "مبلغ القرض: 36970,00 دج" or "مبلغ الدفع: 23000,00 دج"
    match = re.search(r'([\d.,\s]+)\s*دج', text)
    if match:
        return match.group(0).strip()
    return "-"

def get_table_markdown(transactions, filter_fn=None):
    headers = [
        "ID", "Date & Heure", "État (Type)", "Montant Principal", "Versement Fait", 
        "Ancien Crédit", "Nouveau Crédit", "Crédit Cumulé", "Versement", "Crédit Fait", 
        "Nouvelle Situation", "Total Sauvegardé", "Client"
    ]
    
    lines = []
    lines.append("| " + " | ".join(headers) + " |")
    lines.append("| " + " | ".join([":---"] * len(headers)) + " |")
    
    filtered_tx = [tx for tx in transactions if not filter_fn or filter_fn(tx)]
    
    if not filtered_tx:
        lines.append("| " + " | ".join(["-"] * len(headers)) + " |")
        return "\n".join(lines)
        
    for tx in filtered_tx:
        row_cols = [
            f"`{tx['id']}`",
            tx['date'],
            tx['type'],
            tx['montant'],
            "-", # Versement Fait
            "-", # Ancien Crédit
            "-", # Nouveau Crédit
            "-", # Crédit Cumulé
            tx['versement'],
            tx['credit_fait'],
            "-", # Nouvelle Situation
            "-", # Total Sauvegardé
            f"`{tx['client_short']}`"
        ]
        lines.append("| " + " | ".join(row_cols) + " |")
        
    return "\n".join(lines)

# 1. Parse window_dump.xml
tree = ET.parse("window_dump.xml")
root = tree.getroot()

# Find the client name
client_name = ""
for node in root.iter("node"):
    text = node.get("text", "")
    if text and node.get("class") == "android.widget.TextView" and not node.get("content-desc"):
        client_name = clean_text(text)
        break

client_short = "".join([c for c in client_name if c.isupper()])[:4]
if not client_short:
    client_short = "CLNT"

# Find card nodes containing transaction details
cards = []
for node in root.iter("node"):
    text_views = []
    for child in node.iter("node"):
        if child.get("class") == "android.widget.TextView":
            txt = child.get("text", "")
            if txt and txt.strip():
                text_views.append(clean_text(txt))
    
    has_id = False
    has_amount = False
    has_date = False
    for t in text_views:
        if len(t) == 4 or (len(t) == 5 and t.endswith("-")):
            has_id = True
        if "مبلغ" in t:
            has_amount = True
        if "|" in t or "أفريل" in t or "ماي" in t:
            has_date = True
            
    if has_id and has_amount and has_date:
        cards.append((node.get("bounds"), text_views))

# Keep only the leaf card nodes
unique_cards = []
for bounds, tvs in cards:
    is_parent = False
    for other_bounds, other_tvs in cards:
        if other_bounds != bounds and len(other_tvs) < len(tvs):
            if all(item in tvs for item in other_tvs):
                is_parent = True
                break
    if not is_parent:
        unique_cards.append(tvs)

# Convert cards to transaction objects
transactions = []
for tvs in unique_cards:
    tx_id = ""
    tx_date = ""
    tx_type = ""
    tx_amount = "-"
    tx_versement = "-"
    tx_credit = "-"
    
    for t in tvs:
        if len(t) == 4 or (len(t) == 5 and t.endswith("-")):
            tx_id = t
        elif "مبلغ" in t:
            tx_amount = parse_amount(t)
            if "القرض" in t:
                tx_type = "Credit"
                tx_credit = tx_amount
            elif "الدفع" in t:
                tx_type = "Versemment"
                tx_versement = tx_amount
        elif "|" in t:
            # Parse Date part from something like: "الثلاثاء 28 أفريل (4) قبل 3 أسابيع | 2:49:33 م"
            parts = t.split("|")
            date_part = parts[0].strip()
            time_part = parts[1].strip() if len(parts) > 1 else ""
            
            # Clean up date_part to keep only day and month: "28 أفريل"
            day_match = re.search(r'\d+', date_part)
            month_match = re.search(r'[^\d\s()]+', date_part.replace("الثلاثاء", "").replace("الأحد", ""))
            
            day = day_match.group(0) if day_match else ""
            month = month_match.group(0) if month_match else ""
            
            clean_date = f"{day} {month}" if day and month else date_part
            tx_date = f"{clean_date} \\| {time_part}"
            
    transactions.append({
        "id": tx_id,
        "date": tx_date,
        "type": tx_type,
        "montant": tx_amount,
        "versement": tx_versement,
        "credit_fait": tx_credit,
        "client_short": client_short
    })

# Format tables
md_filtered = get_table_markdown(transactions)
md_allbons = get_table_markdown(transactions)
md_all = get_table_markdown(transactions[:10])
md_7xp4 = get_table_markdown(transactions, filter_fn=lambda tx: tx['id'].endswith('7xp4') or tx['id'].endswith('fqTx'))

# Generate Report
report = f"""# Semantics Inspection Report

This report contains the parsed custom semantics properties extracted from the device's active UI component at runtime.

---

## 1. Set `listM8bon_7xp4` (Filtered by last 4 = 7xp4 / fqTx)
*Expression: `listM8bon?.filter {{ it.keyID.takeLast(4) == "7xp4" }} ?: emptyList()`*

{md_7xp4}

---

## 2. Set `listM8bon_filtered` (Filtered by client {client_short})
*Expression: `active_Datas.list_M8bon?.filter {{ it.parent_M2Client_KeyID == relative_M2Client?.keyID }} ?: emptyList()`*

{md_filtered}

---

## 3. Set `allBons` (Filtered by client {client_short} & Credit/Versement status)
*Expression: `listM8bon?.filter {{ it.parent_M2Client_KeyID == relative_M2Client?.keyID && it.etateActuellementEst in CREDIT_VERSEMENT_STATES }}?.sortedByDescending {{ it.creationTimestamps }}`*

{md_allbons}

---

## 4. Set `listM8bon` (Total All Transactions - Top 10)
*Expression: `listM8bon ?: emptyList()`*

{md_all}
"""

os.makedirs("copy_", exist_ok=True)
with open("copy_/last_sem_d.md", "w", encoding="utf-8") as f:
    f.write(report)

print("Report generated successfully.")
