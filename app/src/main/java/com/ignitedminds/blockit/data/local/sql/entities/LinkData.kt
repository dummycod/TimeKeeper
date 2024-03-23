package com.ignitedminds.blockit.data.local.sql.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "linksData", indices = [Index(value = ["url"], unique = true)])
data class LinkData(
    val url: String,
    val type: String,
    val imageUrl: String,
    val title: String,
    val description: String,
    val starred: Int
) {
    @PrimaryKey(autoGenerate = true)
    var _id: Int = 0
}