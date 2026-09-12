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
    map_file_path = os.path.join(os.path.dirname(__file__), "files_affiched.md")
    
    if not os.path.exists(map_file_path):
        print(f"Error: Map file {map_file_path} not found.")
        return
        
    with open(map_file_path, "r", encoding="utf-8") as f:
        original_lines = f.readlines()
        
    # Phase 0: Clean up existing HTML coloring tags and comments to get a raw annotated tree
    cleaned_lines = []
    for line in original_lines:
        line_strip = line.strip()
        if line_strip == "<!--" or line_strip == "-->":
            continue
        # Clean up HTML tags (if any exist from old format)
        line_clean = re.sub(r'</?font[^>]*>', '', line)
        
        # Clean diff format prefixes (+ , - , or 2 spaces)
        if line_clean.startswith('+ ') or line_clean.startswith('- ') or line_clean.startswith('  '):
            line_clean = line_clean[2:]
            
        cleaned_lines.append(line_clean)

    # Separate header, tree block, and footer
    in_block = False
    header_lines = []
    tree_lines = []
    footer_lines = []
    masked_root = "app/src/main/java"
    
    for line in cleaned_lines:
        line_strip = line.strip()
        if line_strip == "```text" or line_strip == "```diff" or line_strip == "<pre>":
            in_block = True
            header_lines.append("```diff") # Use markdown diff for coloring
            continue
        elif line_strip == "```" or line_strip == "</pre>":
            in_block = False
            footer_lines.append("```")
            continue
            
        if "Racine commune masquée :" in line:
            match = re.search(r'`([^`]+)`', line)
            if match:
                masked_root = "".join(match.group(1).split()).rstrip('/')
                
        if in_block:
            tree_lines.append(line)
        else:
            if not tree_lines:
                header_lines.append(line)
            else:
                footer_lines.append(line)

    # Parse tree hierarchy
    nodes = []
    path_stack = []
    
    for line in tree_lines:
        line_raw = line.rstrip('\n')
        if not line_raw.strip():
            continue
            
        has_allow = '++' in line_raw
        has_deny = '--' in line_raw
        
        # Clean markers to extract the base folder/file name
        cleaned_node_line = line_raw.replace('++', '').replace('--', '').rstrip()
        
        match = re.match(r'^([│\s├└─┌]*)([^/]+/?)$', cleaned_node_line)
        if not match:
            continue
            
        prefix, name_with_slash = match.groups()
        name = name_with_slash.strip()
        is_dir = name.endswith('/')
        
        depth = len(prefix) // 4
        
        node = {
            'prefix': prefix,
            'name': name,
            'is_dir': is_dir,
            'has_allow': has_allow,
            'has_deny': has_deny,
            'children': [],
            'parent': None,
            'state': 'none', # 'active', 'inactive', 'none'
            'raw_line': line_raw
        }
        
        # Maintain depth path_stack to find parent
        path_stack = path_stack[:depth - 1]
        if path_stack:
            parent = path_stack[-1]
            node['parent'] = parent
            parent['children'].append(node)
            
        path_stack.append(node)
        nodes.append(node)

    # Phase 1: Compute and propagate states down the tree
    def compute_states(node, inherited_state='none'):
        if node['has_allow']:
            node['state'] = 'active'
        elif node['has_deny']:
            node['state'] = 'inactive'
        else:
            node['state'] = inherited_state
            
        for child in node['children']:
            compute_states(child, node['state'])
            
    for node in nodes:
        if node['parent'] is None:
            compute_states(node)

    # Phase 2: Compute if all sub-items under directories are active
    def compute_all_active(node):
        if not node['is_dir'] or not node['children']:
            node['is_all_children_active'] = (node['state'] == 'active')
            return node['is_all_children_active']
            
        all_active = True
        for child in node['children']:
            child_active = compute_all_active(child)
            if not child_active:
                all_active = False
                
        node['is_all_children_active'] = all_active
        return all_active

    for node in nodes:
        if node['parent'] is None:
            compute_all_active(node)

    # Phase 3: Render tree with HTML coloring and HTML comment blocks
    rendered_tree_lines = []
    
    def render_node(node, in_comment_block=False):
        raw = node['raw_line']
        diff_prefix = "  "
        if node['state'] == 'active':
            diff_prefix = "+ "
        elif node['state'] == 'inactive':
            diff_prefix = "- "
            
        colored_line = f"{diff_prefix}{raw}"
        rendered_tree_lines.append(colored_line)
        
        # Collapse children into a comment block if they are all active and not already commented
        should_comment_children = node['is_dir'] and node['children'] and node['is_all_children_active'] and not in_comment_block
        
        if should_comment_children:
            rendered_tree_lines.append("  <!--")
            
        for child in node['children']:
            render_node(child, in_comment_block or should_comment_children)
            
        if should_comment_children:
            rendered_tree_lines.append("  -->")

    # Render root nodes
    for node in nodes:
        if node['parent'] is None:
            render_node(node)

    # Write output to files_affiched.md
    output_lines = []
    # Add headers, stripping newlines to prevent double spacings
    for line in header_lines:
        output_lines.append(line.rstrip('\n'))
    output_lines.extend(rendered_tree_lines)
    for line in footer_lines:
        output_lines.append(line.rstrip('\n'))
        
    with open(map_file_path, "w", encoding="utf-8") as f:
        f.write("\n".join(output_lines) + "\n")
        
    print("Map colorization and collapsing successfully applied to files_affiched.md.")

if __name__ == "__main__":
    main()
