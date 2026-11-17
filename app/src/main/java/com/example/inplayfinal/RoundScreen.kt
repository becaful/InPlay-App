package com.example.inplayfinal


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundsScreen(
    onOpenRound: (Long) -> Unit,
    viewModel: InplayViewModel
) {
    val rounds = viewModel.completedRounds().collectAsState(emptyList()).value
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (rounds.isEmpty()) {
            Column(
                Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) { Text("No completed rounds yet.") }
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(rounds.size) { i ->
                val r = rounds[i]
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { onOpenRound(r.id)},
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(r.courseName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${r.holes} holes\nGross ${r.gross}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            java.text.SimpleDateFormat("dd MMM yyyy")
                                .format(java.util.Date(r.datePlayed)),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun RoundDetailsScreen(roundId: Long, viewModel: InplayViewModel) {
    val header = viewModel.roundHeader(roundId).collectAsState(null).value
    val strokes = viewModel.strokesByRound(roundId).collectAsState(emptyList()).value
    val courseId = viewModel.courseIdFromRound(roundId).collectAsState(-1L).value
    val tee = viewModel.teeboxFromRound(roundId).collectAsState("").value
    val holes = viewModel.holes(courseId, tee).collectAsState(emptyList()).value

    // Simple stats (FIR, tee shot hits fairway, GIR ball is on green in par minus 2,
    val putts = strokes.count { it.isPutt }
    val gross = strokes.size
    val fir = holes.filter { it.par != 3 }.count { h ->
        strokes.firstOrNull { it.holeNumber == h.holeNumber && it.strokeNo == 1 }?.result == "Fairway"
    }
    val gir = holes.count { h ->
        val needBy = h.par - 2
        strokes.filter { it.holeNumber == h.holeNumber }
            .any { it.result == "Green" && it.strokeNo <= needBy }
    }
    val penaltiesTotal = strokes.sumOf { it.penaltyStrokes }
    val leakedPutts = holes.sumOf { h->
        val holePutts = strokes.count { it.holeNumber == h.holeNumber && it.isPutt}
        if (holePutts > 3) holePutts - 3
        else 0
    }
    val leakedShots = penaltiesTotal + leakedPutts

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item{Text(header?.let { "${it.courseName} - ${it.teeBox}\n${it.holes} holes" } ?: "",
            fontSize = 20.sp)}
        item{Text("Gross: $gross   Putts: $putts   FIR: $fir   GIR: $gir")}
        item{Text("Leaked shots: $leakedShots")}

        items(holes.size) { i ->
            val h = holes[i]
            val hs = strokes.filter { it.holeNumber == h.holeNumber }.sortedBy { it.strokeNo }
            val holePutts = hs.count { it.isPutt }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Hole ${h.holeNumber}  Par ${h.par}\n${h.yardage} yds")
                    if (hs.isEmpty()) Text("— no strokes")
                    hs.forEach { s ->
                        Text(
                            "${s.strokeNo}. ${s.club} , ${s.lie} , ${s.distance}yd , ${s.result}" +
                                    (s.miss?.let { " , Miss: $it" } ?: ""))
                    }
                    Text("Putts: $holePutts")
                }
            }
        }
    }
}




