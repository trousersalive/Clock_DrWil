package com.example.minimalclockwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ClockWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) { ids.forEach { update(context, manager, it) }; schedule(context) }
    override fun onEnabled(context: Context) { schedule(context) }
    override fun onDisabled(context: Context) { cancel(context) }
    override fun onReceive(context: Context, intent: android.content.Intent) { super.onReceive(context, intent); if (intent.action=="android.intent.action.TIME_SET" || intent.action=="android.intent.action.TIMEZONE_CHANGED" || intent.action=="android.intent.action.DATE_CHANGED") updateAll(context) }

    companion object {
        private const val PREF="clockprefs"
        private fun prefs(c: Context, id:Int)=c.getSharedPreferences(PREF,0)
        fun update(c: Context, m:AppWidgetManager, id:Int) {
            val now=Date(); val locale=Locale.getDefault()
            val time=SimpleDateFormat("HH:mm",locale).format(now)
            val date=SimpleDateFormat("EEEE d MMMM",locale).format(now)
            val font=prefs(c,id).getInt("font",0)
            val v=RemoteViews(c.packageName,R.layout.widget_clock)
            val times=intArrayOf(R.id.time0,R.id.time1,R.id.time2,R.id.time3,R.id.time4)
            val dates=intArrayOf(R.id.date0,R.id.date1,R.id.date2,R.id.date3,R.id.date4)
            for(i in 0..4){ v.setViewVisibility(times[i],if(i==font)View.VISIBLE else View.GONE); v.setViewVisibility(dates[i],if(i==font)View.VISIBLE else View.GONE); v.setTextViewText(times[i],time); v.setTextViewText(dates[i],date) }
            m.updateAppWidget(id,v)
        }
        fun updateAll(c:Context){ val m=AppWidgetManager.getInstance(c); val ids=m.getAppWidgetIds(android.content.ComponentName(c,ClockWidgetProvider::class.java)); ids.forEach{update(c,m,it)}; schedule(c) }
        private fun schedule(c:Context){ val am=c.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager; val pi=android.app.PendingIntent.getBroadcast(c,9876,android.content.Intent(c,TickReceiver::class.java),android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE); am.cancel(pi); val first=(System.currentTimeMillis()/60000+1)*60000; am.setRepeating(android.app.AlarmManager.RTC_WAKEUP,first,60000,pi) }
        private fun cancel(c:Context){ val am=c.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager; val pi=android.app.PendingIntent.getBroadcast(c,9876,android.content.Intent(c,TickReceiver::class.java),android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE); am.cancel(pi) }
    }
}

class TickReceiver: android.content.BroadcastReceiver(){ override fun onReceive(c:Context,i:android.content.Intent){ ClockWidgetProvider.updateAll(c) } }
