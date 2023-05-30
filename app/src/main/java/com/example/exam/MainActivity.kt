package com.example.exam


import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EdgeEffect
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    lateinit var uriImageUser:Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        uriImageUser=Uri.parse("android.resource://" + packageName + "/" + R.drawable.icone_user)
    }



    fun creatUser(view: View) {
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.motdepasse)
        val passworConfirmation = findViewById<EditText>(R.id.motdepasseconfirm)
        val NomPrenom=findViewById<EditText>(R.id.nomPrenom)
        val ville=findViewById<Spinner>(R.id.ville)
        val user =User(NomPrenom.text.toString(),ville.selectedItem.toString())
        val valider = validerUser(email,password,passworConfirmation)

        //firebase
        if(validerUser(email,password,passworConfirmation)){
            //Création d'un instance de Firebase authentification
            val auth= Firebase.auth
            //Créer un user (pour s’authentifier utiliser la fonction signInWithEmailAndPassword
            auth.createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    //instance de firbase realtime
                    val dbReference = FirebaseDatabase.getInstance().getReference("Utilisateurs")
                    dbReference.child(auth.currentUser?.uid.toString()).setValue(user)

                    //instance de firebase storage
                    var storageReference =
                        FirebaseStorage.getInstance().getReference(auth.currentUser?.uid.toString())
                    //Ecrire dans la base de données (Upload un fichier) exemple : une image dans les ressources
                    val uri = Uri.parse("android.resource://$packageName/${R.drawable.icone_user}")
                    storageReference.putFile(uri)

                    //intent
                    startActivity(Intent(this, Operation::class.java))
                } else
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG)
                        .show()//message d erreur de Firebase
            }
            //apres l ajout de lutilisateur si successful on pass a l autre activiter

        }
    }

    private fun validerUser(email: EditText, password: EditText, passworConfirmation: EditText): Boolean {
        var validation = true
        if(!Patterns.EMAIL_ADDRESS.matcher(email.text).matches()){
            validation = false
            email.error="format d'email incorrecte!"
        }
        if(password.text.toString()!=passworConfirmation.text.toString()){
            validation = false
            password.error="mot de passe n'est pas le meme'"
        }
        if(password.length()<6){
            validation=false
            password.error="Le mot de passe doit contenir au moins 6 caractères"
        }
        return validation
    }

    fun camera(view: View) {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 2)
        } else {
            // La permission n'est pas encore accordée, on va la redemander à nouveau
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
        }
    }
    fun parcourir(view: View){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 1)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) { uriImageUser= data.data!!
        }
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            val imageBitmap=data?.extras?.get("data") as Bitmap
            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val path = MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, "Titre de l'image", null)
            uriImageUser = Uri.parse(path)
        }
        findViewById<ImageView>(R.id.imageProfile).setImageURI(uriImageUser)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Vous pouvez exécuter le code qui nécessite cette permission
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, 2)
                } else {
                    // La permission a été refusée
                    Toast.makeText(
                        this,
                        "Vous devez nous donner l'accord pour utiliser votre caméra",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}