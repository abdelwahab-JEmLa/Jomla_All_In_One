package com.example.clientjetpack.Id1.PrixChangable.Test.Passive

import android.icu.util.Currency

/**
 * Mock Currency class to use in testing environments
 * This avoids the NullPointerException from Currency.getInstance()
 */
class MockCurrency private constructor(private val currencyCode: String) {
    companion object {
        // Factory method to create a MockCurrency
        fun create(currencyCode: String = "USD"): Currency {
            return try {
                // Try to get a real Currency instance
                Currency.getInstance(currencyCode)
            } catch (e: Exception) {
                // If that fails, create a mock Currency using reflection
                try {
                    val constructor = Currency::class.java.getDeclaredConstructor(String::class.java)
                    constructor.isAccessible = true
                    constructor.newInstance(currencyCode)
                } catch (e2: Exception) {
                    // If that also fails, create our test mock implementation
                    TestCurrency(currencyCode)
                }
            }
        }
    }
    
    /**
     * Minimal implementation of Currency for testing purposes
     */
    class TestCurrency(private val code: String) : Currency(code) {
        override fun getCurrencyCode(): String = code
        override fun toString(): String = code
    }
}

// Usage example:
// val currency = MockCurrency.create("USD")
