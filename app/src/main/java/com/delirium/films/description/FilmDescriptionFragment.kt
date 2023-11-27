package com.delirium.films.description

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.delirium.films.R
import com.delirium.films.databinding.FragmentFilmDescriptionBinding
import com.squareup.picasso.Picasso

class FilmDescriptionFragment : Fragment() {
    private var _viewBinding: FragmentFilmDescriptionBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val args by navArgs<FilmDescriptionFragmentArgs>()
    private val film by lazy { args.selectedFilm }
    private var presenter = FilmDescriptionPresenter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentFilmDescriptionBinding.inflate(inflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.with(viewBinding.imageFilmDesc.context)
            .load(film.image_url)
            .placeholder(R.drawable.not_found)
            .into(viewBinding.imageFilmDesc)

        viewBinding.originName.text = film.localized_name

        val genresWithYear = joinGenreWithYear(film.genres, film.year)
        viewBinding.genresWithYear.text = getString(R.string.genres_with_year, genresWithYear)

        val rating: String = transformRatingToString(film.rating)
        viewBinding.rating.text = getString(R.string.rating, rating)

        viewBinding.descriptionFilm.text = film.description
            ?: getString(R.string.without_description)

        setIconFavorite()

        viewBinding.favoriteIndicator.setOnClickListener {
            setFilmInFavorite()
        }
    }

    private fun setFilmInFavorite() {
        film.isFavorite = presenter.setFilmInFavorite(film.localized_name!!) == true
        setIconFavorite()
    }

    private fun setIconFavorite() {
        if (film.isFavorite)
            viewBinding.favoriteIndicator.setImageResource(R.drawable.ic_favorite_black_24dp)
        else
            viewBinding.favoriteIndicator.setImageResource(R.drawable.ic_favorite_border_black_24dp)
    }

    private fun joinGenreWithYear(genres: List<String>, year: String?): String {
        val genreWithYear: MutableList<String> = mutableListOf()
        genreWithYear.addAll(genres)

        year?.let {
            genreWithYear.add(it + " " + getString(R.string.year))
        }
        return genreWithYear.joinToString()
    }

    private fun transformRatingToString(rating: String?) = rating?.let {
        String.format("%.1f", it.toDouble()).replace(",", ".")
    } ?: getString(R.string.rating_without_value)

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}