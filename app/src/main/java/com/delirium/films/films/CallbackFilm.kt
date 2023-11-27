package com.delirium.films.films

import com.delirium.films.model.StatusCode

interface CallbackFilm {
    fun successful()
    fun failed(statusCode: StatusCode?)
}