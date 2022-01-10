package com.example.androidnavigatorleti.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParcelUserLocation(
    val lat: Double,
    val lng: Double
) : Parcelable