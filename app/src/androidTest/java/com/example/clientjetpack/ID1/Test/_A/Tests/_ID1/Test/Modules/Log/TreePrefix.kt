package com.example.clientjetpack.ID1.Test._A.Tests._ID1.Test.Modules.Log

enum class TreePrefix(private val lastItem: String, private val normalItem: String) {
    Type1("└─", "├─"),                 // For products
    Type2("  ├─", "  ├─"),             // For clients (not last product)
    Type3("  └─", "  └─"),             // For clients (last product)
    Type4("     └─", "     ├─"),       // For tarification types (last product & last client)
    Type5("  │     └─", "  │     ├─"), // For tarification types (not last client)
    Type6("          └─", "          ├─"), // For currencies (last product & last client & last type)
    Type7("  │     │  └─", "  │     │  ├─"); // For currencies (not last client or not last type)

    fun get(isLast: Boolean): String = if (isLast) lastItem else normalItem
}
