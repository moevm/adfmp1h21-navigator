package com.example.androidnavigatorleti.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
data class UserLocation(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double
)

fun UserLocation.toLatLng(): LatLng = LatLng(this.lat, this.lng)