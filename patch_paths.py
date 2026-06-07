import os

dst_dir = r"D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\skill_agent"

def patch_dir(dst):
    for root, dirs, files in os.walk(dst):
        for item in files:
            ext = os.path.splitext(item)[1].lower()
            if ext in ['.md', '.py', '.txt', '.json']:
                path = os.path.join(root, item)
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                # Replace C: style paths with D: style paths
                new_content = content.replace("C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack", "D:/AndroidStudioProjects/ClientJetPack")
                new_content = new_content.replace("C:\\Users\\Abou Mohamed\\AndroidStudioProjects\\ClientJetPack", "D:\\AndroidStudioProjects\\ClientJetPack")
                new_content = new_content.replace("C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles", "D:/AndroidStudioProjects/ClientJetPack")
                new_content = new_content.replace("C:\\Users\\Abou Mohamed\\AndroidStudioProjects\\Light_App_Controles", "D:\\AndroidStudioProjects\\ClientJetPack")
                
                if new_content != content:
                    with open(path, 'w', encoding='utf-8') as f:
                        f.write(new_content)
                    print(f"Patched paths in: {path}")

patch_dir(dst_dir)

# Update root h_.md
root_h_md_path = r"D:\AndroidStudioProjects\ClientJetPack\h_.md"
if os.path.exists(root_h_md_path):
    with open(root_h_md_path, 'r', encoding='utf-8') as f:
        content = f.read()
    new_content = content.replace("C:/Users/Abou%20Mohamed/AndroidStudioProjects/ClientJetPack", "D:/AndroidStudioProjects/ClientJetPack")
    new_content = new_content.replace("C:\\Users\\Abou Mohamed\\AndroidStudioProjects\\ClientJetPack", "D:\\AndroidStudioProjects\\ClientJetPack")
    new_content = new_content.replace("C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles", "D:/AndroidStudioProjects/ClientJetPack")
    new_content = new_content.replace("C:\\Users\\Abou Mohamed\\AndroidStudioProjects\\Light_App_Controles", "D:\\AndroidStudioProjects\\ClientJetPack")
    if new_content != content:
        with open(root_h_md_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Patched root h_.md")
