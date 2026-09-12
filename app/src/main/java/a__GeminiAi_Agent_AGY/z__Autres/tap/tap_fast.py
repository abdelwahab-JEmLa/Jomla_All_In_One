import sys
import os
import subprocess
import xml.etree.ElementTree as ET
import re

ADB_PATH = r"C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe"
if not os.path.exists(ADB_PATH):
    ADB_PATH = "adb"

# File to store the last tap coordinates
SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
LAST_TAP_FILE = os.path.join(SCRIPT_DIR, "last_tap.txt")

def run_cmd(cmd):
    result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    return result.stdout.strip(), result.stderr.strip()

def main():
    query = sys.argv[1].lower() if len(sys.argv) > 1 else ""

    # Launch app + Replay last tap (shel_tap_l)
    if query == "shel_tap_l":
        print("Launching the app on the phone via ADB...")
        run_cmd([
            ADB_PATH, "shell", "am", "start", "-n",
            "com.example.light_app_controles/com.example.light_app_controles.A.Main.MainActivity"
        ])
        import time
        print("Waiting 1.5 seconds for the app to start...")
        time.sleep(1.5)
        query = "tap_l"

    # Replay last tap (tap_l)
    if query == "tap_l":
        if os.path.exists(LAST_TAP_FILE):
            try:
                with open(LAST_TAP_FILE, "r") as f:
                    coords = f.read().strip()
                m = re.match(r"^(\d+)\s+(\d+)$", coords)
                if m:
                    cx, cy = m.groups()
                    print(f"Replaying last tap at coordinates ({cx}, {cy}) instantly...")
                    out, err = run_cmd([ADB_PATH, "shell", "input", "tap", cx, cy])
                    print("Tap executed successfully.")
                    sys.exit(0)
                else:
                    print("Error: Invalid coordinates format in last_tap.txt.")
            except Exception as e:
                print(f"Error reading last_tap.txt: {e}")
        else:
            print("Error: No previous tap coordinates found (last_tap.txt does not exist).")
        sys.exit(1)

    # 1. Dump UI hierarchy
    print("Dumping UI hierarchy from device...")
    run_cmd([ADB_PATH, "shell", "uiautomator", "dump", "/sdcard/window_dump.xml"])

    # 2. Pull dump
    temp_xml = os.path.join(SCRIPT_DIR, "temp_window_dump.xml")
    run_cmd([ADB_PATH, "pull", "/sdcard/window_dump.xml", temp_xml])

    if not os.path.exists(temp_xml):
        print("Error: Could not retrieve UI layout dump from device.")
        sys.exit(1)

    # 3. Parse XML
    try:
        tree = ET.parse(temp_xml)
        root = tree.getroot()
    except Exception as e:
        print(f"Error parsing UI XML: {e}")
        sys.exit(1)
    finally:
        if os.path.exists(temp_xml):
            os.remove(temp_xml)

    # 4. Search for node
    best_node = None
    best_score = -1

    def parse_bounds(bounds_str):
        m = re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", bounds_str)
        if m:
            return [int(x) for x in m.groups()]
        return None

    for node in root.iter("node"):
        text = node.get("text", "")
        content_desc = node.get("content-desc", "")
        resource_id = node.get("resource-id", "")
        bounds_str = node.get("bounds", "")
        clickable = node.get("clickable", "") == "true"
        
        if not bounds_str:
            continue
        
        bounds = parse_bounds(bounds_str)
        if not bounds:
            continue
        
        x1, y1, x2, y2 = bounds
        cx = (x1 + x2) // 2
        cy = (y1 + y2) // 2
        
        if not query:
            # Check for FAB / Menu
            if content_desc.lower() == "menu" or text.lower() == "menu":
                best_node = (cx, cy, "Menu button (content-desc='Menu')")
                break
            # Fallback to bottom-right clickable button/view if not found yet
            if clickable and cx > 750 and cy > 1200:
                score = cx + cy
                if score > best_score:
                    best_score = score
                    best_node = (cx, cy, f"Bottom-right clickable element ({node.get('class')})")
        else:
            # Match query in text, content-desc, or resource-id
            node_text_lower = text.lower()
            node_desc_lower = content_desc.lower()
            node_id_lower = resource_id.lower()
            
            if query in node_text_lower or query in node_desc_lower or query in node_id_lower:
                score = 0
                if query == node_text_lower or query == node_desc_lower:
                    score += 100
                if clickable:
                    score += 50
                if query in node_text_lower:
                    score += 10
                
                if score > best_score:
                    best_score = score
                    best_node = (cx, cy, f"Matched '{text or content_desc or resource_id}'")

    if best_node:
        cx, cy, desc = best_node
        print(f"Target located: {desc} at coordinates ({cx}, {cy})")
        
        # Save coordinates for future replay (tap_l)
        try:
            with open(LAST_TAP_FILE, "w") as f:
                f.write(f"{cx} {cy}")
        except Exception as e:
            print(f"Warning: Could not save coordinates to last_tap.txt: {e}")

        print("Sending tap input via ADB...")
        out, err = run_cmd([ADB_PATH, "shell", "input", "tap", str(cx), str(cy)])
        print("Tap executed successfully.")
    else:
        print(f"Error: Target '{query or 'FAB/Menu'}' not found in the current screen layout.")
        sys.exit(1)

if __name__ == "__main__":
    main()
