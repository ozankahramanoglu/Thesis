package com.example.thesis

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.app_bar_main_page.*
import kotlinx.android.synthetic.main.content_main_page.*

class MainPage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val addbutton = findViewById<ImageView>(R.id.imageView2)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        val navigationView : NavigationView  = findViewById(R.id.nav_view)
        val headerView : View = navigationView.getHeaderView(0)
        val navUsername : TextView = headerView.findViewById(R.id.textView2)
        val navPhoto : ImageView = headerView.findViewById(R.id.imageView)

        val authUser = FirebaseAuth.getInstance().currentUser

        navUsername.text = authUser?.displayName.toString()

        Glide.with(this).load(authUser?.photoUrl.toString()).into(navPhoto)

        layoutManager = LinearLayoutManager(this)
        rv.layoutManager = layoutManager

        adapter = RecyclerAdapter()
        rv.adapter = adapter


        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, profilePage::class.java)
                startActivity(intent)
            }
            R.id.nav_main_page -> {
                mainPage()
            }
            R.id.nav_food -> {
                food()
            }
            R.id.nav_tools -> {
                barcodeReader()
            }
            R.id.nav_step_counter -> {
                stepActivityCounter()
            }
            R.id.nav_log_out -> {
                logOut()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    fun logOut(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun barcodeReader(){
        val intent = Intent(this, BarcodeReader::class.java)
        startActivity(intent)
    }
    fun stepActivityCounter(){
        val intent = Intent(this, stepCounter::class.java)
        startActivity(intent)
    }
    fun food(){
        val intent = Intent(this, NutritiousActivity::class.java)
        startActivity(intent)
    }
    fun mainPage(){
        val intent = Intent(this, MainPage::class.java)
        startActivity(intent)
    }
}



