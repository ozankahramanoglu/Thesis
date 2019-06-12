package com.example.thesis

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.IntegerArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_personal.*
import kotlinx.android.synthetic.main.app_bar_personal.*
import kotlinx.android.synthetic.main.content_personal.*
import java.util.*
import android.widget.Toast
import android.view.View.OnFocusChangeListener
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import javax.xml.datatype.DatatypeConstants.MONTHS
import kotlin.math.log


class personal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
//            val infoResult = saveData()
//            Snackbar.make(view, infoResult, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            saveData()
        }

        birthDateInput.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)


                val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { personal, year, monthOfYear, dayOfMonth ->

                    var a = "$dayOfMonth $month, $year"
                    birthDateInput.setText (a)
                }, year, month, day)

                dpd.show()
            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val authUser = FirebaseAuth.getInstance().currentUser

        val navigationView : NavigationView  = findViewById(R.id.nav_view)
        val headerView : View = navigationView.getHeaderView(0)
        val navUsername : TextView = headerView.findViewById(R.id.textView2)
        val navPhoto : ImageView = headerView.findViewById(R.id.imageView)

        navUsername.text = authUser?.displayName.toString()
        Glide.with(this).load(authUser?.photoUrl.toString()).into(navPhoto)

        val spinner: Spinner = findViewById(R.id.alcohol_freq_array)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.alcohol_freq_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        val spinner2: Spinner = findViewById(R.id.tobacco_freq_array)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.tobacco_freq_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner2.adapter = adapter
        }
        val spinner3: Spinner = findViewById(R.id.gender_array)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner3.adapter = adapter
        }

    }


    fun saveData():String{
        val myDatabase = FirebaseDatabase.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        myDatabase.getReference("users").child(user?.uid.toString()).child("BirthDate").setValue(birthDateInput.text.toString())

        if (weightInput.text.toString().toInt() in 0..800 ){
            myDatabase.getReference("users").child(user?.uid.toString()).child("Weight").setValue(weightInput.text.toString())
        }
        else{
            weightInput.text = null
            return "You can not be under 0 kg or above 800 kg"
        }
        if(heightInput.text.toString().toInt() in 50..251 ){
            myDatabase.getReference("users").child(user?.uid.toString()).child("Height").setValue(heightInput.text.toString())
        }
        else{
            heightInput.text = null
            return "You can not be under 50cm or above 251 cm"
        }



        myDatabase.getReference("users").child(user?.uid.toString()).child("Gender").setValue(gender_array.selectedItem.toString())
        myDatabase.getReference("users").child(user?.uid.toString()).child("Tobacco").setValue(tobacco_freq_array.selectedItem.toString())
        myDatabase.getReference("users").child(user?.uid.toString()).child("Alcohol").setValue(alcohol_freq_array.selectedItem.toString())
        myDatabase.getReference("users").child(user?.uid.toString()).child("Name").setValue(user?.displayName)
        myDatabase.getReference("users").child(user?.uid.toString()).child("Email").setValue(user?.email)
        myDatabase.getReference("users").child(user?.uid.toString()).child("PhotoURI").setValue(user?.photoUrl.toString())

        val intent = Intent(this, profilePage::class.java)
        startActivity(intent)

        return "Your data has saved"
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.personal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
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
