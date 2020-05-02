package com.example.td2_exo7

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Seance")
data class Seance (@PrimaryKey var id : Int, var jour : String, var weekN : String,  var hDebut : String, var hFin : String, var module : String, var salle : String, var enseignant : String){
}