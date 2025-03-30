package com.example.creationdonnee

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ConnexionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_connexion)
        val loginUser = findViewById<EditText>(R.id.login_user)
        val loginMdp = findViewById<EditText>(R.id.login_mdp)
        val loginButton = findViewById<Button>(R.id.login_button_submit)

        loginButton.setOnClickListener {
            val username = loginUser.text.toString()
            val password = loginMdp.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Nom d'utilisateur et mot de passe requis", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val dbHelper = DatabaseHelper(this)
                val user = dbHelper.getUserByUsernameOrEmail(username)

                if (user != null && user.password == password) {
                    val intent = Intent(this, PlaningActivity::class.java)
                    val userId = dbHelper.getUserIdByUsernameOrEmail(user.username)
                    intent.putExtra("USER_ID", userId)

                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Nom d'utilisateur ou mot de passe incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


}