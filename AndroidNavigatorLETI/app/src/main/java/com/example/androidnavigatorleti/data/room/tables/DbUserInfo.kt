package com.example.androidnavigatorleti.data.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidnavigatorleti.domain.model.UserInfo

@Entity
data class DbUserInfo(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "surname") val surname: String = "",
    @ColumnInfo(name = "birthday") val birthday: String = ""
) {

    constructor(info: UserInfo): this(
        id = info.id,
        name = info.name,
        surname = info.surname,
        birthday = info.birthDay
    )

    fun mapToDomain() = UserInfo(
        id,
        name,
        surname,
        birthday
    )
}