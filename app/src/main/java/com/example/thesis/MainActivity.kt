package com.example.thesis

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*

import com.google.android.gms.tasks.Task

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp

import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var FDatabase: FirebaseDatabase
    private var TAG = "GoogleActivity"
    val RC_SIGN_IN: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        this.auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestId()
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        sign_in_button.setOnClickListener {
            view: View? -> signIn()
        }
    }


    public override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, profilePage::class.java)
            startActivity(intent)
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }


    private fun handleResult(completedTask : Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
            updateUI()
        }
        catch (e: ApiException){
            Toast.makeText(this,e.toString(), Toast.LENGTH_LONG).show()
        }
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = getCredential(acct.idToken, null)
        var firebaseAuth1 = FirebaseAuth.getInstance()
        firebaseAuth1.signInWithCredential(credential).addOnCompleteListener {
        }

    }

    private fun signIn() {

        val signInIntent = this.googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }


    private fun updateUI(){

        val myDatabase = FirebaseDatabase.getInstance()
        var user = FirebaseAuth.getInstance().currentUser

//        myDatabase.getReference("users").child(user?.uid.toString()).setValue(account.id)
//        myDatabase.getReference("users").child(user?.uid.toString()).child("Name").setValue(account.displayName)
//        myDatabase.getReference("users").child(user?.uid.toString()).child("Email").setValue(account.email)
//        myDatabase.getReference("users").child(user?.uid.toString()).child("PhotoURI").setValue(account.photoUrl.toString())

        if ((myDatabase.getReference("users").child(user?.uid.toString()).child("Gender").toString() == "") ||
            (myDatabase.getReference("users").child(user?.uid.toString()).child("Tobacco").toString()== "") ||
            (myDatabase.getReference("users").child(user?.uid.toString()).child("Alcohol").toString()== "") ) {
            val intent = Intent(this, personal::class.java)
            startActivity(intent)
        }
        else{
            val intent = Intent(this, profilePage::class.java)
            startActivity(intent)
        }

    }
}
