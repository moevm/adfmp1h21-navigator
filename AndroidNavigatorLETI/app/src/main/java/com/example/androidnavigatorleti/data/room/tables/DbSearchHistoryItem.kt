package com.example.androidnavigatorleti.data.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidnavigatorleti.domain.model.SearchHistoryItem

@Entity
data class DbSearchHistoryItem(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "place_name") val place: String = ""
) {

    constructor(item: SearchHistoryItem): this(
        id = item.id,
        place = item.place
    )

    fun mapToDomain() = SearchHistoryItem(
        id,
        place
    )
}