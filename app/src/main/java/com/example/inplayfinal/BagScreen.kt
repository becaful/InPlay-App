package com.example.inplayfinal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
/*Skeleton/stub only for navigation completeness
* When bag functionality wired, would allow user to add clubs to their bag and
* see club distance stats (avg, min max) separate to bag statistics*/




@Composable
fun BagScreen(){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Text("Bag Details will go here", fontSize = 24.sp)
        //list of clubs in the club database, can display average distance
    }
}