import sys
import os
import subprocess
import time

ADB_PATH = r"C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe"
if not os.path.exists(ADB_PATH):
    ADB_PATH = "adb"

SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
TAP_SCRIPT = os.path.join(SCRIPT_DIR, "tap_fast.py")

def get_focus():
    try:
        res = subprocess.run([ADB_PATH, "shell", "dumpsys", "window"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        for line in res.stdout.splitlines():
            if "mCurrentFocus" in line:
                return line
    except Exception:
        pass
    return ""

def is_app_focused():
    focus = get_focus()
    return "com.example.light_app_controles" in focus

def main():
    print("Starting wait_and_tap monitoring...")
    
    # Phase 1: If the app is currently focused, wait for it to be closed/unfocused during compilation/installation
    if is_app_focused():
        print("App is currently focused. Waiting for it to terminate/unfocus...")
        unfocused = False
        for _ in range(60):  # 30 seconds timeout
            time.sleep(0.5)
            if not is_app_focused():
                print("App terminated/unfocused detected.")
                unfocused = True
                break
        if not unfocused:
            print("Warning: App did not close, continuing to wait for new focus launch anyway...")

    # Phase 2: Wait for the app to become focused
    print("Waiting for the app to launch and gain focus...")
    launched = False
    for _ in range(360):  # 180 seconds timeout (generous for rebuilds)
        time.sleep(0.5)
        if is_app_focused():
            print("App launch and focus detected!")
            launched = True
            break

    if launched:
        # Give it a short moment to render the UI
        print("Waiting 1.30 seconds for UI to settle...")
        time.sleep(1.30)
        
        # Trigger tap_l
        print("Executing tap_l...")
        res = subprocess.run([sys.executable, TAP_SCRIPT, "tap_l"], capture_output=True, text=True)
        print(res.stdout)
        print(res.stderr)
    else:
        print("Timeout waiting for app launch.")
        sys.exit(1)

if __name__ == "__main__":
    main()
