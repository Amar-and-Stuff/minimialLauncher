package com.example.minimiallauncher.service

import kotlin.text.iterator

fun fuzzySearchPriority(appName: String, query: String): Int {
    val lowerApp = appName.lowercase()
    val lowerQuery = query.lowercase()

    return when {
        lowerApp == lowerQuery -> 3 // Exact match
        lowerApp.contains(lowerQuery) -> 2 // Middle match
        isFuzzyMatch(lowerApp, lowerQuery) -> 1 // Loose fuzzy match
        else -> 0
    }
}

fun isFuzzyMatch(appName: String, query: String): Boolean {
    var i = 0
    for (char in appName) {
        if (i < query.length && char == query[i]) i++
    }
    return i == query.length
}

