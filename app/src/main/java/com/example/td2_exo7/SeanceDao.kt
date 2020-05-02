package com.example.td2_exo7

import androidx.room.*

@Dao
interface SeanceDao {


    @Query("Select * from Seance")
    fun loadAllSeances(): List<Seance>

    @Query("Select * from Seance where id = :seanceId")
    fun findSeanceById(seanceId : Int):List<Seance>

    @Query("Select * from Seance where jour = :day")
    fun findSeancesByDay(day : String):List<Seance>

    @Query("Select * from Seance where module = :module")
    fun findSeancesByModule(module : String):List<Seance>

    @Query("Select * from Seance where salle = :salle")
    fun findSeancesBySalle(salle : String):List<Seance>

    @Query("Select * from Seance where enseignant = :ens")
    fun findSeancesByEnseignant(ens : String):List<Seance>

    @Query("Select * from Seance where weekN = :week")
    fun findSeancesByWeek(week : String):List<Seance>

    @Query("Select jour data, count(*) count from Seance group by jour")
    fun getDayList():List<DataCount>

    @Query("Select weekN data, count(*) count from Seance group by weekN")
    fun getWeekList():List<DataCount>

    @Query("Select module data, count(*) count from Seance group by module")
    fun getModuleList():List<DataCount>

    @Query("Select salle data, count(*) count from Seance group by salle")
    fun getSalleList():List<DataCount>

    @Query("Select enseignant data, count(*) count from Seance group by enseignant")
    fun getEnsList():List<DataCount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSeance(seance: Seance)

    @Update
    fun modifySeance(seance: Seance)

    @Delete
    fun deleteSeance(seance: Seance)

}
