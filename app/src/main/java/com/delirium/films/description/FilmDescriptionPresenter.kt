package com.delirium.films.description

import com.delirium.films.films.CallbackFilm
import com.delirium.films.model.FilmInfo
import com.delirium.films.model.Model
import com.delirium.films.model.StatusCode

class FilmDescriptionPresenter : CallbackFilm {
    private val model = Model(null)

    init {
        model.getData()
    }

    fun setFilmInFavorite(name: String): Boolean? {
        var currentFilm: FilmInfo? = null

        model.getRequestData().forEach {
            if (it.localized_name == name) currentFilm = it
        }
        if (currentFilm!!.isFavorite) {
            currentFilm?.isFavorite = false
            model.deleteFilmInFavorite(currentFilm!!)
        } else {
            currentFilm?.isFavorite = true
            model.saveFilmInFavorite(currentFilm!!)
        }

        return currentFilm?.isFavorite
    }

    override fun successful() {
        TODO("Not yet implemented")
    }

    override fun failed(statusCode: StatusCode?) {
        TODO("Not yet implemented")
    }
}