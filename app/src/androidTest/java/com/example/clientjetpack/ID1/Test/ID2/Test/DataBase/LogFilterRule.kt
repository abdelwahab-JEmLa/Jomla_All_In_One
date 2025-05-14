package com.example.clientjetpack.ID1.Test.ID2.Test.DataBase

import android.util.Log
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern

class LogFilterRule private constructor(
    private val config: LogConfig
) : TestRule {
    private var testClassName = ""
    private var testMethodName = ""
    private var startTime: Long = 0
    private val logTagList = mutableListOf<String>()

    companion object {
        private const val TAG = "LogFilter"
        private val manualLogs = CopyOnWriteArrayList<LogEntry>()
        private var isCapturing = false

        @JvmStatic
        fun log(tag: String, message: String) {
            if (isCapturing) {
                manualLogs.add(LogEntry(System.currentTimeMillis(), tag, message))
                // Également envoyer au logcat standard
                Log.d("$TAG-Manual", "[$tag] $message")
            }
        }

        @JvmStatic
        fun filter(): Builder {
            return Builder()
        }
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                setupTest(description)

                try {
                    // Exécuter le test
                    base.evaluate()
                } finally {
                    // Afficher les logs filtrés
                    processLogs()
                }
            }
        }
    }

    private fun setupTest(description: Description) {
        testClassName = description.className.substringAfterLast('.')
        testMethodName = description.methodName
        startTime = System.currentTimeMillis()

        if (config.filterByMethod != null && config.filterByMethod != testMethodName) {
            return
        }

        manualLogs.clear()
        isCapturing = true

        Log.i(TAG, "Setting up log filter for: $testClassName.$testMethodName")
    }

    private fun processLogs() {
        if (config.filterByMethod != null && config.filterByMethod != testMethodName) {
            return
        }

        try {
            Log.i(TAG, "===== TEST LOGS: $testClassName.$testMethodName =====")

            if (config.captureManualLogs && manualLogs.isNotEmpty()) {
                Log.i(TAG, "-- MANUAL LOGS --")
                manualLogs.forEach { entry ->
                    Log.i(TAG, "[${formatTimestamp(entry.timestamp)}] ${entry.tag}: ${entry.message}")
                }
            }

            // Always capture logcat, regardless of the captureLogcat setting
            val logcatOutput = getLogcatSinceTime(startTime)
            val filteredLogs = filterLogs(logcatOutput)

            if (filteredLogs.isNotEmpty()) {
                Log.i(TAG, "-- FILTERED LOGCAT LOGS --")
                filteredLogs.forEach { Log.i(TAG, it) }
            }

            // 3. Capturer les logs spécifiques si un pattern est défini
            if (config.specificPattern != null) {
                val logcatOutput = getLogcatSinceTime(startTime)
                val specificLogs = logcatOutput.filter { it.contains(config.specificPattern) }

                if (specificLogs.isNotEmpty()) {
                    Log.i(TAG, "-- SPECIFIC PATTERN LOGS --")
                    specificLogs.forEach { Log.i(TAG, it) }
                }
            }

            Log.i(TAG, "===============================================")

            // Arrêter la capture manuelle
            isCapturing = false

        } catch (e: Exception) {
            Log.e(TAG, "Error processing logs: ${e.message}")
        }
    }

    private fun getLogcatSinceTime(sinceTime: Long): List<String> {
        val dateFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)
        val timeStr = dateFormat.format(Date(sinceTime))

        // Include all log levels (V for verbose)
        val logcatCmd = "logcat -v threadtime -t \"$timeStr\" *:V"
        return executeCommand(logcatCmd)
    }

    private fun filterLogs(logs: List<String>): List<String> {
        return logs.filter { line ->
            // Check if any of the configured tags match
            if (config.filterByTagList != null && config.filterByTagList.isNotEmpty()) {
                for (tag in config.filterByTagList) {
                    if (line.contains(tag)) {
                        return@filter true
                    }
                }
                return@filter false
            }

            var keep = true

            // Appliquer les filtres configurés
            if (config.filterByTag != null) {
                keep = keep && line.contains(config.filterByTag)
            }

            if (config.filterByText != null) {
                keep = keep && line.contains(config.filterByText)
            }

            if (config.filterByRegex != null) {
                keep = keep && config.filterByRegex.matcher(line).find()
            }

            keep
        }
    }

    private fun executeCommand(command: String): List<String> {
        val output = mutableListOf<String>()
        try {
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                line?.let { output.add(it) }
            }

            process.waitFor()
            reader.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error executing command: $command")
        }

        return output
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return format.format(date)
    }

    /**
     * Configuration du logger
     */
    data class LogConfig(
        val captureManualLogs: Boolean = true,
        val captureLogcat: Boolean = true, // Changed default to true
        val filterByTag: String? = null,
        val filterByTagList: List<String>? = null, // Added support for list of tags
        val filterByText: String? = null,
        val filterByMethod: String? = null,
        val filterByRegex: Pattern? = null,
        val specificPattern: String? = null
    )

    /**
     * Entrée de journal manuelle
     */
    data class LogEntry(
        val timestamp: Long,
        val tag: String,
        val message: String
    )

    /**
     * Builder pour configurer facilement le filtre
     */
    class Builder {
        private var captureManualLogs: Boolean = true
        private var captureLogcat: Boolean = true // Changed default to true
        private var filterByTag: String? = null
        private var filterByTagList = mutableListOf<String>() // Added list of tags
        private var filterByText: String? = null
        private var filterByMethod: String? = null
        private var filterByRegexPattern: String? = null
        private var specificPattern: String? = null

        fun filterByTag(tag: String) = apply {
            // Add the tag to our list instead of replacing
            this.filterByTagList.add(tag)
        }

        fun build(): LogFilterRule {
            val filterByRegex = filterByRegexPattern?.let { Pattern.compile(it) }

            val config = LogConfig(
                captureManualLogs,
                captureLogcat,
                filterByTag,
                filterByTagList,
                filterByText,
                filterByMethod,
                filterByRegex,
                specificPattern
            )

            return LogFilterRule(config)
        }
    }
}
