import os
import re

def find_project_root(start_dir):
    current = os.path.abspath(start_dir)
    while True:
        if os.path.exists(os.path.join(current, "settings.gradle.kts")) or os.path.exists(os.path.join(current, ".git")):
            return current
        parent = os.path.dirname(current)
        if parent == current:
            # Reached filesystem root
            return os.path.abspath(os.path.join(start_dir, "..", "..", "..", "..", "..", "..", ".."))
        current = parent

def main():
    project_root = find_project_root(os.path.dirname(__file__))
    ignore_file_path = os.path.join(project_root, ".antigravityignore")
    gemini_ignore_path = os.path.join(project_root, ".geminiignore")
    map_file_path = os.path.join(os.path.dirname(__file__), "files_affiched.md")
    
    if not os.path.exists(map_file_path):
        print(f"Error: Map file {map_file_path} not found.")
        return
        
    with open(map_file_path, "r", encoding="utf-8") as f:
        lines = f.readlines()
        
    # Find tree block
    in_block = False
    tree_lines = []
    masked_root = "app/src/main/java" # default
    
    for line in lines:
        if "Racine commune masquée :" in line:
            # Extract masked root path e.g. app / src / main / java/
            match = re.search(r'`([^`]+)`', line)
            if match:
                val = match.group(1).strip()
                if val in [".", "/"]:
                    masked_root = ""
                else:
                    masked_root = "".join(val.split()).rstrip('/')
        elif line.strip() == "```diff" or line.strip() == "<pre>":
            in_block = True
            continue
        elif line.strip() == "```" or line.strip() == "</pre>":
            in_block = False
            continue
        if in_block:
            tree_lines.append(line.rstrip('\n'))

    # Parse hierarchy
    allows = []
    denies = []
    path_parts = []
    
    for line in tree_lines:
        if not line.strip():
            continue
            
        # Clean diff prefix
        line_clean = line
        if line_clean.startswith('+ ') or line_clean.startswith('- ') or line_clean.startswith('  '):
            line_clean = line_clean[2:]
            
        if '<!--' in line_clean or '-->' in line_clean:
            continue
            
        has_allow = '++' in line_clean
        has_deny = '--' in line_clean
        
        # Clean line to extract name
        cleaned_node_line = line_clean.replace('++', '').replace('--', '').rstrip()
        
        # Match prefix characters and tree nodes
        match = re.match(r'^([│\s├└─┌]*)([^/]+/?)$', cleaned_node_line)
        if not match:
            continue
            
        prefix, name = match.groups()
        name = name.strip()
        
        # Calculate depth based on prefix length
        depth = len(prefix) // 4
        
        # Squeeze path_parts to the current depth level
        # Since depth 1 means root level, we slice to depth - 1
        level = max(0, depth - 1)
        path_parts = path_parts[:level]
        path_parts.append(name)
        
        # Reconstruct path
        if masked_root:
            rel_path = "/".join([masked_root] + path_parts)
        else:
            rel_path = "/".join(path_parts)
        rel_path = re.sub(r'/+', '/', rel_path)
        if rel_path.startswith('/'):
            rel_path = rel_path[1:]
        if name.endswith('/') and not rel_path.endswith('/'):
            rel_path += '/'
        elif not name.endswith('/') and rel_path.endswith('/'):
            rel_path = rel_path.rstrip('/')
        
        # Check marker in suffix
        if has_allow:
            allows.append(rel_path)
        elif has_deny:
            denies.append(rel_path)

    print(f"Parsed {len(allows)} allow rules (++): {allows}")
    print(f"Parsed {len(denies)} deny rules (--): {denies}")

    # Generate .antigravityignore content
    ignore_lines = []
    
    if allows:
        # If we have allow rules, start with block everything
        ignore_lines.append("# Context map active (restricted context)")
        ignore_lines.append("*")
        ignore_lines.append("")
        ignore_lines.append("# Whitelist parent folders and main directory")
        ignore_lines.append("!app/")
        ignore_lines.append("!app/src/")
        ignore_lines.append("!app/src/main/")
        ignore_lines.append("!app/src/main/java/")
        ignore_lines.append("!app/src/main/java/skill_agent/")
        ignore_lines.append("!app/src/main/java/skill_agent/**")
        ignore_lines.append("")
        
        # Whitelist allow paths and their parents
        ignore_lines.append("# Whitelisted folders and files (++)")
        for path in allows:
            # Whitelist parents of allowed path
            parts = path.split('/')
            for i in range(1, len(parts)):
                parent = "/".join(parts[:i]) + "/"
                if parent not in ["app/", "app/src/", "app/src/main/", "app/src/main/java/"]:
                    ignore_lines.append(f"!{parent}")
            # Whitelist path itself
            if path.endswith('/'):
                ignore_lines.append(f"!{path}")
                ignore_lines.append(f"!{path}**")
            else:
                ignore_lines.append(f"!{path}")
        ignore_lines.append("")
        
        # Blacklist deny paths
        if denies:
            ignore_lines.append("# Blacklisted folders and files (--)")
            for path in denies:
                if path.endswith('/'):
                    ignore_lines.append(f"{path}")
                    ignore_lines.append(f"{path}**")
                else:
                    ignore_lines.append(f"{path}")
    elif denies:
        # If we only have deny rules, do not block everything by default, just block denies
        ignore_lines.append("# Context map active (exclusions only)")
        for path in denies:
            if path.endswith('/'):
                ignore_lines.append(f"{path}")
                ignore_lines.append(f"{path}**")
            else:
                ignore_lines.append(f"{path}")
    else:
        # No rules, deactivate restrictions
        ignore_lines.append("# Context restrictions deactivated")
        ignore_lines.append("")

    content = "\n".join(ignore_lines) + "\n"
    
    # Write to both ignore files
    with open(ignore_file_path, "w", encoding="utf-8") as f:
        f.write(content)
    with open(gemini_ignore_path, "w", encoding="utf-8") as f:
        f.write(content)
        
    print(f"Successfully applied map rules to .antigravityignore and .geminiignore.")

if __name__ == "__main__":
    main()
