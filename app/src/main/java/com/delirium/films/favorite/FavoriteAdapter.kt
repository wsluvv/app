package com.delirium.films.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.delirium.films.R
import com.delirium.films.databinding.FilmsItemFavoriteBinding
import com.delirium.films.model.FilmInfo
import com.squareup.picasso.Picasso

class FavoriteAdapter(private val clickListener: ClickFavoriteFilm) :
    RecyclerView.Adapter<FavoriteAdapter.FilmViewHolder>() {

    var data = mutableListOf<FilmInfo>()

    class FilmViewHolder(private var binding: FilmsItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private lateinit var clickElement: ClickFavoriteFilm

        fun bind(item: FilmInfo, clickElementSelect: ClickFavoriteFilm) {

            Picasso.with(binding.imageFilm.context)
                .load(item.image_url)
                .placeholder(R.drawable.not_found)
                .into(binding.imageFilm)

            binding.nameFilm.text = item.localized_name
            binding.imageFilm.isClickable = true
            binding.imageFilm.setOnClickListener(this)

            if (item.isFavorite)
                binding.favoriteIndicator.setImageResource(R.drawable.ic_favorite_black_24dp)
            else
                binding.favoriteIndicator.setImageResource(R.drawable.ic_favorite_border_black_24dp)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val itemView = FilmsItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FilmViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, clickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

interface ClickFavoriteFilm {
    fun onClickFilm(name: String, isFavorite: Boolean)
}