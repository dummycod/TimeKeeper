package com.ignitedminds.blockit.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ignitedminds.blockit.data.local.sql.database.LinksDatabase
import com.ignitedminds.blockit.data.local.sql.entities.LinkData
import com.ignitedminds.blockit.data.repository.LinkDataRepository
import kotlinx.coroutines.launch

class LinkDataViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LinkDataRepository
    val allLinks: LiveData<List<LinkData>>
    val starredLinks: LiveData<List<LinkData>>
    val articleLinks: LiveData<List<LinkData>>
    val videoLinks: LiveData<List<LinkData>>

    init {
        val dao = LinksDatabase.getDatabase(application).getLinksDao()
        repository = LinkDataRepository(dao)
        allLinks = dao.getAllLinks()
        starredLinks = dao.getFavouritesLinks()
        articleLinks = dao.getAllArticles()
        videoLinks = dao.getAllVideos()
    }

    fun insertLink(linkData: LinkData, showDialog: (message: String) -> Unit) =
        viewModelScope.launch {
            try {
                repository.insertLink(linkData)
                showDialog("Todo Inserted!")
            } catch (e: Exception) {
                showDialog("URL is already present.")
            }
        }

    fun updateLink(linkData: LinkData) = viewModelScope.launch {
        repository.update(linkData)
    }

    fun deleteLink(linkData: LinkData) = viewModelScope.launch {
        repository.deleteLink(linkData)
    }

    fun addLinkToFavorites(id: Int) = viewModelScope.launch { repository.addLinkToFavourites(id) }

    fun removeLinkFromFavorites(id: Int) =
        viewModelScope.launch { repository.removeLinkFromFavorites(id) }
}