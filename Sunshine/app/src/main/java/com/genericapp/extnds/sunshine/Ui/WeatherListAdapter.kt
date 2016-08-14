package com.genericapp.extnds.sunshine.Ui

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.genericapp.extnds.sunshine.Models.Retrofit.ForcastData
import com.genericapp.extnds.sunshine.Models.SugarORM.Forcast
import com.genericapp.extnds.sunshine.R
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item_today.view.*
import java.util.*

/**
 * Created by Nooba(PratickRoy) on 29-07-2016.
 */

class WeatherListAdapter(val context: Context, val forcastList: List<Forcast>? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TAG = "WeatherListAdapter"
        const val DAY_TAG = "day"
        const val WEATHER_TAG = "weather"
        const val TEMPERATURE_TAG = "temperature"

        const val WEATHER_VIEW_TYPE_TODAY = 1
        const val WEATHER_VIEW_TYPE_LATER = 2
    }

    interface WeatherListAdapterCallback {
        fun openDetailsFragment(forcastData: ForcastData)
        fun openDetailsFragment(forcastId: Long?)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        if(forcastList==null)
            return

        if (holder is ViewHolderToday)
            holder.draw(forcastList[position])
        else if (holder is ViewHolderLater)
            holder.draw(forcastList[position])

    }

    override fun getItemCount(): Int {
        if (forcastList != null)
            return forcastList.size
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return WEATHER_VIEW_TYPE_TODAY
        return WEATHER_VIEW_TYPE_LATER
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == WEATHER_VIEW_TYPE_TODAY)
            return ViewHolderToday(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_today, parent, false))
        return ViewHolderLater(LayoutInflater.from(parent?.context).inflate(R.layout.list_item, parent, false))
    }

    class ViewHolderToday(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun draw(forcast: Forcast) {
            with(itemView)
            {
                val date = Date(forcast.date!! * 1000L)
                val currentDate = Date(System.currentTimeMillis())

                day_month_date_text_view.text = context.getString(R.string.format_day_month_date, "Today", "${DateFormat.format("MMMM", date)}", date.date)

                if(currentDate.hours>6 && currentDate.hours<18)
                    weather_type_today_image_view.setImageBitmap(BitmapFactory.decodeByteArray(forcast.iconDay, 0, forcast.iconDay!!.size))
                else
                    weather_type_today_image_view.setImageBitmap(BitmapFactory.decodeByteArray(forcast.iconNight, 0, forcast.iconNight!!.size))
                max_temp_today_text_view.text = context.getString(R.string.format_temperature, forcast.maxTemp!!)
                min_temp_today_text_view.text = context.getString(R.string.format_temperature, forcast.minTemp!!)

                setOnClickListener {
                    (context as WeatherListAdapterCallback).openDetailsFragment(forcast.id)
                }
            }
        }
    }

    class ViewHolderLater(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun draw(forcast: Forcast) {
            with(itemView)
            {
                val date = Date(forcast.date!! * 1000L)
                val currentDate = Date(System.currentTimeMillis())

                if (currentDate.date + 1 == date.date)
                    day_text_view.text = "Tomorrow"
                else
                    day_text_view.text = "${DateFormat.format("EEEE", date) as String}"

                weather_type_text_view.text = "${forcast.main}"

                if(currentDate.hours>6 && currentDate.hours<18)
                    weather_type_image_view.setImageBitmap(BitmapFactory.decodeByteArray(forcast.iconDay, 0, forcast.iconDay!!.size))
                else
                    weather_type_image_view.setImageBitmap(BitmapFactory.decodeByteArray(forcast.iconNight, 0, forcast.iconNight!!.size))
                max_temp_text_view.text = context.getString(R.string.format_temperature, forcast.maxTemp!!)
                min_temp_text_view.text = context.getString(R.string.format_temperature, forcast.minTemp!!)

                setOnClickListener {
                    (context as WeatherListAdapterCallback).openDetailsFragment(forcast.id)
                }
            }
        }
    }

}