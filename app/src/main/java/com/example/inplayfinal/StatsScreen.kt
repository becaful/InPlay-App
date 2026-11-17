package com.example.inplayfinal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
/*Skeleton only, for navigation completeness
would allow user to navigate between overall game stats
(number of pars, bogeys etc, leaked shots, handicap predictions etc etc
or toggle to bag statistics for things like dispersion, maybe identifying
when clubs have a common miss etc*/



@Composable fun StatsGameScreen(viewModel: InplayViewModel){
    //displays overall game statistics
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Text("Game Statistics", fontSize = 24.sp)
    }
}

@Composable fun StatsBagScreen(){
    //displays bag statistics like short/long/dispersion etc
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Text("Bag Statistics", fontSize = 24.sp)
    }
}