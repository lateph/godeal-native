package com.lateph.godeals.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lateph.godeals.R
import kotlinx.android.synthetic.main.col_main.view.*

data class MainMenu(val text:String, val img:Int)

class MainMenuAdapter(val items : ArrayList<MainMenu>, val context: Context) : RecyclerView.Adapter<MainMenuAdapter.ViewHolder>() {
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0?.menuText?.text = items.get(p1).text
        p0?.menuImg?.setImageResource(items.get(p1).img)
    }

    lateinit var mClickListener: ClickListener

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(pos: MainMenu, aView: ImageView)
    }

//    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
//        p0?.menuText?.text = items.get(p1).text
//        p0?.menuImg?.setImageResource(items.get(p1).img)
//    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.col_main,
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

    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) , View.OnClickListener {
        override fun onClick(v: View) {
            mClickListener.onClick(items[adapterPosition], menuImg)
        }
        // Holds the TextView that will add each animal to
        val menuText = view.colText
        val menuImg = view.colImage

        init {
            itemView.setOnClickListener(this)
        }
    }
}

