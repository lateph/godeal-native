package com.example.primapay.adapter


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.primapay.R
import com.idemia.wa.api.Failure
import com.idemia.wa.api.WaCard
import com.idemia.wa.api.wms.GetResourceListener
import com.idemia.wa.api.wms.ResourcesService
import com.idemia.wa.api.wms.WaResource
import kotlinx.android.synthetic.main.col_card.view.*
import java.io.File.separator
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.io.FileInputStream




class ListCardAdapter(val items : List<WaCard>, val context: Context, val resourceService: ResourcesService, val clickListener: (WaCard) -> Unit) : RecyclerView.Adapter<ListCardAdapterHolder>() {
    override fun onBindViewHolder(adap: ListCardAdapterHolder, p1: Int) {
        val card = items.get(p1)
        adap.bind(items.get(p1), clickListener)
        if(card.cardArts.size > 0){
            val art = card.cardArts.get(0)
            val myPrivateFile = File(context.getFilesDir(),art.resourceId + ".png")
            if(myPrivateFile.exists()){
                val length = myPrivateFile.length().toInt()

                val bytes = ByteArray(length)

                val inputSteam = FileInputStream(myPrivateFile)
                try {
                    inputSteam.read(bytes)
                } catch (e : Exception) {
                    Log.d("file", e.message)
                }finally {
                    inputSteam.close()
                }

                val bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                adap.menuImg.setImageBitmap(bm)
            }
            else{
                resourceService.getResource(art.resourceId, object : GetResourceListener {
                    override fun onCompleted(p0: WaResource?) {
                        val bm = BitmapFactory.decodeByteArray(p0?.data, 0, p0!!.data.size)
//                        try {
//                            val directory = File(context.getFilesDir())
//                            if (!directory.exists()) {
//                                directory.mkdir()
//                                // If you require it to make the entire directory path including parents,
//                                // use directory.mkdirs(); here instead.
//                            }
//                        }
//                        catch (e: Exception) {
//                            Log.e("file", "error create directory")
//                        }
                        try {
                            myPrivateFile.createNewFile()
                            myPrivateFile.getPath()
                            val fos = FileOutputStream(myPrivateFile)
                            fos.write(p0.data)
                            fos.close()
                        }
                        catch (e: Exception) {
                            Log.e("file", "error nulis file")
                            Log.e("file", e.message)
                        }
                        adap.menuImg.setImageBitmap(bm)
                    }

                    override fun onError(p0: Failure?) {
                        Log.d("error", p0.toString())
                    }
                })
            }
        }
//        p0?.menuImg?.setImageResource(items.get(p1).img)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ListCardAdapterHolder {
        return ListCardAdapterHolder(
            LayoutInflater.from(context).inflate(
                R.layout.col_card,
                p0,
                false
            )
        )
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }
}

class ListCardAdapterHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val menuImg = view.colImage

    fun bind(part: WaCard, clickListener: (WaCard) -> Unit) {
        itemView.setOnClickListener { clickListener(part)}
    }
}