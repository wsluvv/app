package com.delirium.films.model

import com.delirium.films.RealmConfiguration
import com.delirium.films.films.CallbackFilm
import com.delirium.films.films.FilmsPresenter
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class Model(val callback: CallbackFilm?) {
    private val filmRequest: FilmsRequest = SequeniaTestTaskSetting.filmsRequest
    private val configDB: RealmConfiguration = RealmConfiguration()

    private val realmDB: Realm = Realm.getInstance(configDB.getConfigDB())

    private var requestData: List<FilmInfo> = listOf()

    fun getData() {
        filmRequest.films().enqueue(object : Callback<FilmList> {
            override fun onFailure(call: Call<FilmList>, t: Throwable) {
                when (t) {
                    is SocketTimeoutException ->
                        callback?.failed(StatusCode.REQUEST_TIMEOUT)
                    is NumberFormatException ->
                        callback?.failed(StatusCode.CONFLICT_VALUE)
                    is UnknownHostException ->
                        callback?.failed(StatusCode.NOT_CONNECT)
                    else -> callback?.failed(StatusCode.SOME_ERROR)
                }
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<FilmList>,
                response: Response<FilmList>
            ) {
                if (response.isSuccessful) {
                    requestData = response.body()?.films as List<FilmInfo>
                    checkIsFavorite()
                    callback?.successful()
                } else {
                    callback?.failed(StatusCode.NOT_FOUND)
                }
            }
        })
    }

    fun getRequestData(): List<FilmInfo> {
        if (requestData.isNotEmpty()) checkIsFavorite()
        return requestData
    }

    private fun checkIsFavorite() {
        val data = converterFavoriteFilmToFilmInfo(
            realmDB.where(FavoriteFilm::class.java).findAll()
        ) as MutableList<FilmInfo>

        requestData.forEach {
            it.isFavorite = data.find { film ->
                film.name == it.name
            } != null
        }
    }

    fun saveFilmInFavorite(film: FilmInfo) {
        val filmDB = converterFilmInfoToFavoriteFilm(film)
        realmDB.beginTransaction()
        realmDB.copyToRealm(filmDB)
        realmDB.commitTransaction()
    }

    fun deleteFilmInFavorite(film: FilmInfo) {
        realmDB.beginTransaction()
        val removeObject = realmDB.where(FavoriteFilm::class.java)
            .equalTo("id", film.id)
            .findFirst()
        removeObject?.deleteFromRealm()
        realmDB.commitTransaction()
    }

    fun deleteFilmInFavoriteList(film: FilmInfo): List<FilmInfo> {
        deleteFilmInFavorite(film)
        return getAllFavorite()
    }

    fun getAllFavorite(): List<FilmInfo> {
        val data = realmDB.where(FavoriteFilm::class.java).findAll()

        return converterFavoriteFilmToFilmInfo(data)
    }

    private fun converterFilmInfoToFavoriteFilm(filmInfo: FilmInfo) = FavoriteFilm(
        id = filmInfo.id,
        localized_name = filmInfo.localized_name,
        name = filmInfo.name,
        year = filmInfo.year,
        rating = filmInfo.rating,
        image_url = filmInfo.image_url,
        description = filmInfo.description,
        genres = filmInfo.genres.joinToString(),
        isFavorite = filmInfo.isFavorite
    )

    private fun converterFavoriteFilmToFilmInfo(favoriteFilm: List<FavoriteFilm>)
            : List<FilmInfo> {
        return favoriteFilm.map {
            val genres = it.genres?.split(",") ?: listOf()
            genres.forEach { it.trim() }

            FilmInfo(
                id = it.id,
                localized_name = it.localized_name,
                name = it.name,
                year = it.year,
                rating = it.rating,
                image_url = it.image_url,
                description = it.description,
                genres = genres,
                isFavorite = it.isFavorite
            )
        }
    }
}