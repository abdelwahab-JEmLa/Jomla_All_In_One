# Skill - Schedule App Launch & Tap Replay (Schedule_agy_task_tap_l)

This skill instructs the assistant on how to start a background focus-monitoring task that waits for the user to rebuild/launch the app from Android Studio. When the app is launched and gains focus, it automatically triggers `tap_l`. If the user wants to cancel the monitoring, they can request "arrete_sch".

---

## Trigger Phrases
- "Schedule_agy_task_tap_l"
- "schedule_tap_l"
- "sch_tap_l"
- "st_"
- "arrete_sch" (cancels any running schedule task)

---

## Steps to Execute

### When "Schedule_agy_task_tap_l" is triggered:

1. **Launch the Focus Monitoring Script in the Background**:
   Run the `wait_and_tap.py` script as a background task:
   ```powershell
   python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\tap\wait_and_tap.py"
   ```
   Capture the returned task ID (e.g. `561e60f6-2e94-4fab-bdcd-d8ce1f1580e2/task-280`).

2. **Save Task ID**:
   Write the task ID directly to:
   `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\tap\active_schedule.txt`

3. **End Current Turn**:
   Inform the user that monitoring is active, stop calling tools, and return control. The user can now rebuild and launch the app from Android Studio. Once the app starts and gains focus, the script will automatically execute the tap and exit.

---

### When "arrete_sch" is triggered:

1. **Read Task ID**:
   Check if the file `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\tap\active_schedule.txt` exists and read the task ID.

2. **Cancel Task**:
   - If a task ID is found, run the `manage_task` tool with:
     - **Action**: `"kill"`
     - **TaskId**: `<task_id>`
   - Write `# No active schedule task` to `active_schedule.txt`.
   - Report to the user: "Active schedule task cancelled successfully."

3. **Fallback**:
   - If the file is empty or does not exist, report: "No active schedule task found to cancel."
