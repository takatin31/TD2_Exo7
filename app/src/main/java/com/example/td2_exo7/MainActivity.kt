package com.example.td2_exo7

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var checkSpinner : Int = 0
    var dataList = arrayListOf<DataCount>()
    var currentPosition = 0
    private val PERMISSION_CODE: Int = 1000

    lateinit var adapter: GlobalDataAdapter
    lateinit var layoutManager : LinearLayoutManager

    private lateinit var seanceDatabase : SeanceRoomDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seanceDatabase = SeanceRoomDatabase.getDatabase(this)

        ArrayAdapter.createFromResource(
            this,
            R.array.data_menu,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
            dataSpinner.adapter = adapter
        }

        dataSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(++checkSpinner > 1) {
                    currentPosition = position
                    when(position){
                        0 -> getJourData()
                        1 -> getWeekData()
                        2 -> getModuleData()
                        3 -> getSalleData()
                        4 -> getEnsData()
                    }
                }
            }
        }

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = GlobalDataAdapter(this)
        recyclerView.adapter = adapter

        initData()
    }

    fun initData(){

        val jsonString = loadJson(this)


        val jsonObject = JSONObject(jsonString)

        val seances = jsonObject.getJSONArray("seances")

        Toast.makeText(this, "Data is loaded succefully", Toast.LENGTH_LONG).show()

        if (seances != null){
            try {
                for (i in 0 until seances.length()){
                    val seance = seances.getJSONObject(i)
                    val id = seance.getInt("seanceId")
                    val jour = seance.getString("jour")
                    val semaine = seance.getString("semaine")
                    val hD = seance.getString("heureD")
                    val hF = seance.getString("heureF")
                    val module = seance.getString("module")
                    val salle = seance.getString("salle")
                    val ens = seance.getString("enseignant")

                    val newS = Seance(id, jour, semaine, hD, hF, module, salle, ens)
                    addSeance(newS)
                }
            }catch (e : JSONException){
                e.printStackTrace()
            }

            getJourData()
        }

    }

    fun addSeance(seance: Seance) {
        AppExecutors.instance!!.diskIO().execute {
            seanceDatabase.seanceDao().addSeance(seance)

            AppExecutors.instance!!.mainThread().execute( Runnable {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                    != PackageManager.PERMISSION_GRANTED) {
                    val permission = arrayOf(Manifest.permission.WRITE_CALENDAR)
                    requestPermissions(permission, PERMISSION_CODE)
                }else {
                    addCalendar(seance)
                }

            })
        }
    }

    @SuppressLint("MissingPermission")
    private fun addCalendar(seance : Seance) {

        val jour = seance.jour
        val hD = seance.hDebut
        val hF = seance.hFin

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)

        val startCal = Calendar.getInstance()
        startCal.time = sdf.parse("$jour $hD")

        val endCal = Calendar.getInstance()
        endCal.time = sdf.parse("$jour $hF")

        val values = ContentValues().apply {
            val calID: Long = 1
            put(CalendarContract.Events.DTSTART, startCal.timeInMillis)
            put(CalendarContract.Events.DTEND, endCal.timeInMillis)
            put(CalendarContract.Events.TITLE, seance.module)
            put(CalendarContract.Events.CALENDAR_ID, calID)
            put(CalendarContract.Events.DESCRIPTION, "${seance.enseignant} ${seance.salle}")
            put(CalendarContract.Events.EVENT_TIMEZONE, "Africa/Algiers")
        }
        val uri: Uri? = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                } else {
                    Toast.makeText(this, "Vous ne disposez pas des permissions necessaires", Toast.LENGTH_SHORT)
                }
                return
            }
        }
    }

    fun getWeekData(){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().getWeekList())
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getJourData(){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().getDayList())
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getModuleData(){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().getModuleList())
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getEnsData(){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().getEnsList())
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getSalleData(){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().getSalleList())
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    private fun loadJson(context: Context): String? {
        var input: InputStream? = null
        var jsonString: String

        try {
            // Create InputStream
            input = context.assets.open("data.json")

            val size = input.available()

            // Create a buffer with the size
            val buffer = ByteArray(size)

            // Read data from InputStream into the Buffer
            input.read(buffer)

            // Create a json String
            jsonString = String(buffer)
            return jsonString;
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            // Must close the stream
            input?.close()
        }

        return null
    }


    class GlobalDataAdapter(val activity : MainActivity) : RecyclerView.Adapter<GlobalDataAdapter.GlobalDataViewHolder>(){
        class GlobalDataViewHolder(v : View) : RecyclerView.ViewHolder(v){
            val titlecontent = v.findViewById<TextView>(R.id.title_content)
            val nbrSeances = v.findViewById<TextView>(R.id.nbrSeanceContent)
            val title =  v.findViewById<TextView>(R.id.title_data)
            val layout = v.findViewById<RelativeLayout>(R.id.dataItemLayout)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlobalDataViewHolder {
            return GlobalDataViewHolder(LayoutInflater.from(activity).inflate(R.layout.item1_layout, parent, false))
        }

        override fun getItemCount(): Int {
            return activity.dataList.size
        }

        override fun onBindViewHolder(holder: GlobalDataViewHolder, position: Int) {
            val title = activity.resources.getStringArray(R.array.data_menu)[activity.currentPosition]
            val value = activity.dataList[position].data

            holder.nbrSeances.text = activity.dataList[position].count.toString()
            holder.titlecontent.text = value
            holder.title.text = title

            holder.layout.setOnClickListener {
                val intent = Intent(activity, SeanceActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("value", value)
                activity.startActivity(intent)
            }
        }
    }


}
