package com.example.androidnavigatorleti.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParcelUserLocation(
    val lat: Double,
    val lng: Double
) : Parcelable