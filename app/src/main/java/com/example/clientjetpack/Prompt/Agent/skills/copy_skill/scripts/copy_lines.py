import sys, ctypes

def copy_text_to_clipboard(text):
    CF_UNICODETEXT = 13
    data = (text + "\0").encode("utf-16le")
    
    ctypes.windll.user32.OpenClipboard(0)
    ctypes.windll.user32.EmptyClipboard()
    
    hGlobalMem = ctypes.windll.kernel32.GlobalAlloc(0x0042, len(data))
    lpGlobalMem = ctypes.windll.kernel32.GlobalLock(hGlobalMem)
    
    ctypes.memmove(lpGlobalMem, data, len(data))
    
    ctypes.windll.kernel32.GlobalUnlock(hGlobalMem)
    ctypes.windll.user32.SetClipboardData(CF_UNICODETEXT, hGlobalMem)
    ctypes.windll.user32.CloseClipboard()

if __name__ == "__main__":
    if len(sys.argv) > 1:
        copy_text_to_clipboard(sys.argv[1])
    else:
        # If piped input
        if not sys.stdin.isatty():
            text = sys.stdin.read()
            copy_text_to_clipboard(text)
