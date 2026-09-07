import sqlite3
import os
import sys
import re
import datetime

# Ensure UTF-8 printing
sys.stdout.reconfigure(encoding='utf-8')

def format_timestamp(ts, heur):
    if not ts:
        return heur or "-"
    try:
        dt = datetime.datetime.fromtimestamp(ts / 1000)
        months = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"]
        return f"{dt.day} {months[dt.month - 1]} \\| {heur or dt.strftime('%H:%M')}"
    except Exception:
        return f"{ts} \\| {heur or '-'}"

def format_currency(val):
    if val is None or val == 0.0:
        return "-"
    return f"{val:.2f} دج"

def format_short_id(key):
    if not key:
        return "-"
    return f"`{key[-4:]}`"

def translate_query(expression):
    expression = expression.strip()
    # Mappings
    table_mappings = {
        "m8": "M8BonVent",
        "bon": "M8BonVent",
        "m8bonvent": "M8BonVent",
        "m2": "M2Client",
        "client": "M2Client",
        "m2client": "M2Client",
        "m1": "M01Produit",
        "produit": "M01Produit",
        "m01produit": "M01Produit",
        "m15": "M15Grossist",
        "grossist": "M15Grossist",
        "m15grossist": "M15Grossist",
        "m9": "M09AppCompt",
        "compt": "M09AppCompt"
    }
    
    # Split table from rest of query
    parts = expression.split(maxsplit=1)
    if not parts:
        return "", ""
        
    table_alias = parts[0].lower()
    table_name = table_mappings.get(table_alias, table_alias)
    
    condition_part = parts[1] if len(parts) > 1 else ""
    
    # Parse condition
    if not condition_part:
        return table_name, f"SELECT * FROM {table_name} LIMIT 20"
        
    # Translate "key 4 = <value>" to "keyID LIKE '%<value>'"
    # Handle optional quotes
    condition_part = re.sub(
        r'key\s+4\s*=\s*[\'"]?(\w+)[\'"]?', 
        r"keyID LIKE '%\1'", 
        condition_part, 
        flags=re.IGNORECASE
    )
    
    # For other simple conditions: if a word is on the right of = and not quoted/not numeric, quote it
    # E.g. name = val -> name = 'val'
    # We can match: = <word_without_quotes>
    def quote_match(m):
        val = m.group(1).strip()
        if (val.startswith("'") and val.endswith("'")) or (val.startswith('"') and val.endswith('"')) or val.replace('.','',1).isdigit():
            return f"= {val}"
        return f"= '{val}'"
        
    condition_part = re.sub(r'=\s*([^\s\'"]+)', quote_match, condition_part)
    
    sql = f"SELECT * FROM {table_name} WHERE {condition_part}"
    
    # Add default ordering for M8BonVent if applicable
    if table_name == "M8BonVent" and "order by" not in sql.lower():
        sql += " ORDER BY creationTimestamps DESC"
        
    return table_name, sql

