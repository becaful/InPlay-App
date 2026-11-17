package com.example.inplayfinal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@OptIn( ExperimentalMaterial3Api::class)
@Composable
fun InPlayTopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.ic_inplay_full),
                contentDescription = "InPlay App Logo",
                modifier = Modifier.height(56.dp)
            )
        }
    )
}

@Composable
fun InPlayNavigationBar(navController: NavController){
    val items = listOf(
        Rounds to R.drawable.ic_icon_rounds,
        Bag to R.drawable.ic_icon_bag,
        Play to R.drawable.ic_icon_play,
        Stats to  R.drawable.ic_icon_stats
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val current = backStackEntry?.destination

    NavigationBar {
        items.forEach { (route, iconItem) ->
            NavigationBarItem(
                selected = when (route) {
                    Rounds -> current?.hasRoute<Rounds>() == true
                    Bag -> current?.hasRoute<Bag>() == true
                    Play -> current?.hasRoute<Play>() == true
                    Stats -> current?.hasRoute<Stats>() == true
                    else -> false
                },
                onClick = {navController.navigate(route)},
                icon = {
                    Image(
                        painter = painterResource(iconItem),
                        contentDescription = null,
                        modifier = Modifier.height(36.dp)
                    )
                },

                label = null
            )
        }
    }
}
//routes
@Serializable object Rounds //home page, list of completed rounds
@Serializable object Bag
@Serializable object Play //starts playing a game
@Serializable object Stats

//Start Round
@Serializable data class ActiveRound(val roundId: Long)
@Serializable data class ViewRoundDetails(val roundId: Long) //view a single round details
@Serializable data class AddStroke(val roundId: Long, val hole: Int)


@Composable
fun AppUi(viewModel: InplayViewModel) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {InPlayTopAppBar()},
        bottomBar = {InPlayNavigationBar(navController)}
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rounds,
            modifier = Modifier.padding(innerPadding)
        ){
            //Completed Rounds Screen,acts as home screen
            composable<Rounds> {RoundsScreen(
                    onOpenRound = {roundId ->
                        navController.navigate(ViewRoundDetails(roundId))},
                        viewModel = viewModel
                )
            }
            composable<ViewRoundDetails> { entry ->
                val args = entry.toRoute<ViewRoundDetails>()
                RoundDetailsScreen(roundId = args.roundId, viewModel = viewModel)
            }

            //Bag Screen - skeleton only, no functions
            composable<Bag> { Text("Bag - Clubs")}

            //Play Screen -> Start playing a round of golf to track strokes
            composable<Play> { PlayGameScreen(
                onStartRound = {roundId ->
                    navController.navigate(ActiveRound(roundId))},
                    viewModel = viewModel
                    )
                }

            composable<ActiveRound> { entry ->
                val args = entry.toRoute<ActiveRound>()
                ActiveRoundScreen(
                    roundId = args.roundId,
                    viewModel = viewModel,
                    onAddStroke = { rid, h -> navController.navigate(AddStroke(rid, h)) }
                )
            }

            composable<AddStroke> { entry ->
                val args = entry.toRoute<AddStroke>()
                AddStrokeScreen(
                    onClose = { navController.popBackStack() },
                    onSave = { club, lie, distance, result, isPutt, penalties, shotType, miss ->
                        viewModel.viewModelScope.launch(Dispatchers.IO) {
                            viewModel.addStroke(
                                roundId = args.roundId,
                                hole = args.hole,
                                club = club,
                                lie = lie,
                                distance = distance,
                                result = result,
                                isPutt = isPutt,
                                penaltyStrokes = penalties,
                                shotType = shotType,
                                miss = miss
                            )
                        }
                        navController.popBackStack()
                    }
                )
            }

            //skeleton only, no functions
            composable<Stats> { StatsGameScreen(viewModel = viewModel)}

        }
    }
}

//used for hardwired List dropdowns
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDropdown(label: String, value: String, options: List<String>, onSelect: (String)->Unit){
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false })
            }
        }
    }
}





