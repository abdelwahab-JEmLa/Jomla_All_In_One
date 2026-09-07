import os
import ctypes
import re
import struct

def set_clipboard_files(files):
    CF_HDROP = 15
    user32 = ctypes.windll.user32
    kernel32 = ctypes.windll.kernel32

    kernel32.GlobalAlloc.argtypes = [ctypes.c_uint, ctypes.c_size_t]
    kernel32.GlobalAlloc.restype = ctypes.c_void_p
    kernel32.GlobalLock.argtypes = [ctypes.c_void_p]
    kernel32.GlobalLock.restype = ctypes.c_void_p
    kernel32.GlobalUnlock.argtypes = [ctypes.c_void_p]
    user32.SetClipboardData.argtypes = [ctypes.c_uint, ctypes.c_void_p]
    user32.SetClipboardData.restype = ctypes.c_void_p

    pFiles = 20
    header = struct.pack("IIIII", pFiles, 0, 0, 0, 1)
    
    file_strings = "\0".join(files) + "\0\0"
    data = header + file_strings.encode("utf-16le")
    
    user32.OpenClipboard(0)
    user32.EmptyClipboard()
    
    hGlobalMem = kernel32.GlobalAlloc(0x0042, len(data))
    lpGlobalMem = kernel32.GlobalLock(hGlobalMem)
    
    ctypes.memmove(lpGlobalMem, data, len(data))
    
    kernel32.GlobalUnlock(hGlobalMem)
    user32.SetClipboardData(CF_HDROP, hGlobalMem)
    user32.CloseClipboard()

def fast_cc():
    hist_path = r"C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_skill\references\hist_copie.md"
    
    if not os.path.exists(hist_path):
        print("EMPTY")
        return
        
    paths = []
    with open(hist_path, 'r', encoding='utf-8') as f:
        for line in f:
            match = re.search(r'file:///(.*?)\)', line)
            if match:
                path = match.group(1).replace('%20', ' ')
                path = os.path.normpath(path)
                if os.path.exists(path):
                    paths.append(path)
                    
    if not paths:
        print("EMPTY")
        return
        
    set_clipboard_files(paths)
    
    package = os.path.basename(os.path.dirname(paths[0])) if paths else "Unknown"
    print(package)
    for path in paths:
        lines = 0
        try:
            with open(path, 'r', encoding='utf-8') as src:
                lines = sum(1 for _ in src)
        except:
            pass
        print(f"| `{os.path.basename(path)}` | {lines} |")

if __name__ == "__main__":
    fast_cc()
