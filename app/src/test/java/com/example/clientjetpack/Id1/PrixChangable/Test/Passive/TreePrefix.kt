package com.example.clientjetpack.Id1.PrixChangable.Test.Passive

    enum class TreePrefix(private val lastItem: String, private val normalItem: String) {
        Type1("└─", "├─"),                 // For products
        Type2("  ├─", "  ├─"),             // For clients (not last product)
        Type3("  └─", "  └─"),             // For clients (last product)
        Type4("     └─", "     ├─");       // For tarification types (last client)

        fun get(isLast: Boolean): String = if (isLast) lastItem else normalItem
    }
