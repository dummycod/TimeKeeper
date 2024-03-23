package com.ignitedminds.blockit.data.repository

import com.ignitedminds.blockit.data.local.sql.dao.LinksDao
import com.ignitedminds.blockit.data.local.sql.entities.LinkData

class LinkDataRepository(private val linksDao: LinksDao) {

    val allLinks = linksDao.getAllLinks()
    val starredLinks = linksDao.getFavouritesLinks()
    val articleLinks = linksDao.getAllArticles()
    val videoLinks = linksDao.getAllVideos()

    suspend fun insertLink(linkData: LinkData) {
        linksDao.insert(linkData)
    }

    suspend fun deleteLink(linkData: LinkData) {
        linksDao.delete(linkData)
    }

    suspend fun update(linkData: LinkData) {
        linksDao.update(linkData)
    }

    suspend fun addLinkToFavourites(id: Int) {
        linksDao.addLinkToFavourites(id)
    }

    suspend fun removeLinkFromFavorites(id: Int) {
        linksDao.removeLinkFromFavorites(id)
    }
}