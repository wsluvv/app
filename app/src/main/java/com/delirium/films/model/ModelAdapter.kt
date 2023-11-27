package com.delirium.films.model

open class ModelAdapter

class Genres(val genre: String, val isSelected: Boolean = false) : ModelAdapter()
class Films(val film: FilmInfo) : ModelAdapter()
class Titles(val titleBlock: String) : ModelAdapter()