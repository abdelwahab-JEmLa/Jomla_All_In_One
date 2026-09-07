import os
import re
import sys

def find_project_root(start_dir):
    current = os.path.abspath(start_dir)
    while True:
        if os.path.exists(os.path.join(current, "settings.gradle.kts")) or os.path.exists(os.path.join(current, ".git")):
            return current
        parent = os.path.dirname(current)
        if parent == current:
            return os.path.abspath(os.path.join(start_dir, "..", "..", "..", "..", "..", "..", ".."))
        current = parent

def get_file_stats(file_path):
    try:
        with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()
            lines = len(content.splitlines())
            chars = len(content)
            return lines, chars
    except Exception:
        return 0, 0

def get_dir_stats(project_root, rel_path):
    full_path = os.path.join(project_root, rel_path.replace("/", os.sep))
    if not os.path.exists(full_path):
        return 0, 0, 0
        
    if os.path.isfile(full_path):
        lines, chars = get_file_stats(full_path)
        return lines, chars, 1
        
    total_lines = 0
    total_chars = 0
    total_files = 0
    for root, dirs, files in os.walk(full_path):
        for file in files:
            file_path = os.path.join(root, file)
            lines, chars = get_file_stats(file_path)
            total_lines += lines
            total_chars += chars
            total_files += 1
    return total_lines, total_chars, total_files

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = find_project_root(script_dir)
    map_file_path = os.path.join(script_dir, "files_affiched.md")
    
    if not os.path.exists(map_file_path):
        print(f"Error: Map file {map_file_path} not found.")
        sys.exit(1)
        
    with open(map_file_path, "r", encoding="utf-8") as f:
        lines = f.readlines()
        
    masked_root = "app/src/main/java"
    tree_lines = []
    in_block = False
    
    for line in lines:
        if "Racine commune masquée :" in line:
            match = re.search(r'`([^`]+)`', line)
            if match:
                val = match.group(1).strip()
                if val in [".", "/"]:
                    masked_root = ""
                else:
                    masked_root = "".join(val.split()).rstrip('/')
        elif line.strip() in ["```text", "```diff", "<pre>"]:
            in_block = True
            continue
        elif line.strip() in ["```", "</pre>"]:
            in_block = False
            continue
        if in_block:
            tree_lines.append(line.rstrip('\n'))
            
    path_parts = []
    targets = []
    
    for line in tree_lines:
        if not line.strip():
            continue
            
        line_clean = line
        # Clean diff format prefixes (+ , - , or 2 spaces)
        if line_clean.startswith('+ ') or line_clean.startswith('- ') or line_clean.startswith('  '):
            line_clean = line_clean[2:]
            
        if '<!--' in line_clean or '-->' in line_clean:
            continue
            
        has_query = '??' in line_clean
        
        # Clean markers to extract name
        cleaned_node_line = line_clean.replace('++', '').replace('--', '').replace('??', '').rstrip()
        match = re.match(r'^([│\s├└─┌]*)([^/]+/?)$', cleaned_node_line)
        if not match:
            continue
            
        prefix, name = match.groups()
        name = name.strip()
        is_dir = name.endswith('/')
        
        depth = len(prefix) // 4
        level = max(0, depth - 1)
        path_parts = path_parts[:level]
        path_parts.append(name)
        
        if has_query:
            rel_path = "/".join([masked_root] + path_parts) if masked_root else "/".join(path_parts)
            rel_path = re.sub(r'/+', '/', rel_path)
            if rel_path.startswith('/'):
                rel_path = rel_path[1:]
            if is_dir and not rel_path.endswith('/'):
                rel_path += '/'
            elif not is_dir and rel_path.endswith('/'):
                rel_path = rel_path.rstrip('/')
                
            targets.append((name, rel_path, is_dir))
            
    if not targets:
        print("Aucun marqueur '??' trouvé dans files_affiched.md.")
        sys.exit(0)
        
    print("| Nom | Chemin | Type | Lignes | Fichiers | Tokens (est.) |")
    print("| :--- | :--- | :--- | :--- | :--- | :--- |")
    for name, rel_path, is_dir in targets:
        lines, chars, files = get_dir_stats(project_root, rel_path)
        type_str = "Dossier" if is_dir else "Fichier"
        tokens = int(chars / 3.5)
        print(f"| {name} | `{rel_path}` | {type_str} | **{lines:,}** | {files} | **{tokens:,}** |")

if __name__ == "__main__":
    main()
