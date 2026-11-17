package com.example.inplayfinal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [
        Course::class,
        CourseHole::class, Round::class,
                     Stroke::class], version = 4)
abstract class InplayDb : RoomDatabase() {
    abstract fun inplayDao(): InplayDao

    companion object{
        @Volatile private var INSTANCE: InplayDb? = null

        fun getDatabase(context: Context): InplayDb =
            INSTANCE ?: synchronized(this){
                Room.databaseBuilder(context, InplayDb::class.java, "inplay.db")
                    .addCallback(object : Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = INSTANCE!!.inplayDao()
                                val courseId = dao.insertCourse(Course(name = "Yas Acres Golf & Country Club", holes = 9))
                                val tee = "Orange"
                                val yardages = listOf(399, 240, 126, 342, 336, 86, 313, 329, 383)
                                val holes = yardages.mapIndexed{ index, yards ->
                                   val holeNum = index + 1
                                    val par = when (holeNum) { 3, 6 -> 3; 1, 9 -> 5; else -> 4}
                                    CourseHole(
                                        courseId = courseId,
                                        teeBox = tee,
                                        holeNumber = holeNum,
                                        par = par,
                                        yardage = yards
                                    )
                                }

                                    dao.insertHoles(holes)

                                // Seed one completed round - generated from sample of my own game
                                val roundId = dao.insertRound(
                                    Round(courseId = courseId, teeBox = "Orange", holes = 9, isDraft = false)
                                )

                                //param helper
                                fun s(
                                    h: Int, n: Int,
                                    club: String, lie: String, dist: Int,
                                    result: String, miss: String?,
                                    shotType: String, putt: Boolean,
                                    pen: Int = 0
                                ) = Stroke(
                                    roundId = roundId,
                                    holeNumber = h,
                                    strokeNo = n,
                                    club = club,
                                    lie = lie,
                                    distance = dist,
                                    result = result,
                                    miss = miss,
                                    shotType = shotType,
                                    isPutt = putt,
                                    penaltyStrokes = pen
                                )

                                // Hole 1 (Par 5) — 6
                                dao.insertStroke(s(1,1,"Driver","Tee",159,"Fairway",null,"Full",false))
                                dao.insertStroke(s(1,2,"5 Wood","Fairway",140,"Fairway",null,"Full",false))
                                dao.insertStroke(s(1,3,"PW","Fairway",18,"Green",null,"Chip",false))
                                dao.insertStroke(s(1,4,"SW","Green",64,"Green",null,"Chip",false))
                                dao.insertStroke(s(1,5,"Putter","Green",10,"Green",null,"Putt",true))
                                dao.insertStroke(s(1,6,"Putter","Green",3,"Hole",null,"Putt",true))

                                //  Hole 2 (Par 4) — 5
                                dao.insertStroke(s(2,1,"Driver","Tee",178,"Fairway",null,"Full",false))
                                dao.insertStroke(s(2,2,"LW","Fairway",33,"Green","Short","Chip",false))
                                dao.insertStroke(s(2,3,"LW","Fringe",21,"Green",null,"Chip",false))
                                dao.insertStroke(s(2,4,"Putter","Green",10,"Green",null,"Putt",true))
                                dao.insertStroke(s(2,5,"Putter","Green",2,"Hole",null,"Putt",true))

                                // Hole 3 (Par 3) — 4
                                dao.insertStroke(s(3,1,"8 Iron","Tee",115,"Fairway","Short","Full",false))
                                dao.insertStroke(s(3,2,"LW","Fairway",52,"Fringe",null,"Chip",false))
                                dao.insertStroke(s(3,3,"LW","Fringe",29,"Green",null,"Chip",false))
                                dao.insertStroke(s(3,4,"Putter","Green",5,"Hole",null,"Putt",true))

                                // Hole 4 (Par 4) — 7
                                dao.insertStroke(s(4,1,"Driver","Tee",156,"Fairway",null,"Full",false))
                                dao.insertStroke(s(4,2,"5 Wood","Fairway",121,"Fairway",null,"Full",false))
                                dao.insertStroke(s(4,3,"SW","Fairway",86,"Green","Short","Chip",false))
                                dao.insertStroke(s(4,4,"Putter","Green",10,"Green",null,"Putt",true))
                                dao.insertStroke(s(4,5,"Putter","Green",5,"Green",null,"Putt",true))
                                dao.insertStroke(s(4,6,"Putter","Green",2,"Green",null,"Putt",true))
                                dao.insertStroke(s(4,7,"Putter","Green",1,"Hole",null,"Putt",true))

                                //  Hole 5 (Par 4) — 7
                                dao.insertStroke(s(5,1,"Driver","Tee",123,"Fairway",null,"Full",false))
                                dao.insertStroke(s(5,2,"3 Wood","Fairway",142,"Fairway",null,"Full",false))
                                dao.insertStroke(s(5,3,"9 Iron","Fairway",72,"Fringe",null,"Full",false))
                                dao.insertStroke(s(5,4,"LW","Fringe",36,"Green",null,"Chip",false))
                                dao.insertStroke(s(5,5,"Putter","Green",10,"Green",null,"Putt",true))
                                dao.insertStroke(s(5,6,"Putter","Green",5,"Green",null,"Putt",true))
                                dao.insertStroke(s(5,7,"Putter","Green",2,"Hole",null,"Putt",true))

                                // Hole 6 (Par 3) — 3 (GIR)
                                dao.insertStroke(s(6,1,"9 Iron","Tee",97,"Green",null,"Full",false))
                                dao.insertStroke(s(6,2,"Putter","Green",15,"Green",null,"Putt",true))
                                dao.insertStroke(s(6,3,"Putter","Green",2,"Hole",null,"Putt",true))

                                // Hole 7 (Par 4) — 7
                                dao.insertStroke(s(7,1,"Driver","Tee",137,"Fairway",null,"Full",false))
                                dao.insertStroke(s(7,2,"5 Hybrid","Fairway",48,"Fairway",null,"Full",false))
                                dao.insertStroke(s(7,3,"SW","Fairway",69,"Green",null,"Full",false))
                                dao.insertStroke(s(7,4,"PW","Green",82,"Green",null,"Chip",false))
                                dao.insertStroke(s(7,5,"Putter","Green",10,"Green",null,"Putt",true))
                                dao.insertStroke(s(7,6,"Putter","Green",3,"Green",null,"Putt",true))
                                dao.insertStroke(s(7,7,"Putter","Green",1,"Hole",null,"Putt",true))

                                // Hole 8 (Par 4) — 7
                                dao.insertStroke(s(8,1,"Driver","Tee",151,"Fairway",null,"Full",false))
                                dao.insertStroke(s(8,2,"3 Wood","Fairway",64,"Fairway",null,"Full",false))
                                dao.insertStroke(s(8,3,"9 Iron","Fairway",99,"Green","Short","Full",false))
                                dao.insertStroke(s(8,4,"LW","Fringe",39,"Green",null,"Chip",false))
                                dao.insertStroke(s(8,5,"LW","Green",10,"Green",null,"Chip",false))
                                dao.insertStroke(s(8,6,"Putter","Green",1,"Green",null,"Putt",true))
                                dao.insertStroke(s(8,7,"Putter","Green",1,"Hole",null,"Putt",true))

// ---- Hole 9 (Par 5) — 7 ----
                                dao.insertStroke(s(9,1,"Driver","Tee",171,"Fairway",null,"Full",false))
                                dao.insertStroke(s(9,2,"7 Iron","Fairway",40,"Fairway",null,"Full",false))
                                dao.insertStroke(s(9,3,"PW","Fairway",97,"Green",null,"Full",false))
                                dao.insertStroke(s(9,4,"SW","Green",56,"Green",null,"Chip",false))
                                dao.insertStroke(s(9,5,"SW","Green",56,"Green",null,"Chip",false))
                                dao.insertStroke(s(9,6,"Putter","Green",10,"Green",null,"Putt",true))
                                dao.insertStroke(s(9,7,"Putter","Green",2,"Hole",null,"Putt",true))

                            }
                        }
                    })
                    .build()
                    .also  { INSTANCE = it }
            }
    }
}