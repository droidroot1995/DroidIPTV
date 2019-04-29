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
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

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

        supportActionBar?.title = "DroidIPTV"
        supportActionBar?.setDisplayShowTitleEnabled(true)

        /*val actionBar = supportActionBar
        actionBar?.title = "DroidIPTV"
        actionBar?.setDisplayShowTitleEnabled(true)*/

        //actionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_CUSTOM)

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
    }
}
