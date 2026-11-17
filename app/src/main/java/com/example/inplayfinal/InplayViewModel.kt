package com.example.inplayfinal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inplayfinal.InplayDb.Companion.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class InPlayViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InplayViewModel(context) as T
    }
}

class InplayViewModel(context: Context) : ViewModel() {

    val ipDao: InplayDao
    init {
        val db = getDatabase(context)
        ipDao = db.inplayDao()
    }

    //courses
    val courses = ipDao.getCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getAvailableCourses() = ipDao.getCourses()

    fun teeBoxes(courseId: Long): Flow<List<String>> = ipDao.getTeesForCourse(courseId)

    fun holes(courseId: Long, teeBox: String): Flow<List<CourseHole>> = ipDao.getHoles(courseId,teeBox)


    suspend fun lastStrokeNo(roundId: Long, hole: Int) = ipDao.lastStrokeNumber(roundId, hole)

    fun strokesPerHole(roundId: Long, hole: Int) = ipDao.strokesPerHole(roundId, hole)

    suspend fun addStroke(
        roundId: Long,
        hole: Int,
        club: String,
        lie: String,
        distance: Int,
        result: String,
        isPutt: Boolean,
        penaltyStrokes: Int = 0,
        shotType: String,
        miss: String? = null
    ){
        val next = (ipDao.lastStrokeNumber(roundId,hole) ?: 0) + 1
        viewModelScope.launch(Dispatchers.IO) {
            ipDao.insertStroke(
                Stroke(
                    roundId = roundId,
                    holeNumber = hole,
                    strokeNo = next,
                    club = club,
                    lie = lie,
                    distance = distance,
                    result = result,
                    isPutt = isPutt,
                    penaltyStrokes = penaltyStrokes,
                    shotType = shotType,
                    miss = miss
                )
            )
        }
    }

    suspend fun createDraftRound(courseId: Long, teeBox: String, holes: Int): Long {
            val round = Round(
                courseId = courseId,
                teeBox = teeBox,
                holes = holes,
                isDraft = true,
                datePlayed = System.currentTimeMillis()
            )
            return ipDao.insertRound(round)
    }

    // Round summaries for cards
    fun completedRounds() = ipDao.completedRoundSummaries()

    fun completeRound(roundId: Long) = viewModelScope.launch{
        ipDao.completeRound(roundId)
    }

    // Whole-round strokes (for details calculations)
    fun strokesByRound(roundId: Long) = ipDao.strokesByRound(roundId)

    fun roundHeader(roundId: Long): Flow<InplayDao.RoundHeader?> =
        ipDao.roundHeader(roundId)

    fun courseIdFromRound(roundId: Long): Flow<Long> = ipDao.getCourseIdFromRound(roundId)

    fun teeboxFromRound(roundId: Long): Flow<String> = ipDao.getTeeboxFromRound(roundId)


    //functions for if add course functionality was wired
    /*fun addCourse(name: String, holes: Int) = viewModelScope.launch(Dispatchers.IO){
        ipDao.insertCourse(Course(name = name, holes = holes))
    }
    fun addHole(courseId: Long, teeBox: String, holeNumber: Int, par: Int, yardage: Int)=
        viewModelScope.launch(Dispatchers.IO){
            ipDao.insertHole(
                CourseHole(
                    id = 0,
                    courseId = courseId,
                    teeBox = teeBox,
                    holeNumber = holeNumber,
                    par = par,
                    yardage = yardage
                )
            )
        }*/
}