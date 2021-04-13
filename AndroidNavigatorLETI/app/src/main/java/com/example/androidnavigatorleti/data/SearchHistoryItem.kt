package com.example.androidnavigatorleti.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class SearchHistoryItem(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "place") val place: String = ""
)