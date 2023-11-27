package com.delirium.films.films

import com.delirium.films.model.FilmInfo
import com.delirium.films.model.Model
import com.delirium.films.model.StatusCode
import java.io.Serializable

class FilmsPresenter : CallbackFilm {
    private var filmView: FilmView? = null
    private var selectGenre: String? = null
    private val model = Model(this)

    private var loadingInProgress: Boolean = false
    private var dataReceived: Boolean = false
    private var gotError: Boolean = false

    init {
        loadingInProgress = true
        model.getData()
        changeStateView(null)
    }

    fun attachView(filmView: FilmView) {
        this.filmView = filmView
    }

    fun detachView(viewPresenter: ViewModelFilmsPresenter) {
        filmView = null
        viewPresenter.presenter = this
    }

    private fun changeStateView(statusCode: StatusCode?) = when {
        loadingInProgress -> filmView?.showProgressBar()
        dataReceived -> {
            filmView?.hideSnackBar()
            settingData()
        }
        gotError -> {
            filmView?.hideProgressBar()
            filmView?.snackBarWithError(statusCode)
        }
        else -> Unit
    }

    fun retryLoadDataOnError() {
        filmView?.showProgressBar()
        gotError = false
        loadingInProgress = true
        model.getData()
    }

    fun loadData(statusCode: StatusCode? = null) {
        if (model.getRequestData().isNotEmpty()) {
            filmView?.hideProgressBar()
            dataReceived = true
            loadingInProgress = false
            gotError = false
        }

        changeStateView(statusCode)
    }

    fun changeCurrentGenre(genre: String) {
        selectGenre = if (genre == selectGenre) {
            null
        } else {
            genre
        }
        settingData()
    }

    private fun settingData() {
        val receivedData = model.getRequestData()
        val genres = defineGenres(receivedData)
        val filterFilm = filmsFilterByGenre(receivedData)
        filmView?.showGenresAndFilms(genres, filterFilm, selectGenre)
    }

    private fun defineGenres(filmsInfo: List<FilmInfo>) =
        filmsInfo.flatMap { it.genres }.distinct().sorted()

    private fun filmsFilterByGenre(filmsInfo: List<FilmInfo>) = if (selectGenre != null) {
        filmsInfo.filter { film: FilmInfo -> film.genres.contains(selectGenre) }
    } else {
        filmsInfo
    }

    fun goToDescriptionFilm(name: String) {
        var currentFilm: FilmInfo? = null

        model.getRequestData().forEach {
            if (it.localized_name == name) currentFilm = it
        }
        currentFilm?.let { filmView?.showFilmDescription(it) }
    }

    /******* Callback *******/

    override fun successful() {
        loadData()
    }

    override fun failed(statusCode: StatusCode?) {
        gotError = true
        loadingInProgress = false
        loadData(statusCode)
    }

    /******** DB *******/

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

        settingData()
        return currentFilm?.isFavorite
    }
}