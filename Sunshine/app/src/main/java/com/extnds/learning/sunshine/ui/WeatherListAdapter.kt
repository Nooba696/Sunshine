package com.extnds.learning.sunshine.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.extnds.learning.sunshine.R
import com.extnds.learning.sunshine.models.sugarORM.Forcast
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item_today.view.*
import java.util.*


class WeatherListAdapter(val context: Context, val forcastList: List<Forcast>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TAG = "WeatherListAdapter"
        const val LOG_TAG = MainActivity.LOG_TAG_BASE + TAG
        const val WEATHER_VIEW_TYPE_TODAY = 1
        const val WEATHER_VIEW_TYPE_LATER = 2
    }

    interface WeatherListAdapterCallback {
        fun openDetailsFragment(forcastId: Long?)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        if (forcastList == null) {
            Log.d(MainFragment.LOG_TAG, "No data fed to WeatherListAdapter, So nothing to populate")
            return
        }

        if (holder is ViewHolderToday) {
            Log.d(MainFragment.LOG_TAG, "Populating Today List Item")
            holder.draw(forcastList[position])
        } else if (holder is ViewHolderLater) {
            Log.d(MainFragment.LOG_TAG, "Populating Later List Item")
            holder.draw(forcastList[position])
        }

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
                val date = Calendar.getInstance()
                date.timeInMillis=forcast.date?.times(1000L) ?: 0L

                val currentDate = Calendar.getInstance()
                currentDate.timeInMillis=System.currentTimeMillis()

                day_month_date_text_view.text = context.getString(R.string.format_day_month_date, "Today", "${DateFormat.format("MMMM", date)}", date.get(Calendar.DATE))

                if (currentDate.get(Calendar.HOUR_OF_DAY) > 6 && currentDate.get(Calendar.HOUR_OF_DAY) < 18)
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
                val date = Calendar.getInstance()
                date.timeInMillis=forcast.date?.times(1000L) ?: 0L

                val currentDate = Calendar.getInstance()
                currentDate.timeInMillis=System.currentTimeMillis()

                if (currentDate.get(Calendar.DATE) + 1 == date.get(Calendar.DATE))
                    day_text_view.text = "Tomorrow"
                else
                    day_text_view.text = "${DateFormat.format("EEEE", date) as String}"

                weather_type_text_view.text = "${forcast.main}"

                if (currentDate.get(Calendar.HOUR_OF_DAY) > 6 && currentDate.get(Calendar.HOUR_OF_DAY) < 18)
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