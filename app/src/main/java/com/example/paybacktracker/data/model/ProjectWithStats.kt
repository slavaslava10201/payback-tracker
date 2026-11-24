package com.example.paybacktracker.data.model

data class ProjectWithStats(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val totalExpense: Double,
    val totalIncome: Double
) {
    val balance: Double
        get() = totalIncome - totalExpense

    val isProfitable: Boolean
        get() = balance >= 0
}
