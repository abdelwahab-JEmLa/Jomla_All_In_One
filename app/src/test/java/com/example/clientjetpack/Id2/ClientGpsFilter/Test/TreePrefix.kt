package com.example.clientjetpack.Id2.ClientGpsFilter.Test

enum class TreePrefix(private val lastItem: String, private val normalItem: String) {
    Type1("  └─", "  ├─"),
    Type2("     └─", "     ├─"),
    Type3("  │  └─", "  │  ├─"),
    Type4("     ", "  │  ");

    fun get(isLast: Boolean): String = if (isLast) lastItem else normalItem
}
