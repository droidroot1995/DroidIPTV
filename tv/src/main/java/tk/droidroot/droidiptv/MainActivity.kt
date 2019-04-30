/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tk.droidroot.droidiptv

import android.app.ActionBar
import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.SimpleAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL

/**
 * Loads [MainFragment].
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        //toolbar?.title = "DroidIPTV"

        setSupportActionBar(toolbar)
        setContentView(R.layout.activity_main)

        //supportActionBar?.title = "DroidIPTV"
        //supportActionBar?.setDisplayShowTitleEnabled(true)

        val actionBar = supportActionBar
        actionBar?.title = "DroidIPTV"
        actionBar?.setDisplayShowTitleEnabled(true)

        //actionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_CUSTOM)

        //kotlin

        app_drawer.setScrimColor(Color.TRANSPARENT)

        val mDrawerToggle: ActionBarDrawerToggle = object:ActionBarDrawerToggle(this, app_drawer, toolbar, R.string.drawer_open, R.string.drawer_close){
            override fun onDrawerOpened(drawerView: View){
                super.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }
        }

        mDrawerToggle.isDrawerIndicatorEnabled = true
        app_drawer.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        val mListView = channelList

        val pd = PlaylistDownloader(applicationContext, mListView)
        pd.execute("link")
    }

    override fun onResume() {
        super.onResume()
    }

    class PlaylistDownloader(context: Context, lw: ListView) : AsyncTask<String, Void, List<Map<String, String>>?>(){

        private val context: Context
        private val lw: ListView

        init {
            this.context = context.applicationContext
            this.lw = lw
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("Playlist", "Downloading playlist")
        }

        override fun doInBackground(vararg params: String): List<Map<String, String>>? {

            val channelList = mutableListOf<Map<String, String>>()
            try {

                if(params[0].equals("link")) {
                    val address: String = "http://help.avk-wellcom.info/avk.m3u"

                    val url: URL = URL(address)

                    val inputStream = url.openStream()
                    val lineList = mutableListOf<String>()
                    inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }

                    for (l in lineList) {
                        Log.d("Playlist", l)
                    }

                    inputStream.close()



                    for(i in 1 until lineList.size step 2){

                        val chMap = mutableMapOf<String, String>()

                        val name = lineList[i].split(',')[1]
                        chMap.put("name", name)

                        val number = lineList[i].split(',')[0].split(':')[1]
                        chMap.put("number", number)

                        val chUrl = lineList[i+1]
                        chMap.put("url", chUrl)

                        channelList.add(chMap)
                    }
                }
                else if (params[0].equals("file"))
                {
                    val inputStream = File(params[1]).inputStream()

                    val lineList = mutableListOf<String>()
                    inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }

                    for (l in lineList) {
                        Log.d("Playlist", l)
                    }

                }
            }
            catch (e:InterruptedException){
                e.printStackTrace()
            }
            return channelList
        }

        override fun onPostExecute(result: List<Map<String, String>>?) {
            super.onPostExecute(result)
            Log.d("Playlist", "Playlist was downloaded")

            if(result?.size != 0){
                val from = arrayOf("name", "number", "url")
                val to = intArrayOf(R.id.channelName, R.id.channelNumber, R.id.channelURL)

                val adapter = SimpleAdapter(context, result, R.layout.channel_list_item, from, to)
                lw.setAdapter(adapter)
            }
        }
    }
}
