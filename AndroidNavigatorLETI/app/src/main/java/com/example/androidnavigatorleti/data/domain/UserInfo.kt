package com.example.androidnavigatorleti.data.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
    val name: String = "",
    val surname: String = "",
    val birthday: String = ""
) : Parcelable