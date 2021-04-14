package com.example.androidnavigatorleti.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfo(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "surname") val surname: String = "",
    @ColumnInfo(name = "birthday") val birthday: String = ""
)