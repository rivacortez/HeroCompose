package pe.edu.upc.superherocompose.presentation.Hero

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.edu.upc.superherocompose.common.Resource
import pe.edu.upc.superherocompose.common.UIState
import pe.edu.upc.superherocompose.data.repository.HeroRepository
import pe.edu.upc.superherocompose.domain.Hero

class HeroViewModel(private val repository: HeroRepository) : ViewModel() {
    private val _state = mutableStateOf(UIState<List<Hero>>())
    val state: State<UIState<List<Hero>>> get() = _state

    init {
        loadFavoriteHeroes()
    }

    private fun loadFavoriteHeroes() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = repository.getFavoriteHeroes()
            if (result is Resource.Success) {
                _state.value = UIState(data = result.data)
            } else {
                _state.value = UIState(message = result.message ?: "An error occurred")
            }
        }
    }

    fun toggleFavorite(hero: Hero) {
        hero.isFavorite = !hero.isFavorite
        viewModelScope.launch {
            if (hero.isFavorite) {
                repository.insertHero(hero)
            } else {
                repository.deleteHero(hero)
            }
            loadFavoriteHeroes()
        }
    }
}
