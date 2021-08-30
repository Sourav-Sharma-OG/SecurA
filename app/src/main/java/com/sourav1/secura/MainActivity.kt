package com.sourav1.secura

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sourav1.secura.auth.LoginActivity
import com.sourav1.secura.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //View Binding
    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //Action Bar
    private lateinit var actionBar: ActionBar

    //FirebaseAuth and Firestore and DocumentReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference

    //UserName and UserId
    private var userName = ""

    private var userEmail = ""
    private var userId = ""
    private lateinit var imageSlider: ImageSlider

    companion object{
        var userNumber:String? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configure ActionBar.
        actionBar = supportActionBar!!
        actionBar.title = "SecurA"
        actionBar.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_edt1))

        //init firebase auth and Firestore and DocumentReference
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //init user id and user name
//        userId = firebaseAuth.currentUser!!.uid

        //init fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        checkUser()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutBtn -> logoutUser()

            R.id.profileMenuBtn ->{
                val intent = Intent(applicationContext, Profile::class.java)
                intent.putExtra("Name", userName)
                intent.putExtra("Number", userNumber)
                intent.putExtra("Email", userEmail)
                startActivity(intent)
            }
        }
        return true
    }

    private fun logoutUser() {
        firebaseAuth.signOut()
        checkUser()
    }

    private fun checkUser() {
        //Check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //User is not null, means user is logged in.
            showMainData()
        } else {
            //User is null, user is not logged in
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showMainData(){
        val firebaseUser = firebaseAuth.currentUser
        userId = firebaseUser!!.uid
        documentReference = firestore.collection("users").document(userId)
        documentReference.get().addOnSuccessListener { document ->
            if (document != null) {
                Log.d("TAG", "Name is ${document.getString("Name").toString()}")

                //Initializing the username, email and number
                userName = document.getString("Name").toString()
                userNumber = document.getString("Number").toString()
                userEmail = document.getString("Email").toString()

                binding.userName.text = userName

                //Setting up the slider
                imageSlider = binding.imageSlider
                val imageList = getImageList()

                imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)

                binding.helpBtn.setOnClickListener {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        val intent = Intent(applicationContext, MapsActivity::class.java)
                        startActivity(intent)

                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            0
                        )
                    }
                }
            } else {
                Toast.makeText(this, "$document does not exits....", Toast.LENGTH_SHORT).show()
            }
        }
    }





    private fun getImageList(): List<SlideModel> {
        val imageList = ArrayList<SlideModel>()

        imageList.add(
            SlideModel(
                "https://www.childlineindia.org/images/white-green.png",
                "Child Helpline"
            )
        )
        imageList.add(
            SlideModel(
                "https://4.bp.blogspot.com/-w_e8MbRa-f8/WWxpe6p4t-I/AAAAAAAABa4/2anViCGCo7AYAza9vC83Hsq6ss_AZW9VwCLcBGAs/s1600/" +
                        "181-Women-helpline.png", "Women Helpline"
            )
        )
        imageList.add(
            SlideModel(
                "https://www.emri.in/wp-content/uploads/2017/03/dail-100-logo.png",
                "Police Helpline"
            )
        )
        imageList.add(
            SlideModel(
                "https://play-lh.googleusercontent.com/BV9H80cTbvEMvMJ7OIN7mYavTBwm7jDJogOuMMnfUDpxDFw-3iwAdIttCrE75nf_tuk",
                "Medical Helpline"
            )
        )
        return imageList
    }
}