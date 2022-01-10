package com.example.androidnavigatorleti.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
    val id: Int,
    val name: String,
    val surname: String,
    val birthDay: String,
) : Parcelable