# Skill - Commit, Tag and Push

This skill instructs the assistant on how to automatically commit staged changes with a descriptive message, automatically determine an appropriate tag name (either incrementing/continuing the previous tag if it is a continuation of a bug/work, or creating a new tag), apply the tag, and push the branch and tags to the remote repository whenever the user requests "push_taged".

---

## Trigger Phrases
- "push_taged"
- "push_"
- "p_"
- "p_tag=cleanup"

---

## Steps to Execute

### 1. Identify Changed and Staged Files
First, run `git status` to see the current modifications and staged changes:
```powershell
git status
```

### 2. Identify the Most Recent Tags
List the recent tags to check the versioning history:
```powershell
git tag --list
```

### 3. Determine the New Tag Name and Commit Message
Analyze the current changes relative to the previous tag:
- **Cleanup Suffix Tag (`p_tag=cleanup`)**: If the command or user input requests `p_tag=cleanup` (or similar cleanup tag), identify the most recent semantic tag (e.g., `v1.0.0` or `v1.0.1-something`). Increment the patch version (e.g., to `v1.0.1`) and append the `+cleanup` suffix using the plus sign `+` (e.g., `v1.0.1+cleanup`). If no previous version tag exists, default to `v1.0.0+cleanup`.
- **Continuation of the previous bug/feature**: If the changes are a continuation of the work captured by the previous tag (e.g., `v1.0.0-fab-gradle-fix`), increment the patch version or append a suffix (e.g., `v1.0.1-fab-gradle-fix` or `v1.0.0-fab-gradle-fix-v2`).
- **New bug/feature**: If the changes cover a new scope, choose a new semantic version (e.g., `v1.1.0-<scope>` or increment the minor/major version).
- Draft a highly descriptive commit message summarizing the changes.

### 4. Commit the Changes
Commit the staged files using the descriptive commit message:
```powershell
git commit -m "<Commit Message>"
```

### 5. Create the Tag
Create an annotated tag on the new commit:
```powershell
git tag -a <TAG_NAME> -m "<Tag Description>"
```

### 6. Push Commit and Tags to Remote
Determine the active branch and push both the branch and the new tag to the `github` remote:
```powershell
git push github <BRANCH_NAME>
git push github <TAG_NAME>
```

### 7. Report Progress
Provide the user with a detailed summary showing:
- **Nom du Tag** : The name of the tag created (e.g. `v1.0.1-fab-gradle-fix`).
- **Commit hash & Message** : The hash and full commit message.
- **Définition/Description** : The tag's annotation description/definition.
- **Confirmation de push** : Confirmation that both branch and tags were successfully pushed to GitHub.
