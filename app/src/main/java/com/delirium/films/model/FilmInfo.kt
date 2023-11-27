package com.delirium.films.model

import java.io.Serializable

data class FilmInfo(
    var id: Int?,
    var localized_name: String?,
    var name: String?,
    var year: String?,
    var rating: String?,
    var image_url: String?,
    var description: String?,
    var genres: List<String>,
    var isFavorite: Boolean = false
) : Serializable
