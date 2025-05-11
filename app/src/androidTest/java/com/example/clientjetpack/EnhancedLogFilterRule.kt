package com.example.clientjetpack

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.BufferedReader
import java.io.InputStreamReader

class EnhancedLogFilterRule(
    private val includeOnly: List<String> = listOf("TestRunner"),
    private val excludeTags: List<String> = listOf(
        "ConfigStore", "ANDR-PERF-MPCTL", "chatty", "NetworkSession", 
        "CNSS", "ThermalEngine", "HBMFeatureControl", "SarService"
    )
) : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                // Clear logs before test
                clearLogcat()
                
                // Suppress unnecessary logs
                suppressLogs()
                
                try {
                    // Execute the test
                    base.evaluate()
                    
                    // Filter and display logs
                    filterAndPrintLogs()
                } finally {
                    // Nothing to restore as we're just filtering output
                }
            }
        }
    }

    private fun clearLogcat() {
        try {
            Runtime.getRuntime().exec("adb logcat -c").waitFor()
        } catch (e: Exception) {
            println("Failed to clear logcat: ${e.message}")
        }
    }

    private fun suppressLogs() {
        excludeTags.forEach { tag ->
            try {
                Runtime.getRuntime().exec("adb shell setprop log.tag.$tag silent")
            } catch (e: Exception) {
                // Ignore errors
            }
        }
    }

    private fun filterAndPrintLogs() {
        try {
            val process = Runtime.getRuntime().exec("adb logcat -d")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val filteredLogs = StringBuilder()
            
            reader.useLines { lines ->
                lines.forEach { line ->
                    if (shouldIncludeLine(line)) {
                        filteredLogs.append(line).append("\n")
                    }
                }
            }
            
            // Write filtered logs to a file or output
            println("====== FILTERED TEST LOGS ======")
            println(filteredLogs.toString())
            println("================================")
        } catch (e: Exception) {
            println("Failed to filter logs: ${e.message}")
        }
    }
    
    private fun shouldIncludeLine(line: String): Boolean {
        // Include only specified tags
        if (includeOnly.isNotEmpty() && includeOnly.none { tag -> line.contains(" $tag: ") }) {
            return false
        }
        
        // Exclude specified tags
        if (excludeTags.any { tag -> line.contains(" $tag: ") }) {
            return false
        }
        
        return true
    }
}
