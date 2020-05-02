package com.example.td2_exo7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class SeanceActivity : AppCompatActivity() {

    var dataList = arrayListOf<Seance>()

    lateinit var adapter: SeanceAdapter
    lateinit var layoutManager : LinearLayoutManager

    private lateinit var seanceDatabase : SeanceRoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seance)

        seanceDatabase = SeanceRoomDatabase.getDatabase(this)

        val title = intent.getStringExtra("title")
        val value = intent.getStringExtra("value")


        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = SeanceAdapter(this)
        recyclerView.adapter = adapter

        getData(title, value)
    }

    fun getData(title : String, value : String){
        when(title){
            "Jour" -> getJourData(value)
            "Semaine" -> getWeekData(value)
            "Module" -> getModuleData(value)
            "Salle" -> getSalleData(value)
            "Enseignant" -> getEnsData(value)
        }
    }


    fun getWeekData(week : String){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().findSeancesByWeek(week))
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getJourData(day : String){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().findSeancesByDay(day))
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getModuleData(module : String){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().findSeancesByModule(module))
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getEnsData(ens : String){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().findSeancesByEnseignant(ens))
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getSalleData(salle : String){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().findSeancesBySalle(salle))
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }


    class SeanceAdapter(val activity : SeanceActivity) : RecyclerView.Adapter<SeanceAdapter.SeanceViewHolder>(){
        class SeanceViewHolder(v : View) : RecyclerView.ViewHolder(v){
            val titleModule = v.findViewById<TextView>(R.id.moduleTitle)
            val titleDate = v.findViewById<TextView>(R.id.dateTitle)
            val titleHD = v.findViewById<TextView>(R.id.heurDtitle)
            val titleHF = v.findViewById<TextView>(R.id.heureFTitle)
            val titleSalle = v.findViewById<TextView>(R.id.salleTitle)
            val titleEns = v.findViewById<TextView>(R.id.EnsTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeanceViewHolder {
            return SeanceViewHolder(LayoutInflater.from(activity).inflate(R.layout.seance_layout, parent, false))
        }

        override fun getItemCount(): Int {
            return activity.dataList.size
        }

        override fun onBindViewHolder(holder: SeanceViewHolder, position: Int) {
            val seance = activity.dataList[position]

            holder.titleDate.text = seance.jour
            holder.titleEns.text = seance.enseignant
            holder.titleHD.text = seance.hDebut
            holder.titleHF.text = seance.hFin
            holder.titleModule.text = seance.module
            holder.titleSalle.text = seance.salle

        }
    }
}
