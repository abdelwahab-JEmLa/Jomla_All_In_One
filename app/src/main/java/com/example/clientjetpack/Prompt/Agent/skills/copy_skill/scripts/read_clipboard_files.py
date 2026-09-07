import ctypes
import os
import sys

def get_clipboard_files():
    CF_HDROP = 15
    CF_UNICODETEXT = 13
    
    user32 = ctypes.windll.user32
    shell32 = ctypes.windll.shell32
    kernel32 = ctypes.windll.kernel32
    
    user32.GetClipboardData.restype = ctypes.c_void_p
    kernel32.GlobalLock.argtypes = [ctypes.c_void_p]
    kernel32.GlobalLock.restype = ctypes.c_void_p
    kernel32.GlobalUnlock.argtypes = [ctypes.c_void_p]
    
    shell32.DragQueryFileW.argtypes = [ctypes.c_void_p, ctypes.c_uint, ctypes.c_wchar_p, ctypes.c_uint]
    shell32.DragQueryFileW.restype = ctypes.c_uint
    
    user32.OpenClipboard(0)
    try:
        files = []
        if user32.IsClipboardFormatAvailable(CF_HDROP):
            hDrop = user32.GetClipboardData(CF_HDROP)
            if hDrop:
                count = shell32.DragQueryFileW(hDrop, 0xFFFFFFFF, None, 0)
                for i in range(count):
                    buffer = ctypes.create_unicode_buffer(1024)
                    shell32.DragQueryFileW(hDrop, i, buffer, 1024)
                    files.append(buffer.value)
        if files: return files
        if user32.IsClipboardFormatAvailable(CF_UNICODETEXT):
            h_text = user32.GetClipboardData(CF_UNICODETEXT)
            if h_text:
                ptr = kernel32.GlobalLock(h_text)
                text = ctypes.c_wchar_p(ptr).value
                kernel32.GlobalUnlock(h_text)
                if text:
                    lines = [line.strip() for line in text.splitlines() if line.strip()]
                    valid_files = [p for p in lines if os.path.isfile(p)]
                    if valid_files: return valid_files
        return []
    finally:
        user32.CloseClipboard()

if __name__ == "__main__":
    files = get_clipboard_files()
    if not files:
        # Fallback to the last one if we want, or just exit. 
        # For max speed, let's just assume we return "EMPTY" and the agent handles it, 
        # but if we have files, we do EVERYTHING here.
        print("EMPTY")
        sys.exit(0)
        
    hist_path = r"C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_skill\references\hist_copie.md"
    os.makedirs(os.path.dirname(hist_path), exist_ok=True)
    
    package = os.path.basename(os.path.dirname(files[0]))
    
    out_table = []
    
    with open(hist_path, 'w', encoding='utf-8') as f:
        for f_path in files:
            f_url = "file:///" + f_path.replace('\\', '/')
            f.write(f"### 🔗 [{os.path.basename(f_path)}]({f_url})\n")
            
            lines = 0
            try:
                with open(f_path, 'r', encoding='utf-8') as src:
                    lines = sum(1 for _ in src)
            except: pass
            out_table.append((os.path.basename(f_path), lines))
            
    # Output the exact markdown the agent needs to print:
    print(package)
    for name, lines in out_table:
        print(f"| `{name}` | {lines} |")