def execute_and_format(db_path, table_name, sql):
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    try:
        cursor.execute(sql)
        rows = cursor.fetchall()
        
        # Get column names
        cursor.execute(f"PRAGMA table_info({table_name});")
        columns = cursor.fetchall()
        colnames = [col[1] for col in columns]
        
        if not colnames and rows:
            # Fallback if PRAGMA doesn't return anything
            colnames = [d[0] for d in cursor.description]
            
        if table_name == "M8BonVent":
            # 13 columns formatting
            headers = [
                "ID", "Date & Heure", "État (Type)", "Montant Principal", "Versement Fait", 
                "Ancien Crédit", "Nouveau Crédit", "Crédit Cumulé", "Versement", "Crédit Fait", 
                "Nouvelle Situation", "Total Sauvegardé", "Client"
            ]
            lines = []
            lines.append("| " + " | ".join(headers) + " |")
            lines.append("| " + " | ".join([":---"] * len(headers)) + " |")
            
            if not rows:
                lines.append("| " + " | ".join(["-"] * len(headers)) + " |")
                return "\n".join(lines)
                
            for row in rows:
                row_dict = dict(zip(colnames, row))
                key_id = row_dict.get("keyID")
                ts = row_dict.get("creationTimestamps")
                heur = row_dict.get("heurDebutInString")
                etat = row_dict.get("etateActuellementEst")
                montant = row_dict.get("montant_principale_du_type")
                vers_fait = row_dict.get("versement_fait")
                anc_cred = row_dict.get("ancien_credit")
                new_cred = row_dict.get("new_credit_apre_tout_fait")
                cred_cum = row_dict.get("sum_De_Credit_Fait")
                vers = row_dict.get("versement")
                cred_fait = row_dict.get("credit_fait")
                new_sit = row_dict.get("new_situation")
                tot_saved = row_dict.get("totale_saved")
                client = row_dict.get("parent_M2Client_KeyID")
                
                row_cols = [
                    format_short_id(key_id),
                    format_timestamp(ts, heur),
                    str(etat or "-"),
                    format_currency(montant),
                    format_currency(vers_fait),
                    format_currency(anc_cred),
                    format_currency(new_cred),
                    format_currency(cred_cum),
                    format_currency(vers),
                    format_currency(cred_fait),
                    format_currency(new_sit),
                    format_currency(tot_saved),
                    format_short_id(client)
                ]
                lines.append("| " + " | ".join(row_cols) + " |")
            return "\n".join(lines)
        else:
            # Generic table formatting
            if not colnames:
                colnames = [f"Col{i}" for i in range(len(rows[0]))] if rows else ["No Columns"]
                
            lines = []
            lines.append("| " + " | ".join(colnames) + " |")
            lines.append("| " + " | ".join([":---"] * len(colnames)) + " |")
            
            if not rows:
                lines.append("| " + " | ".join(["-"] * len(colnames)) + " |")
                return "\n".join(lines)
                
            for row in rows:
                row_cols = []
                for val in row:
                    if isinstance(val, bytes):
                        row_cols.append("BLOB")
                    elif val is None:
                        row_cols.append("-")
                    else:
                        cleaned_val = str(val).replace('\r','').replace('\n',' ').replace('|', '\\|')
                        row_cols.append(cleaned_val)
                lines.append("| " + " | ".join(row_cols) + " |")
            return "\n".join(lines)
            
    except Exception as e:
        return f"**Error executing query:** `{str(e)}`"
    finally:
        conn.close()

def main():
    if len(sys.argv) < 3:
        print("Usage: python query_room.py <expression> <source_file>")
        return
        
    expression = sys.argv[1]
    source_file = sys.argv[2]
    
    table_name, sql = translate_query(expression)
    
    db_path = "app_database_temp"
    
    # Run the query
    result_md = execute_and_format(db_path, table_name, sql)
    
    # Write report to copy_/room_d/last_room_d.md
    os.makedirs("copy_/room_d", exist_ok=True)
    report_content = f"""# Room Database Query Results

- **Expression**: `{expression}`
- **SQL Executed**: `{sql}`
- **Database**: `{db_path}`
- **Timestamp**: {datetime.datetime.now().isoformat()}

---

## Query Results Table

{result_md}
"""
    with open("copy_/room_d/last_room_d.md", "w", encoding="utf-8") as f:
        f.write(report_content)
        
    # Write metadata to app/src/main/java/skill_agent/room_d/last_query.md
    os.makedirs("app/src/main/java/skill_agent/room_d", exist_ok=True)
    metadata_content = f"""# Last Executed Room Query

- **Source File**: [{os.path.basename(source_file)}](file:///{source_file.replace('\\', '/')})
- **Expression**: `{expression}`
- **SQL Statement**: `{sql}`
- **Timestamp**: {datetime.datetime.now().isoformat()}
"""
    with open("app/src/main/java/skill_agent/room_d/last_query.md", "w", encoding="utf-8") as f:
        f.write(metadata_content)
        
    print("Report and metadata generated successfully.")

if __name__ == "__main__":
    main()
