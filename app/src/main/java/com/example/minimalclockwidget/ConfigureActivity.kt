package com.example.minimalclockwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Button

class ConfigureActivity: Activity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_configure)
  val id=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID)
  if(id==AppWidgetManager.INVALID_APPWIDGET_ID){finish();return}
  val spinner=findViewById<Spinner>(R.id.fontSpinner)
  spinner.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayOf("Sans","Serif","Monospace","Condensed","Light"))
  val current=getSharedPreferences("clockprefs",0).getInt("font",0); spinner.setSelection(current)
  findViewById<Button>(R.id.saveButton).setOnClickListener{ getSharedPreferences("clockprefs",0).edit().putInt("font",spinner.selectedItemPosition).apply(); ClockWidgetProvider.update(this,AppWidgetManager.getInstance(this),id); setResult(RESULT_OK,Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id)); finish() }
 }
}
