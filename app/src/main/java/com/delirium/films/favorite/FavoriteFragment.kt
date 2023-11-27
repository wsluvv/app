package com.delirium.films.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delirium.films.databinding.FragmentFavoriteBinding
import com.delirium.films.model.FilmInfo

class FavoriteFragment : Fragment(), ClickFavoriteFilm {
    private var _viewBinding: FragmentFavoriteBinding? = null
    private val viewBinding get() = _viewBinding!!
    private var recyclerView: RecyclerView? = null

    private lateinit var gridManager: GridLayoutManager

    private var _adapter: FavoriteAdapter? = null
    private val adapter get() = _adapter!!

    private val presenterViewModel: ViewModelFavoritePresenter by activityViewModels()
    private lateinit var presenter: FavoritePresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentFavoriteBinding.inflate(inflater, container, false)

        gridManager = GridLayoutManager(activity, 2)
        recyclerView = viewBinding.recyclerFavorite
        recyclerView?.layoutManager = gridManager
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _adapter = FavoriteAdapter(this)
        presenter = presenterViewModel.presenter ?: FavoritePresenter()
        presenter.attachView(this)
        presenter.getFilmInFavorite()
        recyclerView?.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewBinding.goToFilms.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
        presenter.detachView(presenterViewModel)
    }

    fun showFavoriteFilm(films: MutableList<FilmInfo>) {
        if (films.isNotEmpty()) {
            viewBinding.textWithoutFilms.visibility = View.INVISIBLE
            viewBinding.goToFilms.visibility = View.INVISIBLE
        }
        adapter.data = films
        adapter.notifyDataSetChanged()
    }

    fun showNotFilms() {
        viewBinding.textWithoutFilms.visibility = View.VISIBLE
        viewBinding.goToFilms.visibility = View.VISIBLE
    }

    fun showFilmDescription(film: FilmInfo) {
        parentFragmentManager.setFragmentResult(
            "SHOW_FILM", bundleOf("filmName" to film.localized_name)
        )
        findNavController().popBackStack()
    }

    override fun onClickFilm(name: String, isFavorite: Boolean) {
        if (isFavorite) {
            presenter.deleteFromFavorite(name)
        } else {
            presenter.goToDescriptionFilm(name)
        }
    }
}