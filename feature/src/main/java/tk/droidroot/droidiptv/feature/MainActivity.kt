package tk.droidroot.droidiptv.feature
import tk.droidroot.droidiptv.feature.R

import android.app.UiModeManager
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.channel_list_item.view.*
import tk.droidroot.droidiptv.feature.player.VLCPlayerActivity
import java.io.File
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

        val pd = PlaylistDownloader(applicationContext, mListView, this)
        pd.execute("link")
    }

    override fun onResume() {
        super.onResume()
    }

    class PlaylistDownloader(context: Context, lw: ListView, mainActivity: AppCompatActivity) : AsyncTask<String, Void, List<Map<String, String>>?>(){

        private val context: Context
        private val lw: ListView
        private val mainActivity: AppCompatActivity

        init {
            this.context = context.applicationContext
            this.lw = lw
            this.mainActivity = mainActivity
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("Playlist", "Downloading playlist")
        }

        override fun doInBackground(vararg params: String): List<Map<String, String>>? {

            val channelList = mutableListOf<Map<String, String>>()
            try {

                if(params[0].equals("link")) {

                    try {
                        val address: String = "http://help.avk-wellcom.info/avk.m3u"

                        val url: URL = URL(address)

                        val inputStream = url.openStream()
                        val lineList = mutableListOf<String>()
                        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }

                        for (l in lineList) {
                            Log.d("Playlist", l)
                        }

                        inputStream.close()


                        var j = 1
                        for (i in 1 until lineList.size step 2) {

                            val chMap = mutableMapOf<String, String>()

                            val name = lineList[i].split(',')[1]
                            chMap.put("name", name)

                            val number = lineList[i].split(',')[0].split(':')[1]
                            chMap.put("number", j.toString())

                            val chUrl = lineList[i + 1]
                            chMap.put("url", chUrl)

                            channelList.add(chMap)
                            j++
                        }
                    }
                    catch (e:Exception){
                        val toast = Toast.makeText(context, "Network is not available", Toast.LENGTH_LONG)
                        toast.show()
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

                lw.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    /*val intent = VLCPlayerActivity.newIntent(context, view.channelURL.text.toString() )
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                    Log.d("Channel URL", view.channelURL.text.toString())
                    context.startActivity(intent)*/

                    for(fragment in mainActivity.supportFragmentManager.fragments){
                        mainActivity.supportFragmentManager.beginTransaction().remove(fragment).commit()
                    }
                    mainActivity.app_drawer.closeDrawer(GravityCompat.START)

                    val fragment = VLCPlayerActivity()
                    val bundle = Bundle()
                    bundle.putString(VLCPlayerActivity.LOCATION, view.channelURL.text.toString())
                    fragment.arguments = bundle
                    mainActivity.supportFragmentManager.beginTransaction().add(R.id.player_fragment, fragment).commit()
                }
            }
        }
    }
}

//
//import android.support.v7.app.AppCompatActivity
//import android.os.Bundle
//import kotlinx.android.synthetic.main.activity_main_1.*
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main_1)
//
//        // Example of a call to a native method
//        sample_text.text = stringFromJNI()
//    }
//
//    /**
//     * A native method that is implemented by the 'native-lib' native library,
//     * which is packaged with this application.
//     */
//    external fun stringFromJNI(): String
//
//    companion object {
//
//        // Used to load the 'native-lib' library on application startup.
//        init {
//            System.loadLibrary("native-lib")
//        }
//    }
//}
