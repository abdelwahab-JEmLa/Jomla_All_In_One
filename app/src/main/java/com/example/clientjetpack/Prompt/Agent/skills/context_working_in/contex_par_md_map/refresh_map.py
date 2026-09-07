import os
import re
import subprocess
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

def parse_existing_annotations(map_file_path):
    annotations = {}
    if os.path.exists(map_file_path):
        with open(map_file_path, "r", encoding="utf-8") as f:
            lines = f.readlines()
            
        in_block = False
        tree_lines = []
        masked_root = "app/src/main/java"
        
        for line in lines:
            if "Racine commune masquée :" in line:
                match = re.search(r'`([^`]+)`', line)
                if match:
                    val = match.group(1).strip()
                    if val in [".", "/"]:
                        masked_root = ""
                    else:
                        masked_root = "".join(val.split()).rstrip('/')
            elif line.strip() in ["```diff", "```text", "<pre>"]:
                in_block = True
                continue
            elif line.strip() in ["```", "</pre>"]:
                in_block = False
                continue
            if in_block:
                tree_lines.append(line.rstrip('\n'))
                
        path_parts = []
        for line in tree_lines:
            if not line.strip():
                continue
            line_clean = line
            # Strip comments / html comment syntax if color_map.py has run
            if line_clean.startswith('+ ') or line_clean.startswith('- ') or line_clean.startswith('  '):
                line_clean = line_clean[2:]
            if '<!--' in line_clean or '-->' in line_clean:
                continue
                
            has_allow = '++' in line_clean
            has_deny = '--' in line_clean
            
            # Clean markers to extract name
            cleaned_node_line = line_clean.replace('++', '').replace('--', '').rstrip()
            match = re.match(r'^([│\s├└─┌]*)([^/]+/?)$', cleaned_node_line)
            if not match:
                continue
                
            prefix, name = match.groups()
            name = name.strip()
            
            depth = len(prefix) // 4
            level = max(0, depth - 1)
            path_parts = path_parts[:level]
            path_parts.append(name)
            
            if masked_root:
                rel_path = "/".join([masked_root] + path_parts)
            else:
                rel_path = "/".join(path_parts)
            rel_path = re.sub(r'/+', '/', rel_path)
            if rel_path.startswith('/'):
                rel_path = rel_path[1:]
            # Ensure trailing slash for directory mapping
            if name.endswith('/') and not rel_path.endswith('/'):
                rel_path += '/'
            elif not name.endswith('/') and rel_path.endswith('/'):
                rel_path = rel_path.rstrip('/')
                
            if has_allow:
                annotations[rel_path] = "++"
            elif has_deny:
                annotations[rel_path] = "--"
                
    if not annotations:
        print("No annotations parsed from map file. Using fallback defaults.")
        annotations = {
            "app/src/main/java/EntreApps/Shared/": "++",
            "app/src/main/java/EntreApps/Shared/Ui/Views/FastEdite_OutlinedTextField.kt": "--",
            "app/src/main/java/EntreApps/Shared/Ui/Views/FastEdite_OutlinedTextField_2.kt": "--"
        }
    else:
        print(f"Successfully parsed {len(annotations)} existing annotations.")
        
    return annotations

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = find_project_root(script_dir)
    map_file_path = os.path.join(script_dir, "files_affiched.md")
    search_dir = os.path.join(project_root, "app", "src", "main", "java")
    
    # 1. Parse existing annotations
    annotations = parse_existing_annotations(map_file_path)
    
    # 2. Scan directory tree
    all_files = []
    
    # Add build.gradle files if they exist
    for extra_file in ["build.gradle.kts", "app/build.gradle.kts"]:
        if os.path.exists(os.path.join(project_root, extra_file)):
            all_files.append(extra_file)
            
    if os.path.exists(search_dir):
        for root, dirs, files in os.walk(search_dir):
            for file in files:
                full_path = os.path.join(root, file)
                rel_path = os.path.relpath(full_path, project_root).replace("\\", "/")
                all_files.append(rel_path)
    
    all_files.sort()
    
    # 3. Build tree
    tree = {}
    for path in all_files:
        parts = path.split('/')
        current = tree
        for part in parts:
            if part not in current:
                current[part] = {}
            current = current[part]
            
    # 4. Render tree with annotations
    markdown_lines = ["# files_affiched.md\n", "Voici l'arborescence des dossiers et des fichiers du contexte actif :\n"]
    
    curr = tree
    skip_levels = 0
    common_path = []
    while len(curr) == 1:
        key = list(curr.keys())[0]
        if len(curr[key]) == 0:
            break
        common_path.append(key)
        curr = curr[key]
        skip_levels += 1

    if common_path:
        markdown_lines.append(f"Racine commune masquée : `{' / '.join(common_path)}/` \n")
    else:
        markdown_lines.append("Racine commune masquée : `.` \n")

    markdown_lines.append("```diff")

    def render(node, current_path_parts, prefix="  "):
        keys = sorted(node.keys())
        for i, key in enumerate(keys):
            is_last = (i == len(keys) - 1)
            connector = "└── " if is_last else "├── "
            is_file = (len(node[key]) == 0)
            
            node_path_parts = current_path_parts + [key]
            rel_path = "/".join(node_path_parts)
            rel_path = re.sub(r'/+', '/', rel_path)
            if not is_file:
                rel_path += "/"
                
            annot = annotations.get(rel_path, "")
            annot_suffix = f"            {annot}" if annot else ""
            
            if is_file:
                markdown_lines.append(f"{prefix}{connector}{key}{annot_suffix}")
            else:
                markdown_lines.append(f"{prefix}{connector}{key}/{annot_suffix}")
                new_prefix = prefix + ("    " if is_last else "│   ")
                render(node[key], node_path_parts, new_prefix)

    if common_path:
        render(curr, common_path, "")
    else:
        render(tree, [], "")

    markdown_lines.append("```")

    # Write back to files_affiched.md
    with open(map_file_path, "w", encoding="utf-8") as f:
        f.write("\n".join(markdown_lines) + "\n")
    print(f"Refreshed map with annotations at: {map_file_path}")
    
    # 5. Run apply_map.py
    apply_script = os.path.join(script_dir, "apply_map.py")
    print("Running apply_map.py...")
    subprocess.run([sys.executable, apply_script], check=True)
    
    # 6. Run color_map.py
    color_script = os.path.join(script_dir, "color_map.py")
    print("Running color_map.py...")
    subprocess.run([sys.executable, color_script], check=True)
    
    print("Refresh, ignore sync, and colorization complete.")

if __name__ == "__main__":
    main()
