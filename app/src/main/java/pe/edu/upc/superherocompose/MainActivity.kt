package pe.edu.upc.superherocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import pe.edu.upc.superherocompose.common.Constants
import pe.edu.upc.superherocompose.data.local.AppDatabase
import pe.edu.upc.superherocompose.data.remote.HeroService
import pe.edu.upc.superherocompose.data.repository.HeroRepository
import pe.edu.upc.superherocompose.domain.Hero
import pe.edu.upc.superherocompose.presentation.Hero.HeroScreen
import pe.edu.upc.superherocompose.presentation.Hero.HeroViewModel
import pe.edu.upc.superherocompose.presentation.HeroListScreen
import pe.edu.upc.superherocompose.presentation.HeroListViewModel
import pe.edu.upc.superherocompose.ui.theme.SuperHeroComposeTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HeroService::class.java)

        val dao = Room
            .databaseBuilder(applicationContext, AppDatabase::class.java, "heroes-db")
            .build()
            .getHeroDao()

        val repository = HeroRepository(service, dao)

        enableEdgeToEdge()

        setContent {
            SuperHeroComposeTheme {
                val items = listOf(
                    ItemTab(
                        "Search",
                        Icons.Filled.Search
                    ),
                    ItemTab(
                        "Favorites",
                        Icons.Filled.Favorite
                    )
                )

                val index = remember {
                    mutableStateOf(0)
                }

                Scaffold { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {
                        LazyRow {
                            itemsIndexed(items) { position, item ->
                                FilterChip(
                                    selected = index.value == position,
                                    leadingIcon = {
                                        Icon(item.icon, contentDescription = item.name)
                                    },
                                    label = {
                                        Text(item.name)
                                    },
                                    onClick = {
                                        index.value = position
                                    }
                                )
                            }
                        }

                        if (index.value == 0) {
                            val heroListViewModel = HeroListViewModel(repository)
                            HeroListScreen(heroListViewModel)
                        } else {
                            val heroViewModel = HeroViewModel(repository)
                            HeroScreen(heroViewModel)
                        }
                    }
                }
            }
        }
    }
}

data class ItemTab(
    val name: String,
    val icon: ImageVector
)
