package com.delirium.films.model

import io.realm.RealmObject

open class FavoriteFilm(
    var id: Int? = null,
    var localized_name: String? = null,
    var name: String? = null,
    var year: String? = null,
    var rating: String? = null,
    var image_url: String? = null,
    var description: String? = null,
    var genres: String? = null,
    var isFavorite: Boolean = false
) : RealmObject()