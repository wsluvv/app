package com.delirium.films

import io.realm.RealmConfiguration

class RealmConfiguration {
    private val config: RealmConfiguration =
        RealmConfiguration.Builder()
            .name("favoriteFilm.realm")
            .schemaVersion(1)
            .build()

    fun getConfigDB() = config
}