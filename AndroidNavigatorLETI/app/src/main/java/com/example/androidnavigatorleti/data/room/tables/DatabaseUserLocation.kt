package com.example.androidnavigatorleti.data.room.tables

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidnavigatorleti.data.domain.UserLocation
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class DatabaseUserLocation(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "lat") var lat: Double? = 0.0,
    @ColumnInfo(name = "lng") var lng: Double? = 0.0
): Parcelable {

    constructor(domainModel: UserLocation) : this(
        lat = domainModel.lat,
        lng = domainModel.lng
    )

    fun mapToDomain() = UserLocation(
        lat ?: 0.0,
        lat ?: 0.0
    )
}