import sys, ctypes, struct

def copy_files_to_clipboard(files):
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
    file_bytes = file_strings.encode("utf-16le")
    
    data = header + file_bytes
    
    user32.OpenClipboard(0)
    user32.EmptyClipboard()
    
    hGlobalMem = kernel32.GlobalAlloc(0x0042, len(data))
    lpGlobalMem = kernel32.GlobalLock(hGlobalMem)
    
    ctypes.memmove(lpGlobalMem, data, len(data))
    
    kernel32.GlobalUnlock(hGlobalMem)
    user32.SetClipboardData(CF_HDROP, hGlobalMem)
    user32.CloseClipboard()

if __name__ == "__main__":
    if len(sys.argv) > 1:
        copy_files_to_clipboard(sys.argv[1:])
