package com.example.inplayfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.inplayfinal.ui.theme.AppTheme

/*
Material theme built using my colours and https://material-foundation.github.io/material-theme-builder/
Blog used to adjust instead of using google fonts https://rijkswatch.blogspot.com/2024/07/using-material-themebuilder-generated.html

Full app skeleton is provided here to show the intended direction, but due to time constraint and size of complete idea,
I have focused on the base of my intended functionality - focusing on stroke tracking and round details
One course and one completed round is preseeded. Viewing details of completed rounds can be done,
a game can be started, add strokes on a hole or 2 , add a penalty, a 3 putt etc. Navigate to hole 9 and save round
the completed card can be looked at in Rounds (flag icon)

Some persistence needs addressing, like if you navigate away from an active round but functionality base
is there with the isDraft bool for strokes.

My aim is to create an app that provides more detail statistics for golf games than what is traditionally available
This includes:
-Tracking misses manually for dispersion/basic stats include

-Tracking shot type, so that shots that aren't full swings (chips, bump and runs) don't impact distance stats
on clubs such as irons etc. Further functionality could
-calculate leaked shots (3putts and penalties subtracted)

In future it would include being able to build your own bag of your clubs,
Add courses or use an API to pull course data
In depth overall game and bag statistics, including average leaked shots, hole scores compared to handicap
bag gapping, dispersion etc.
 */

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: InplayViewModel by viewModels { InPlayViewModelFactory(application) }
        setContent {
            AppTheme {
               AppUi(
                   viewModel
               )
                }
        }
    }
}



