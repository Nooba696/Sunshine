package com.genericapp.extnds.sunshine.Ui

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
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

class WeatherListAdapter(val forcastDataList: List<ForcastData>? = null, val forcastList: List<Forcast>? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TAG = "WeatherListAdapter"
        const val DAY_TAG = "day"
        const val WEATHER_TAG = "weather"
        const val TEMPERATURE_TAG = "temperature"

        const val WEATHER_VIEW_TYPE_TODAY = 1
        const val WEATHER_VIEW_TYPE_LATER = 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        if (holder is ViewHolderToday) {

            if (forcastDataList != null)
                (holder).draw(forcastDataList[position])
            else if (forcastList != null)
                (holder).draw(forcastList[position])
        } else if (holder is ViewHolderLater) {
            if (forcastDataList != null)
                (holder).draw(forcastDataList[position])
            else if (forcastList != null)
                (holder).draw(forcastList[position])
        }
    }

    override fun getItemCount(): Int {
        if (forcastDataList != null)
            return forcastDataList.size
        else if (forcastList != null)
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
        fun draw(forcastData: ForcastData) {
            with(itemView)
            {
                val date = Date(forcastData.dt!! * 1000L)
                day_month_date_text_view.text = context.getString(R.string.format_day_month_date,"Today","${DateFormat.format("MMMM", date)}",date.date)
                max_temp_today_text_view.text = context.getString(R.string.format_temperature,forcastData.temp!!.max!!)
                min_temp_today_text_view.text = context.getString(R.string.format_temperature,forcastData.temp!!.min!!)

                setOnClickListener {
                    val intent = Intent(context, DetailsActivity::class.java)
                    //intent.putExtra(DAY_TAG, day.text.toString())
                    //.putExtra(WEATHER_TAG, weather_type.text.toString())
                    //intent.putExtra(TEMPERATURE_TAG, temperature.text.toString())
                    context.startActivity(intent)
                }
            }
        }

        fun draw(forcast: Forcast) {
            with(itemView)
            {
                val date = Date(forcast.date!! * 1000L)
                if(Date(System.currentTimeMillis()).date + 1 == date.date)
                    day_month_date_text_view.text = context.getString(R.string.format_day_month_date,"Tomorrow","${DateFormat.format("MMMM", date)}",date.date)
                else
                    day_month_date_text_view.text = context.getString(R.string.format_day_month_date,"${DateFormat.format("EEEE", date)}","${DateFormat.format("MMMM", date)}",date.date)
                max_temp_today_text_view.text = context.getString(R.string.format_temperature,forcast.maxTemp!!)
                min_temp_today_text_view.text = context.getString(R.string.format_temperature,forcast.minTemp!!)

                setOnClickListener {
                    val intent = Intent(context, DetailsActivity::class.java)
                    //intent.putExtra(DAY_TAG, day.text.toString())
                    //.putExtra(WEATHER_TAG, weather_type.text.toString())
                    //intent.putExtra(TEMPERATURE_TAG, temperature.text.toString())
                    context.startActivity(intent)
                }
            }
        }
    }

    class ViewHolderLater(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun draw(forcastData: ForcastData) {
            with(itemView)
            {
                val date = Date(forcastData.dt!! * 1000L)
                day_text_view.text = "${DateFormat.format("EEEE", date) as String}"
                weather_type_text_view.text = forcastData.weather!![0].main
                max_temp_text_view.text = context.getString(R.string.format_temperature,forcastData.temp!!.max!!)
                min_temp_text_view.text = context.getString(R.string.format_temperature,forcastData.temp!!.min!!)

                setOnClickListener {
                    val intent = Intent(context, DetailsActivity::class.java)
                    //intent.putExtra(DAY_TAG, day.text.toString())
                    //intent.putExtra(WEATHER_TAG, weather_type.text.toString())
                    //intent.putExtra(TEMPERATURE_TAG, temperature.text.toString())
                    context.startActivity(intent)
                }
            }
        }

        fun draw(forcast: Forcast) {
            with(itemView)
            {
                val date = Date(forcast.date!! * 1000L)
                day_text_view.text = "${DateFormat.format("EEEE", date) as String}"
                weather_type_text_view.text = "${forcast.weatherContdId}"
                max_temp_text_view.text = context.getString(R.string.format_temperature,forcast.maxTemp!!)
                min_temp_text_view.text = context.getString(R.string.format_temperature,forcast.minTemp!!)

                setOnClickListener {
                    val intent = Intent(context, DetailsActivity::class.java)
                    //intent.putExtra(DAY_TAG, day.text.toString())
                    //intent.putExtra(WEATHER_TAG, weather_type.text.toString())
                    //intent.putExtra(TEMPERATURE_TAG, temperature.text.toString())
                    context.startActivity(intent)
                }
            }
        }
    }

}