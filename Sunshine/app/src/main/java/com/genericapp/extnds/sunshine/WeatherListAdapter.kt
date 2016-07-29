package com.genericapp.extnds.sunshine

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*

/**
 * Created by Nooba(PratickRoy) on 29-07-2016.
 */

class WeatherListAdapter(val context: Context, val weatherData : List<String>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    companion object
    {
        const val TAG = "WeatherListAdapter"
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ViewHolder).draw()
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item,parent,false))
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        fun draw()
        {
            with(itemView)
            {
                day.text = "Today"
                weather_type.text = "Sunny"
                weather_type.text = "83/77"
            }
        }
    }

}