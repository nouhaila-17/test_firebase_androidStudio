package com.example.exam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class Operation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operation)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_operations,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.seDeconnecter) {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Déconnexion")
            builder.setMessage("Voulez vous vraiment quitter?")
            builder.setPositiveButton("Oui") { dialog, which ->
                val auth= FirebaseAuth.getInstance()
                auth.signOut()
                startActivity(Intent(this,MainActivity::class.java))
            }
            builder.setNegativeButton("Non") { dialog, which ->
                // rien à faire
            }
            builder.show()

        }
        if (item.itemId == R.id.user) {
            startActivity(Intent(this,MainActivity::class.java))
        }
        return true }
}