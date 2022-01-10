package com.example.androidnavigatorleti.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchHistoryItem(
    val id: Int,
    val place: String
) : Parcelable