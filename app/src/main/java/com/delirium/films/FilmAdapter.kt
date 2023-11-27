package com.delirium.films

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.delirium.films.databinding.FilmsItemBinding
import com.delirium.films.databinding.GenreItemBinding
import com.delirium.films.model.*
import com.squareup.picasso.Picasso
import java.util.*

class FilmAdapter(private val clickListener: ClickElement) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = mutableListOf<ModelAdapter>()

    class GenreViewHolder(var binding: GenreItemBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        private lateinit var clickElement: ClickElement

        fun bind(item: Genres, clickGenreSelect: ClickElement) {
            binding.genreFilm.text = item.genre.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            binding.genreFilm.isClickable = true
            binding.genreFilm.setOnClickListener(this)
            clickElement = clickGenreSelect
        }

        override fun onClick(p0: View?) {
            clickElement.onClickGenre(binding.genreFilm.text.toString().lowercase())
        }
    }

    class FilmViewHolder(private var binding: FilmsItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private lateinit var clickElement: ClickElement
        fun bind(item: FilmInfo, clickElementSelect: ClickElement) {

            Picasso.with(binding.imageFilm.context)
                .load(item.image_url)
                .placeholder(R.drawable.not_found)
                .into(binding.imageFilm)

            binding.nameFilm.text = item.localized_name
            binding.imageFilm.isClickable = true
            binding.imageFilm.setOnClickListener(this)

            if (item.isFavorite)
                binding.favoriteIndicator.setImageResource(R.drawable.ic_favorite_black_24dp)
            else binding.favoriteIndicator.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            binding.favoriteIndicator.isClickable = true
            binding.favoriteIndicator.setOnClickListener(this)

            clickElement = clickElementSelect
        }

        override fun onClick(p0: View?) {
            clickElement.onClickFilm(
                binding.nameFilm.text.toString(),
                binding.favoriteIndicator.id == p0?.id
            )
        }
    }

    class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleName: TextView = itemView.findViewById(R.id.title_name)

        fun bind(item: Titles) {
            titleName.text = item.titleBlock
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        GENRE_TYPE -> {
            val itemView = GenreItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            GenreViewHolder(itemView)
        }
        FILM_INFO_TYPE -> {
            val itemView = FilmsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            FilmViewHolder(itemView)
        }
        TITLE_TYPE -> {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.title_item, parent, false)
            TitleViewHolder(itemView)
        }
        else -> throw IllegalArgumentException()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        if (holder is GenreViewHolder && item is Genres) {
            holder.bind(item, clickListener)
            if (item.isSelected) {
                holder.binding.genreFilm.background = ContextCompat.getDrawable(
                    holder.binding.genreFilm.context,
                    R.drawable.genre_selected
                )
            } else {
                holder.binding.genreFilm.background = ContextCompat.getDrawable(
                    holder.binding.genreFilm.context,
                    R.drawable.genre
                )
            }
        } else if (holder is FilmViewHolder && item is Films) {
            holder.bind(item.film, clickListener)
        } else if (holder is TitleViewHolder && item is Titles) {
            holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int) = when (data[position]) {
        is Genres -> GENRE_TYPE
        is Films -> FILM_INFO_TYPE
        is Titles -> TITLE_TYPE
        else -> throw IllegalArgumentException()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    companion object {
        const val GENRE_TYPE = 0
        const val FILM_INFO_TYPE = 1
        const val TITLE_TYPE = 2
    }
}

interface ClickElement {
    fun onClickFilm(name: String, isFavorite: Boolean)
    fun onClickGenre(genre: String)
}