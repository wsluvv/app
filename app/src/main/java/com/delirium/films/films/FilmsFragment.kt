package com.delirium.films.films

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delirium.films.ClickElement
import com.delirium.films.FilmAdapter
import com.delirium.films.R
import com.delirium.films.databinding.FragmentFilmsBinding
import com.delirium.films.model.*
import com.google.android.material.snackbar.Snackbar

class FilmsFragment : Fragment(), FilmView, ClickElement {
    private var _adapter: FilmAdapter? = null
    private val adapter get() = _adapter!!
    private var recyclerView: RecyclerView? = null

    private lateinit var gridManager: GridLayoutManager

    private var _filmsBinding: FragmentFilmsBinding? = null
    private val filmsBinding get() = _filmsBinding!!

    private var snackBar: Snackbar? = null
    private val presenterViewModel: ViewModelFilmsPresenter by activityViewModels()
    private lateinit var filmsPresenter: FilmsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _filmsBinding = FragmentFilmsBinding.inflate(inflater, container, false)
        gridManager = GridLayoutManager(activity, 2)
        recyclerView = filmsBinding.recycler
        recyclerView?.layoutManager = gridManager

        return filmsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filmsPresenter = presenterViewModel.presenter ?: FilmsPresenter()
        filmsPresenter.attachView(this)

        _adapter = FilmAdapter(this)
        recyclerView?.adapter = adapter
        filmsPresenter.loadData()

        gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    FilmAdapter.GENRE_TYPE -> 2
                    FilmAdapter.FILM_INFO_TYPE -> 1
                    FilmAdapter.TITLE_TYPE -> 2
                    else -> throw IllegalArgumentException()
                }
            }
        }

        setFragmentResultListener("SHOW_FILM") { key, bundle ->
            val filmName: String = bundle.getString("filmName")!!
            filmsPresenter.goToDescriptionFilm(filmName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        filmsPresenter.detachView(presenterViewModel)
        _filmsBinding = null
        recyclerView = null
        _adapter = null
    }

    override fun showGenresAndFilms(
        genres: List<String>,
        films: List<FilmInfo>,
        selectGenre: String?
    ) {
        val dataToShow = setDataByFilmsAndGenres(genres, films, selectGenre)
        adapter.data = dataToShow
        adapter.notifyDataSetChanged()
    }

    private fun setDataByFilmsAndGenres(
        genres: List<String>,
        filmsInfo: List<FilmInfo>,
        selectGenre: String?
    ): MutableList<ModelAdapter> {
        val dataSet = mutableListOf<ModelAdapter>()

        dataSet.add(Titles(getString(R.string.genre_title)))
        genres.forEach { dataSet.add(Genres(genre = it, isSelected = it == selectGenre)) }
        dataSet.add(Titles(getString(R.string.film_title)))

        filmsInfo.forEach { dataSet.add(Films(film = it)) }
        return dataSet
    }

    override fun showFilmDescription(film: FilmInfo) {
        filmsBinding.root.findNavController().navigate(
            //TODO изменить установку заголовка
            FilmsFragmentDirections.actionFilmsFragmentToFilmDescription(
                film,
                film.name ?: getString(R.string.no_title)
            )
        )
    }

    override fun onClickFilm(name: String, isFavorite: Boolean) {
        if (isFavorite) {
            filmsPresenter.setFilmInFavorite(name)
        } else {
            filmsPresenter.goToDescriptionFilm(name)
        }
    }

    override fun onClickGenre(genre: String) {
        filmsPresenter.changeCurrentGenre(genre)
    }

    override fun showProgressBar() {
        filmsBinding.progressBar.visibility = ProgressBar.VISIBLE
    }

    override fun hideProgressBar() {
        filmsBinding.progressBar.visibility = ProgressBar.INVISIBLE
    }

    override fun snackBarWithError(statusCode: StatusCode?) {
        val textError = when (statusCode) {
            StatusCode.NOT_FOUND -> R.string.server_error
            StatusCode.REQUEST_TIMEOUT -> R.string.request_timeout_error
            StatusCode.CONFLICT_VALUE -> R.string.conflict_value_error
            StatusCode.NOT_CONNECT -> R.string.data_not_load
            else -> R.string.unknown_error
        }

        snackBar = Snackbar
            .make(filmsBinding.recycler, getString(textError), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry_on_error) {
                filmsPresenter.retryLoadDataOnError()
            }
        snackBar?.show()
    }

    override fun hideSnackBar() {
        snackBar?.let {
            if (it.isShown) it.dismiss()
        }
    }
}