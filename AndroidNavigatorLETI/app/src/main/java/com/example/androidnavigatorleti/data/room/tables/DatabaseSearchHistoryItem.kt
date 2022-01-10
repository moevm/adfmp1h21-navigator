package com.example.androidnavigatorleti.data.room.tables

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidnavigatorleti.data.domain.SearchHistoryItem
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity
data class DatabaseSearchHistoryItem(
    @PrimaryKey(autoGenerate = true) var id: Int? = 0,
    @ColumnInfo(name = "place_name") val place: String? = ""
) : Parcelable {

    constructor(domainModel: SearchHistoryItem) : this(
        place = domainModel.place
    )

    fun mapToDomain() = SearchHistoryItem(place.orEmpty())
}