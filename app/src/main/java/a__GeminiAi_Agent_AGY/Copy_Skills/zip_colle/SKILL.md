---
name: zip_colle
description: Extract the latest downloaded zip/rar or direct kotlin file from downloads, find its matching file in the project, and overwrite/update it. Trigger this when the user says "zip_colle", "colle_", "colle", "z_", or "z_c".
---

# Zip Colle (zip_colle)

Ce skill instruit l'assistant pour récupérer le dernier fichier téléchargé (archive `.rar`/`.zip` ou fichier Kotlin `.kt` direct), chercher leurs correspondances exactes dans le projet et les écraser avec les nouvelles versions.

## Trigger Phrases
- "zip_colle"
- "colle_"
- "colle"
- "ok_"
- "ok"
- "z_"
- "z_c"

## Objectif
Mettre à jour rapidement des fichiers sources du projet avec de nouvelles versions envoyées par une IA externe (ou téléchargées) sous forme d'archive ou de fichier direct de manière ultra-rapide et automatisée en un seul script.

## Steps to Execute

### 1. Exécuter le Script d'Automatisation
Exécutez la commande PowerShell suivante pour traiter automatiquement le dernier téléchargement (recherche, extraction, correspondance et remplacement) :
```powershell
powershell -ExecutionPolicy Bypass -File "C:\Users\Abou Mohamed\.gemini\config\skills\Copy_Skills\zip_colle\scripts\zip_colle.ps1"
```

### 2. Rapport et Nettoyage
Affichez directement à l'utilisateur le compte-rendu généré par le script sous forme de **liens Markdown cliquables** (ex: `### 🔗 [Fichier.kt](file:///...)`).
