package com.example.thesis

import android.content.Intent
import android.net.sip.SipSession
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.common.util.JsonUtils
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.content_profile_page.*
import kotlinx.android.synthetic.main.nav_header_personal.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL
import java.util.*

class profilePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        val authUser = FirebaseAuth.getInstance().currentUser

        val navigationView : NavigationView  = findViewById(R.id.nav_view)
        val headerView : View = navigationView.getHeaderView(0)
        val navUsername : TextView = headerView.findViewById(R.id.textView2)
        val navPhoto : ImageView = headerView.findViewById(R.id.imageView)

        val myDatabase = FirebaseDatabase.getInstance()


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                navUsername.text = dataSnapshot.child("users").child(authUser?.uid.toString()).child("Name").value.toString()
                nameBox.text = navUsername.text
                val photo = dataSnapshot.child("users").child(authUser?.uid.toString()).child("PhotoURI").value.toString()
                val weight = dataSnapshot.child("users").child(authUser?.uid.toString()).child("Weight").value.toString()
                val height = dataSnapshot.child("users").child(authUser?.uid.toString()).child("Height").value.toString()
                val gender = dataSnapshot.child("users").child(authUser?.uid.toString()).child("Gender").value.toString()
                val tobacco = dataSnapshot.child("users").child(authUser?.uid.toString()).child("Tobacco").value.toString()
                val alcohol = dataSnapshot.child("users").child(authUser?.uid.toString()).child("Alcohol").value.toString()
                val bDate = dataSnapshot.child("users").child(authUser?.uid.toString()).child("BirthDate").value.toString()

                editPhoto(photo,navPhoto,weight,height,gender,tobacco,alcohol,bDate)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("deneme", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        myDatabase.reference.addListenerForSingleValueEvent(postListener)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }
    fun editPhoto(photoURI:String,navPhoto:ImageView,weight:String,height:String,gender:String,tobacco:String,alcohol:String,bDate:String){

        Glide.with(this).load(photoURI).into(navPhoto)
        Glide.with(this).load(photoURI).into(imageViewPhoto)

        tobaccoBox.text = "Tobacco usage\n".plus(tobacco)
        weightBox.text = weight.plus(" kg")
        heightBox.text = height.plus(" cm")
        genderBox.text = gender
        alcoholBox.text = "Alcohol usage\n".plus(alcohol)
        bdayBox.text = bDate

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
        menuInflater.inflate(R.menu.profile_page, menu)
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
