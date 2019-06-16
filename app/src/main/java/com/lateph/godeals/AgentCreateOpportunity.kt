package com.lateph.godeals

import android.app.Activity
import android.app.DatePickerDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lateph.godeals.retrofit.model.CreateOpportunity
import kotlinx.android.synthetic.main.agent_createo.*
import java.util.*
//import jdk.nashorn.internal.objects.NativeDate.getTime
import java.text.SimpleDateFormat
import com.lateph.godeals.retrofit.INodeJS
import com.lateph.godeals.retrofit.RertrofitClient
import com.lateph.godeals.retrofit.model.ListArea
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.databinding.BindingAdapter
import android.util.Log
import com.lateph.godeals.GodealsApplication.Companion.listCities


class AgentCreateOpportunity : AppCompatActivity() {
    var model = CreateOpportunity.Body()
    val myFormat = "yyyy-MM-dd"
    val sdf = SimpleDateFormat(myFormat, Locale.US)
    var compositeDisposable = CompositeDisposable()
    lateinit var myAPI: INodeJS



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.agent_createo)
        val binding: com.lateph.godeals.databinding.CreateO = DataBindingUtil.setContentView(
            this, R.layout.agent_createo)

        binding.opportunity = model
        model.areaIds.add("1")

        // set default
        val now = Calendar.getInstance()
        model.checkInDate.set(sdf.format(now.getTime()))
        now.add(Calendar.DAY_OF_MONTH, 1)
        model.checkOutDate.set(sdf.format(now.getTime()))

        checkInDate.setOnClickListener {
                val _date = model.checkInDate.get()?.split("-")
                val dp = DatePickerDialog(
                    this@AgentCreateOpportunity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        val date = Calendar.getInstance()
                        date.set(Calendar.YEAR, year)
                        date.set(Calendar.MONTH, month)
                        date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                         //In which you need put here
                        model.checkInDate.set(sdf.format(date.getTime()))
                    }, _date!![0].toInt(), _date!![1].toInt() - 1,
                    _date[2].toInt()
                )
            dp.setTitle("Check In Date")
            dp.show()
        }

        checkOutDate.setOnClickListener {
            val _date = model.checkOutDate.get()?.split("-")
            val dp = DatePickerDialog(
                this@AgentCreateOpportunity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val date = Calendar.getInstance()
                    date.set(Calendar.YEAR, year)
                    date.set(Calendar.MONTH, month)
                    date.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    model.checkOutDate.set(sdf.format(date.getTime()))
                }, _date!!.get(0).toInt(), _date!!.get(1).toInt() - 1,
                _date!!.get(2).toInt()
            )
            dp.setTitle("Check Out Date")
            dp.show()
        }

        areaIds.setOnClickListener {
            val myIntent = Intent(this@AgentCreateOpportunity, AgentSelectArea::class.java)
            myIntent.putExtra("ids", ArrayList<String>(model.areaIds.toList()))
            this@AgentCreateOpportunity.startActivityForResult(
                myIntent,
                1
            )
        }

        button.setOnClickListener {
            Log.d("mytest", model.areaIds.joinToString())
        }
        loadData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            val data = data?.extras?.getStringArrayList("ids")
            model.setArea(data!!.toList())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun loadData(){
        val retrofit: Retrofit = RertrofitClient.create(this@AgentCreateOpportunity)
        myAPI = retrofit.create(INodeJS::class.java)
        compositeDisposable
            .add(
                myAPI.listArea().subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe( {result ->
                    var mApp = GodealsApplication()
                    listCities = result.data
//                        val adapter = ArrayAdapter(
//                            this,
//                            android.R.layout.simple_dropdown_item_1line, result.data.map { it.name }
//                        )
//                        areaIds.setAdapter(adapter)
//                        areaIds.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

                }, {
                        throwable ->

//                        progressBar.visibility = View.INVISIBLE
                })
            )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
