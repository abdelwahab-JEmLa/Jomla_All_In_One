# Exemple exmp_1 — `cc_` déclenché depuis un TODO `on_click`

> Cet exemple montre comment le skill `cc_` traite un TODO classique de type **on_click** —
> un callback bouton non encore câblé — en rassemblant les fichiers de contexte et en les
> copiant dans le presse-papiers pour une IA externe.
>
> ⚠️ Ce skill **ne fixe pas lui-même le TODO** (c''est le rôle de `t_`).
> Il **rassemble et copie le contexte** pour que l''IA externe le fasse.

---

## 🔵 Déclencheur utilisateur

```
cc_ le todo on_click dans ProduitCard.kt
```

---

## Étape 1 — TODO identifié dans le code

Scan rapide dans le dossier actif :

```
grep -r "TODO" app/src/main/java/App4/components/ProduitCard.kt
→ ligne 54 : // TODO: on_click → ouvrir le détail du produit (on_ouvrirDetail)
```

Code autour du TODO :

```kotlin
// ProduitCard.kt  (ligne 48–60)

@Composable
fun ProduitCard(
    produit: M3CouleurProduitInfos,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable {
            // TODO: on_click → ouvrir le détail du produit (on_ouvrirDetail)
        }
    ) {
        Text(text = produit.nom)
    }
}
```

---

## Étape 2 — Fichiers rassemblés (max 10, dossier immédiat)

| # | Fichier | Raison |
|---|---------|--------|
| 1 | `ProduitCard.kt` | Fichier actif contenant le TODO |
| 2 | `M3CouleurProduitInfos.kt` | Modèle du produit (import direct) |
| 3 | `b_List_LazyColumnList_App4.kt` | Appelant de `ProduitCard` dans le même dossier |
| 4 | `ProduitDetailScreen.kt` | Écran destination de la navigation (référencé dans le même package) |

> ✅ Pas de recherche profonde dans tout le projet.
> ❌ Ne pas inclure les fichiers de navigation globale, ViewModels distants, ou modules Koin.

---

## Étape 3 — Contenu de `context_agy.md` généré

**Chemin du fichier créé** :
`skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/08_26 07_10 ProduitCard_OnClick_Detail/context_agy.md`

```markdown
fix todo avec la facon la plus rapide (ne pas ecrire "todo resolved" ou "TODO resolved" a la fin)

TODO dans `ProduitCard.kt` ligne 54 :
```kotlin
modifier = modifier.clickable {
    // TODO: on_click → ouvrir le détail du produit (on_ouvrirDetail)
}
```

**Contexte architectural** :
- `ProduitCard` est un composable appelé depuis `b_List_LazyColumnList_App4.kt` via une `LazyColumn`.
- Le composant doit déclencher un callback `on_ouvrirDetail: (M3CouleurProduitInfos) -> Unit` passé en paramètre.
- L''appelant (`b_List_LazyColumnList_App4.kt`) gère la navigation vers `ProduitDetailScreen`.
- Le modèle `M3CouleurProduitInfos` contient le champ `keyID: String` servant d''identifiant de navigation.
```

> ⛔ Ne pas dupliquer le code complet des fichiers dans `context_agy.md`.
> L''IA externe reçoit les fichiers directement via le presse-papiers.

---

## Étape 4 — Contenu de `hist_copie.md`

**Chemin** : `skill_agent/copy_context/copy_skill/references/hist_copie.md`

```markdown
### 🔗 [context_agy.md](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/08_26 07_10 ProduitCard_OnClick_Detail/context_agy.md)

### 🔗 [ProduitCard.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/App4/components/ProduitCard.kt)

### 🔗 [M3CouleurProduitInfos.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/EntreApps/Shared/Models/M3CouleurProduitInfos.kt)

### 🔗 [b_List_LazyColumnList_App4.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/App4/b_List_LazyColumnList_App4.kt)

### 🔗 [ProduitDetailScreen.kt](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/App4/components/ProduitDetailScreen.kt)
```

---

## Étape 5 — Exécution clipboard

```bash
"C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\run_cc.bat"
```

---

## Étape 6 — Résumé affiché à l''utilisateur

| Élément | Détail |
|---------|--------|
| 📄 Fichier contexte | [context_agy.md](.../08_26 07_10 ProduitCard_OnClick_Detail/context_agy.md) |
| 📁 Fichiers copiés | `ProduitCard.kt`, `M3CouleurProduitInfos.kt`, `b_List_LazyColumnList_App4.kt`, `ProduitDetailScreen.kt` |
| 📋 Clipboard | ✅ Réussi — 5 fichiers présents dans le presse-papiers |
| ⏱️ Durée | ~6 secondes |
| 🔁 Re-copier | [run_copy.bat](.../08_26 07_10 ProduitCard_OnClick_Detail/run_copy.bat) |
| 🚀 Exécuter | [run_cc_silent.vbs](...run_cc_silent.vbs) |

> 💡 Une fois le fix reçu de l''IA externe, tape **`ok_`** pour l''appliquer automatiquement dans le projet.

---

## 📌 Ce que ce skill NE fait PAS (différence avec `t_`)

| Skill | Rôle |
|-------|------|
| `t_` | **Fixe** le TODO directement dans le code |
| `cc_` | **Rassemble et copie** le contexte pour qu''une IA externe le fixe |

Le skill `cc_` est utile quand le TODO est complexe et nécessite une IA Deep-Thinking externe
(Claude Sonnet/Thinking, Gemini Pro) plutôt qu''une correction locale rapide.
