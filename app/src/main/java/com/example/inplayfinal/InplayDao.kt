package com.example.inplayfinal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InplayDao{
    //Course
    @Query("SELECT * FROM Course ORDER BY name")
    fun getCourses(): Flow<List<Course>>

    @Insert
    suspend fun insertCourse(course: Course): Long

    //TeeList
    @Query("""
        SELECT DISTINCT teeBox
        FROM CourseHole
        WHERE courseId = :courseId
        ORDER BY teeBox
    """)
    fun getTeesForCourse(courseId: Long): Flow<List<String>>

    //Holes for Course+Teebox
    @Query("""
        SELECT * FROM CourseHole
        WHERE courseId = :courseId AND teeBox = :teeBox
        ORDER BY holeNumber
    """)
    fun getHoles(courseId: Long, teeBox: String): Flow<List<CourseHole>>

    @Insert
    suspend fun insertHoles(holes: List<CourseHole>)

    @Insert
    suspend fun insertHole(hole: CourseHole)

    @Insert
    suspend fun insertRound(round: Round): Long

    //Strokes
    @Insert

    suspend fun insertStroke(stroke: Stroke)
    @Query("""
        SELECT * FROM Stroke
        WHERE roundId = :roundId AND holeNumber = :hole
        ORDER BY strokeNo
    """)
    fun strokesPerHole(roundId: Long, hole: Int): Flow<List<Stroke>>

    @Query("""
        SELECT MAX(strokeNo) FROM Stroke
        WHERE roundId= :roundId AND holeNumber = :hole
    """)
    suspend fun lastStrokeNumber(roundId: Long, hole: Int): Int?

    @Query("""
        SELECT COUNT (*) FROM Stroke
        WHERE roundId= :roundId AND holeNumber =:hole AND isPutt = 1
    """)
    suspend fun holeTotalPutts(roundId: Long, hole: Int): Int

    @Update
    suspend fun updateStroke(stroke: Stroke)

    @Query("DELETE FROM Stroke WHERE id = :strokeId")
    suspend fun deleteStroke(strokeId: Long)

    @Query("UPDATE Round SET isDraft = 0 WHERE id = :roundId")
    suspend fun completeRound(roundId: Long)

    @Query("DELETE FROM Round WHERE id = :roundId")
    suspend fun deleteRound(roundId: Long)

    //Completed Rounds
    @Query("SELECT * FROM Round WHERE isDraft = 0 ORDER BY datePlayed DESC")
    fun getCompletedRounds(): Flow<List<Round>>

    @Query("SELECT courseId FROM Round WHERE id = :roundId LIMIT 1")
    fun getCourseIdFromRound(roundId: Long): Flow<Long>

    @Query("SELECT teeBox FROM Round WHERE id = :roundId LIMIT 1")
    fun getTeeboxFromRound(roundId: Long): Flow<String>

    data class RoundHeader(
        val courseName: String,
        val teeBox: String,
        val holes: Int
    )
    @Query("""
        SELECT c.name AS courseName, r.teeBox AS teeBox, r.holes AS holes
        FROM Round r
        JOIN Course c ON c.id = r.courseId
        WHERE r.id = :roundId
        LIMIT 1
    """)
    fun roundHeader(roundId: Long): Flow<RoundHeader?>


    data class RoundSummary(
        val id: Long,
        val courseName: String,
        val datePlayed: Long,
        val holes: Int,
        val gross: Int
    )

    @Query("""
    SELECT r.id AS id,
           c.name AS courseName,
           r.datePlayed AS datePlayed,
           r.holes AS holes,
           COUNT(s.id) AS gross
    FROM Round r
    JOIN Course c ON c.id = r.courseId
    LEFT JOIN Stroke s ON s.roundId = r.id
    WHERE r.isDraft = 0
    GROUP BY r.id
    ORDER BY r.datePlayed DESC
""")
    fun completedRoundSummaries(): Flow<List<RoundSummary>>

    @Query("""
    SELECT * FROM Stroke
    WHERE roundId = :roundId
    ORDER BY holeNumber, strokeNo
""")
    fun strokesByRound(roundId: Long): Flow<List<Stroke>>

}