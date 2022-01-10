package com.example.androidnavigatorleti.data.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchHistoryItem(
    val place: String = ""
): Parcelable