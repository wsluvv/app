package com.delirium.films.favorite

import androidx.lifecycle.ViewModel
import com.delirium.films.model.FilmInfo
import com.delirium.films.model.Model

class FavoritePresenter : ViewModel() {
    private var favoriteView: FavoriteFragment? = null
    private val model = Model(null)

    private var haveFilmsInFavorite: Boolean = false

    init {
        haveFilmsInFavorite = true
    }

    fun attachView(favoriteView: FavoriteFragment) {
        this.favoriteView = favoriteView
    }

    fun detachView(presenter: ViewModelFavoritePresenter) {
        favoriteView = null
        presenter.presenter = this
    }

    private fun changeStateView() = when (haveFilmsInFavorite) {
        false -> {
            favoriteView?.showFavoriteFilm(mutableListOf())
            favoriteView?.showNotFilms()
        }
        else -> getFilmInFavorite()
    }

    fun getFilmInFavorite() {
        val films = model.getAllFavorite() as MutableList<FilmInfo>
        haveFilmsInFavorite = films.isNotEmpty()
        favoriteView?.showFavoriteFilm(films)
    }

    fun goToDescriptionFilm(name: String) {
        val film = getFilmByName(name)!!
        favoriteView?.showFilmDescription(film)
    }

    fun deleteFromFavorite(name: String) {
        val film = getFilmByName(name)!!
        val filmsAfterDelete = model.deleteFilmInFavoriteList(film)
        haveFilmsInFavorite = filmsAfterDelete.isNotEmpty()
        changeStateView()
    }

    private fun getFilmByName(name: String): FilmInfo? {
        val allFilm = model.getAllFavorite()
        var currentFilm: FilmInfo? = null
        allFilm.forEach {
            if (it.localized_name == name)
                currentFilm = it
        }
        return currentFilm
    }
}