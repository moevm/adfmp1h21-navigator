package com.example.androidnavigatorleti.data.room.tables

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidnavigatorleti.data.domain.UserInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class DatabaseUserInfo(
    @PrimaryKey(autoGenerate = true) var id: Int? = 0,
    @ColumnInfo(name = "name") var name: String? = "",
    @ColumnInfo(name = "surname") var surname: String? = "",
    @ColumnInfo(name = "birthday") var birthday: String? = ""
) : Parcelable {

    constructor(domainModel: UserInfo) : this(
        name = domainModel.name,
        surname = domainModel.surname,
        birthday = domainModel.birthday
    )

    fun mapToDomain() = UserInfo(
        name.orEmpty(),
        surname.orEmpty(),
        birthday.orEmpty()
    )
}