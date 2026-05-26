package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.TimeZone

class HydrationRepository(
    private val drinkDao: DrinkDao,
    val prefs: HydrationPrefs
) {
    val allRecords: Flow<List<DrinkRecord>> = drinkDao.getAllRecords()

    val todayRecords: Flow<List<DrinkRecord>> = allRecords.map { records ->
        val todayStart = getStartOfDay(System.currentTimeMillis())
        records.filter { it.timestamp >= todayStart }
    }

    val todayTotalMl: Flow<Int> = todayRecords.map { records ->
        records.sumOf { it.amountMl }
    }

    val streakStats: Flow<Int> = allRecords.map { records ->
        calculateStreak(records, prefs.dailyGoalMl)
    }

    suspend fun addDrink(amountMl: Int, beverageType: String) {
        drinkDao.insertRecord(DrinkRecord(amountMl = amountMl, beverageType = beverageType))
    }

    private fun getStartOfDay(timestamp: Long): Long {
        return Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun calculateStreak(records: List<DrinkRecord>, dailyGoal: Int): Int {
        if (records.isEmpty() || dailyGoal <= 0) return 0

        val groupedByDay = records.groupBy { getStartOfDay(it.timestamp) }
            .mapValues { entry -> entry.value.sumOf { it.amountMl } }

        val earliestTimestamp = records.minOfOrNull { it.timestamp } ?: return 0
        val earliestDay = getStartOfDay(earliestTimestamp)

        var currentStreak = 0
        var checkDay = getStartOfDay(System.currentTimeMillis())

        // Check if today was achieved
        val todayAchieved = (groupedByDay[checkDay] ?: 0) >= dailyGoal
        if (todayAchieved) {
            currentStreak++
        }
        
        checkDay -= 86400000L // Go to yesterday

        // Only traverse back to the earliest recorded day to avoid any possible infinite loop
        while (checkDay >= earliestDay) {
            if ((groupedByDay[checkDay] ?: 0) >= dailyGoal) {
                currentStreak++
                checkDay -= 86400000L
            } else {
                break
            }
        }
        
        return currentStreak
    }
}
