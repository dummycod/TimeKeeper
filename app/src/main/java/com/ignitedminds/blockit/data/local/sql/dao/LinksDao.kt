package com.ignitedminds.blockit.data.local.sql.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ignitedminds.blockit.data.local.sql.entities.LinkData

@Dao
interface LinksDao {

    companion object{
        private val VIDEOS = "VIDEOS"
        private val ARTICLES = "ARTICLES"
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(linkData: LinkData)

    @Delete
    suspend fun delete(linkData: LinkData)

    @Update
    suspend fun update(linkData : LinkData)

    @Query("SELECT * from linksData")
    fun getAllLinks() : LiveData<List<LinkData>>

    @Query("SELECT * from linksData WHERE type = :videos")
    fun getAllVideos(videos : String = VIDEOS) : LiveData<List<LinkData>>

    @Query("SELECT * from linksData WHERE type = :articles")
    fun getAllArticles(articles : String = ARTICLES) : LiveData<List<LinkData>>

    @Query("SELECT * from linksData WHERE starred = 1 ")
    fun getFavouritesLinks() : LiveData<List<LinkData>>

    @Query("UPDATE linksData SET starred = 1 WHERE _id = :id")
    suspend fun addLinkToFavourites(id : Int)

    @Query("UPDATE linksData SET starred = 0 WHERE _id = :id")
    suspend fun removeLinkFromFavorites(id : Int)
}