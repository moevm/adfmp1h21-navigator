package com.example.androidnavigatorleti.data.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLocation(
    val lat: Double = 0.0,
    val lng: Double = 0.0
) : Parcelable