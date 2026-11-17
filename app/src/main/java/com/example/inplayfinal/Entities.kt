package com.example.inplayfinal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val holes: Int, //9 hole or 18hole course (9 hole 2x 9, 18 hole 19 individual)

)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Course::class,
        parentColumns = ["id"],
        childColumns=["courseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("courseId"), Index(value = ["courseId", "teeBox", "holeNumber"], unique = true)]
)
@Serializable
data class CourseHole(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Long,
    val teeBox: String,
    val holeNumber: Int,
    val par: Int,
    //val strokeIndex: Int, - for use in future when Handicap is a feature in use
    val yardage: Int
)

@Entity
@Serializable
data class Round(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Long,
    val teeBox: String? = null,
    val holes: Int = 9,
    val datePlayed: Long = System.currentTimeMillis(),
    val isDraft: Boolean = true //inserts a round as a draft for persistence until it is completed
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Round::class,
        parentColumns = ["id"],
        childColumns = ["roundId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("roundId"),
        Index(value = ["roundId","holeNumber","strokeNo"], unique = true) //n number of strokes per hole
    ]
)
@Serializable
data class Stroke(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roundId: Long,
    val holeNumber: Int,
    val strokeNo: Int,
    val club: String,
    val lie: String, //Tee, Fairway, Rough, Sand, green?
    val distance: Int,
    val result: String, //Fairway, Rough, Sand, Green, Penalty
    val miss: String?, //Left, Right, Short, Long, Null
    val shotType: String, //Full,Chip,Bump
    val isPutt: Boolean,
    val penaltyStrokes: Int = 0,
)
