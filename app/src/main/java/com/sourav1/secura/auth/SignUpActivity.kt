package com.sourav1.secura.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sourav1.secura.MainActivity
import com.sourav1.secura.R
import com.sourav1.secura.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding:ActivitySignUpBinding

    //Action Bar
    private lateinit var actionBar: ActionBar

    //Progress Dialog
    private lateinit var progressDialog:ProgressDialog

    //Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    //Firestore
    private lateinit var firestore: FirebaseFirestore

    //Password and Email varibles && Number and Name and UserId
    private var email = ""
    private var password = ""
    private var name = ""
    private var number = ""
    private var userId = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configure ActionBar
        actionBar = supportActionBar!!
        actionBar.title = "Sign Up"
        actionBar.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_edt1))
        //Enable back button
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //Configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Creating Account...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init Firestore
        firestore = FirebaseFirestore.getInstance()

        //Handle click on SignUp Button
        binding.signUp.setOnClickListener {
            //Validate Date Entered by the User
            validateData()
        }

    }

    private fun validateData() {
        //Fetching the data from the Layout
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //Validating the data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //Invalid email format
            binding.emailEt.error = "Invalid email format!.."
        }
        else if(TextUtils.isEmpty(password)){
            //Password is not entered.
            binding.passwordEt.error = "Please enter password!.."
        }
        else if(password.length < 6){
            //Password length is less than 6
            binding.passwordEt.error = "Password must be 6 characters long"
        }
        else{
            //Data is valid, Continue with SignUp Process
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        //Show Progress
        progressDialog.show()

        //Create Account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //SignUp Successful
                progressDialog.dismiss()

                //Get Current User
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser?.email
                Toast.makeText(this, "Account Created with email $email", Toast.LENGTH_SHORT).show()
                name = binding.nameEt.text.toString().trim()
                number = binding.numberEt.text.toString().trim()
                userId = firebaseAuth.currentUser!!.uid

                Log.i("Important", "User id is: $userId")

                //Store the user name and number into the database.
                pushNameNumber()

                //Open Main Activity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener{
                //SignUp Failed
                progressDialog.dismiss()
                Toast.makeText(this, "SignUp failed due to ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //Pushing user name and number into the database.
    private fun pushNameNumber() {
        val documentReference: DocumentReference = firestore.collection("users").document(userId)
        val user: HashMap<String, String> = HashMap()
        user["Name"] = name
        user["Number"] = number
        user["Email"] = email

        documentReference.set(user).addOnSuccessListener {
            Log.d("Firestore:", "User created successfully with userId: $userId")
        }
            .addOnFailureListener{
                Log.d("Firestore: ", "User not created due to: " + it.message)
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        //Go back to previous activity, when the back button of action bar is pressed
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}