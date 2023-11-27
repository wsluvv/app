package com.delirium.films.films

import com.delirium.films.model.FilmInfo
import com.delirium.films.model.StatusCode

interface FilmView {
    fun showGenresAndFilms(
        genres: List<String>,
        films: List<FilmInfo>,
        selectGenre: String?
    )

    fun showFilmDescription(film: FilmInfo)
    fun showProgressBar()
    fun hideProgressBar()
    fun snackBarWithError(statusCode: StatusCode?)
    fun hideSnackBar()
}