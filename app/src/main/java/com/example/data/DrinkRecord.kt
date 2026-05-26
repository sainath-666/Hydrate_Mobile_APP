package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drink_records")
data class DrinkRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amountMl: Int,
    val beverageType: String,
    val timestamp: Long = System.currentTimeMillis()
)
