package com.example.androidnavigatorleti.data.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
data class DbUserLocation(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double
) {

    constructor(loc: LatLng): this(
        lat = loc.latitude,
        lng = loc.longitude
    )
}

fun DbUserLocation.toLatLng(): LatLng = LatLng(this.lat, this.lng)