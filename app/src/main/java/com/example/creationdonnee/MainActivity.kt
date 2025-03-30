package com.example.creationdonnee

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_main)
        val newInscButton = findViewById<Button>(R.id.inscription_button)
        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener(buttonListener)
        newInscButton.setOnClickListener(buttonListener)
    }
    val buttonListener = View.OnClickListener { view ->
        when(view.id){
            R.id.inscription_button -> switchToFragment()
            R.id.login_button -> switchActivity(ConnexionActivity::class.java)
        }
    }

    fun switchActivity(activityName: Class<out AppCompatActivity>){
        val intent = Intent(this,activityName)
        startActivity(intent)
    }

    fun switchToFragment(){
        val fragment: InscriptionFragment = InscriptionFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_main, fragment).addToBackStack(null)
        transaction.commit()

    }
}