package com.genericapp.extnds.sunshine

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.genericapp.extnds.sunshine.Models.ForcastData
import kotlinx.android.synthetic.main.list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Nooba(PratickRoy) on 29-07-2016.
 */

class WeatherListAdapter(val context: Context, val forcastDataList : List<ForcastData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    companion object
    {
        const val TAG = "WeatherListAdapter"
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ViewHolder).draw(forcastDataList[position])
    }

    override fun getItemCount(): Int {
        return forcastDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item,parent,false))
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        fun draw(forcastData: ForcastData)
        {
            with(itemView)
            {
                val date = Date(forcastData.dt!! * 1000L)
                day.text = "${DateFormat.format("EEE", date) as String},${DateFormat.format("MMM", date) as String} ${date.date}"
                weather_type.text = forcastData.weather!![0].main
                temperature.text = "${forcastData.temp!!.max!!.toInt()}/${forcastData.temp.min!!.toInt()}"
            }
        }
    }

}