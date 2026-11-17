package com.example.inplayfinal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayGameScreen(
    onStartRound: (Long) -> Unit,
    viewModel: InplayViewModel
){
    val scope = rememberCoroutineScope()
//use exposedDropDownMenu to show Courses, once picked, tees, then play. Default to 9 holes for purpose of MVP
    val courses = viewModel.getAvailableCourses().collectAsState(emptyList()).value
    var courseIndex by remember { mutableIntStateOf(-1) }
    val course = courses.getOrNull(courseIndex)

    val tees = if (course != null)
        viewModel.teeBoxes(course.id).collectAsState(emptyList()).value
    else emptyList()
    var teeIndex by remember { mutableIntStateOf(-1) }
    val holes by remember { mutableIntStateOf(9) }

    var expandCourse by remember { mutableStateOf(false) }
    var expandTee by remember { mutableStateOf(false) }
    Column(Modifier.padding(16.dp)){

        //Course
        ExposedDropdownMenuBox(
            expanded = expandCourse,
            onExpandedChange =  { expandCourse = !expandCourse}
        ) {
            TextField(
                value = course?.name ?: "Select Course",
                onValueChange = {},
                readOnly = true,
                label = { Text("Course")},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandCourse)},
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandCourse,
                onDismissRequest = {expandCourse = !expandCourse}
            ) {
                courses.forEachIndexed { i,c ->
                    DropdownMenuItem(
                        text = {Text(c.name)},
                        onClick = {
                            courseIndex = i
                            teeIndex = -1
                            expandCourse = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        //Tee
        ExposedDropdownMenuBox(
            expanded = expandTee,
            onExpandedChange =  { if (course != null) expandTee = true}
        ) {
            TextField(
                value = tees.getOrNull(teeIndex) ?: "Select Tee",
                onValueChange = {},
                readOnly = true,
                enabled = course != null,
                label = { Text("Tee")},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandTee)},
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandTee,
                onDismissRequest = {expandTee = !expandTee}
            ) {
                tees.forEachIndexed { i,tee ->
                    DropdownMenuItem(
                        text = { Text(tee) },
                        onClick = {
                            teeIndex = i
                            expandTee = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        val startGame = course !=  null && teeIndex in tees.indices
        TextButton(
            enabled = startGame,
            onClick = {
                val courseId = course!!.id
                val tee = tees[teeIndex]
                scope.launch {
                    val roundId = viewModel.createDraftRound(courseId, tee, holes)
                    onStartRound(roundId)
                }
            }
        ) {
            Text("Start Game")
        }
    }
}

@Composable
fun ActiveRoundScreen(
    roundId: Long,
    viewModel: InplayViewModel,
    onAddStroke: (Long, Int) -> Unit
){
    val header = viewModel.roundHeader(roundId).collectAsState(null).value
    val holesTotal = header?.holes ?: 9
    var hole by rememberSaveable(roundId) { mutableIntStateOf(1) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Text(
            text = header?.let { "${it.courseName}\n${it.teeBox}\n${it.holes} holes" } ?: "Loading…",
            fontSize = 22.sp
        )
        Spacer(Modifier.height(8.dp))

        // Pager-ish top bar
        Box(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            TextButton(
                enabled = hole > 1,
                onClick = { hole-- },
                modifier = Modifier.align(Alignment.CenterStart)
            ) { Text("Prev") }

            Text("Hole $hole / $holesTotal", fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))

            TextButton(
                enabled = hole < holesTotal,
                onClick = { hole++ },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) { Text("Next") }
        }

        // Hole details + strokes
        Spacer(Modifier.height(8.dp))
        HoleScreen(roundId = roundId, hole = hole, viewModel = viewModel)

        // Floating-ish Add (kept inline for speed)
        Spacer(Modifier.height(16.dp))
        TextButton(onClick =  { onAddStroke(roundId, hole) }){ Text("+ Add Stroke")}

        if (hole == holesTotal) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = {
                viewModel.completeRound(roundId)
            }) {Text("Finish Round") }
        }
    }
}

@Composable
fun HoleScreen(
    roundId: Long,
    hole: Int,
    viewModel: InplayViewModel
){
    val courseId = viewModel.courseIdFromRound(roundId).collectAsState(-1L).value
    val tee = viewModel.teeboxFromRound(roundId).collectAsState("").value
    val holeInfo = viewModel.holes(courseId, tee).collectAsState(emptyList()).value
        .firstOrNull { it.holeNumber == hole }

    val strokes = viewModel.strokesPerHole(roundId, hole).collectAsState(emptyList()).value

    Column(Modifier.fillMaxWidth()) {
        Text(
            "Par ${holeInfo?.par ?: "-"}\n${holeInfo?.yardage ?: "-"} yds",
            fontSize = 16.sp
        )
        Spacer(Modifier.height(8.dp))

        if (strokes.isEmpty()) {
            Text("No strokes yet.")
        } else {
            strokes.forEach { s ->
                Text("${s.strokeNo}. ${s.club} - ${s.lie} - ${s.distance}y - ${s.result}" +
                        (s.miss?.let { " - Miss: $it" } ?: ""))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStrokeScreen(
    onClose: () -> Unit,
    onSave: (club: String, lie: String, distance: Int, result: String, isPutt: Boolean, penalties: Int, shotType: String, miss: String?) -> Unit
) {
    // simple lists (can promote to one place later)
    val clubs = listOf("Driver","3W","5W","4H","5H","6i","7i","8i","9i","PW","52°","58°","Putter")
    val lies = listOf("Tee","Fairway","Rough","Sand","Green")
    val results = listOf("Fairway","Rough","Sand","Green","Penalty","Hole")
    val shotTypes = listOf("Full","Recovery","Chip","Bump & Run","Putt")
    val misses = listOf("None","Left","Right","Short","Long")

    var club by remember { mutableStateOf(clubs.first()) }
    var lie by remember { mutableStateOf(lies.first()) }
    var distance by remember { mutableStateOf("0") }
    var result by remember { mutableStateOf(results.first()) }
    var isPutt by remember { mutableStateOf(false) }
    var penalties by remember { mutableStateOf("0") }
    var shotType by remember { mutableStateOf(shotTypes.first()) }
    var miss by remember { mutableStateOf(misses.first()) }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Stroke") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        onSave(
                            club,
                            lie,
                            distance.toIntOrNull() ?: 0,
                            result,
                            isPutt,
                            penalties.toIntOrNull() ?: 0,
                            shotType,
                            miss.takeUnless { it == "None" }
                        )
                    }) { Text("Save") }
                }
            )
        }
    ) { inner ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = inner,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {SimpleDropdown("Club", club, clubs) { club = it }}
            item {SimpleDropdown("Lie", lie, lies) { lie = it }}
            item {OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                label = { Text("Distance (y)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
                )
            }
            item {SimpleDropdown("Result", result, results) { result = it }}
            item {SimpleDropdown("Shot Type", shotType, shotTypes) { shotType = it }}
            item {SimpleDropdown("Miss", miss, misses) { miss = it }}
            item {Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isPutt, onCheckedChange = { isPutt = it })
                Spacer(Modifier.width(8.dp)); Text("Putt")
            }}
            item {OutlinedTextField(
                value = penalties,
                onValueChange = { penalties = it },
                label = { Text("Penalty Strokes") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )}
        }
    }
}


/*
Stub for future ability to add course to the database.
@Composable
fun AddCourseScreen(){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        //Text Box to enter course name
        //Drop down to select 9 or 18 holes
        //Textbox to enter TeeBox
        //Populate rows 1..9 or 1..18 with teeBox prepopulated, holeNum prepopulated and entry fields for Par and Yardage
        //add button ->
    }
}
*/

