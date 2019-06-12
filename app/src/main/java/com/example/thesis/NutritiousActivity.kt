package com.example.thesis

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL
import kotlin.collections.ArrayList

class NutritiousActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var listMy : ListView
    lateinit var listofFood : ArrayList<String>
    lateinit var adapter : ArrayAdapter<String>
    lateinit var ingredientsofFood : ArrayList<String>
    lateinit var resultofAPI : String
    lateinit var bestText : String
    lateinit var bestText2 : String
    lateinit var popuptext : TextView
    lateinit var popuptext2 : TextView
    lateinit var lifestyleList : ArrayList<Set<String>>
    lateinit var nutritioneList : ArrayList<Set<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutritious_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        listMy = findViewById(R.id.foodList)
        val filter : EditText = findViewById(R.id.foodSearch)

        listofFood = ArrayList()
        ingredientsofFood = ArrayList()
        lifestyleList = ArrayList()
        nutritioneList = ArrayList()

        this.bestText = "No information found about this product"
        this.bestText2 = "No information found about this product"


        // Popup Menu implementation
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_menu,null)


        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }


        // If API level 23 or higher then execute the code
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Create a new slide animation for popup window enter transition
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.RIGHT
            popupWindow.exitTransition = slideOut

        }
        val necessarybutton = view.findViewById<Button>(R.id.buttonPopup)


        necessarybutton.setOnClickListener{
            popupWindow.dismiss()
        }

        popuptext = view.findViewById(R.id.popupText)
        popuptext2 = view.findViewById(R.id.popupText2)



        filter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {

                val a = "https://chompthis.com/api/product-search.php?token=1mkVvrJRP8R4OkOQi&name="
                val b = filter.text
                val c = a + b

                if (b.length > 5) {

                    doAsync {

                        listofFood.clear()
                        ingredientsofFood.clear()
                        this@NutritiousActivity.resultofAPI = URL(c).readText()
                        val jsono = JSONObject(this@NutritiousActivity.resultofAPI)
                        val jsonarr = jsono.get("products") as JSONObject
                        val jsonarrkey = JSONObject(jsonarr.toString()).keys()

                        for (i in jsonarrkey) {
                            //Log.w("deneme" , i)
                            val temp = (jsonarr.get(i.toString()) as JSONObject).get("name")
                            //Log.w("deneme", temp.toString())
                            this@NutritiousActivity.listofFood.add(temp.toString())
                            this@NutritiousActivity.ingredientsofFood.add(i)
                        }
                    }
                }
                Log.w("deneme",this@NutritiousActivity.listofFood.toString())
                Log.w("deneme",this@NutritiousActivity.ingredientsofFood.toString())
                Thread.sleep(200)
                adapter = ArrayAdapter(this@NutritiousActivity, android.R.layout.simple_list_item_1, listofFood)
                listMy.adapter = adapter
            }
        })



        //LISTVIEW ITEM CLICKED
        listMy.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                this@NutritiousActivity.bestText = ""
                this@NutritiousActivity.bestText2 = ""
                Toast.makeText(this@NutritiousActivity,this@NutritiousActivity.ingredientsofFood[adapter.getPosition(adapter.getItem(i)!!.toString())], Toast.LENGTH_SHORT).show()
                val jsono = JSONObject(this@NutritiousActivity.resultofAPI).get("products") as JSONObject
                val temp = jsono.get(this@NutritiousActivity.ingredientsofFood[adapter.getPosition(adapter.getItem(i)!!.toString())]) as JSONObject
                val details = temp.get("details") as JSONObject
                val name = details.get("name")
                val ingredients = details.get("ingredients")
                val lifestyle = (details.get("lifestyle_flagged_ingredients") as JSONObject).keys()

                for (key in lifestyle){
                    val temp = details.getJSONObject("lifestyle_flagged_ingredients") as JSONObject
                    val temp2 = temp.getJSONObject(key)
                    this@NutritiousActivity.lifestyleList.add(setOf("Life Style: ",temp2.get("lifestyle_name").toString()," - Ingredient: ",temp2.get("name").toString(),"\n",temp2.get("description").toString()))
                }
                this@NutritiousActivity.bestText = ""
                for (life in lifestyleList){
                    for (element in life){
                        this@NutritiousActivity.bestText += element
                    }
                    this@NutritiousActivity.bestText += "\n"
                }

                val nutrition = details.get("nutrition_label") as JSONObject
                val nutritioneKeys = nutrition.keys()
                for (nutritione in nutritioneKeys){
                    nutritioneList.add(setOf(   "Name: ",       (nutrition.get(nutritione) as JSONObject).get("name").toString(),
                                                "\nPer 100g: ",   (nutrition.get(nutritione) as JSONObject).get("per_100g").toString(), " ",
                                                                (nutrition.get(nutritione) as JSONObject).get("measurement").toString(),"\n" ))
                }
                bestText2 = ""
                for (textwow in nutritioneList){
                    for (wow in textwow){
                        bestText2 += wow
                    }
                }



                //this@NutritiousActivity.bestText = jsono.get(this@NutritiousActivity.ingredientsofFood[adapter.getPosition(adapter.getItem(i)!!.toString())]).toString()

                TransitionManager.beginDelayedTransition(findViewById(android.R.id.content))
                popupWindow.showAtLocation(
                    findViewById(android.R.id.content), // Location to display popup window
                    Gravity.CENTER, // Exact position of layout to display popup
                    0, // X offset
                    0 // Y offset
                )

                popuptext.text = this@NutritiousActivity.bestText
                popuptext2.text = this@NutritiousActivity.bestText2
            }
        }

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


//        doAsync {
//            val result = URL("https://chompthis.com/api/product-search.php?token=1mkVvrJRP8R4OkOQi&name=big mac").readText()
//            val jsono = JSONObject(result)
//            val jsonarr = jsono.get("products") as JSONObject
//            val jsonarrkey = JSONObject(jsonarr.toString()).keys()
//            for (i in jsonarrkey){
//                val temp = jsonarr.get(i.toString())
//                Log.w("deneme",temp.toString())
//            }
//        }

        navUsername.text = authUser?.displayName.toString()
        Glide.with(this).load(authUser?.photoUrl.toString()).into(navPhoto)

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
        menuInflater.inflate(R.menu.nutritious_activity, menu)
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
