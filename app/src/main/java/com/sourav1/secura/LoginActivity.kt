package com.sourav1.secura

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.sourav1.secura.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    //Binding
    private lateinit var binding:ActivityLoginBinding

    //Action Bar
    private lateinit var actionBar: ActionBar

    //Progress Dialog
    private lateinit var progressDialog:ProgressDialog

    //Firebase Authentication
    private lateinit var firebaseAuth:FirebaseAuth

    //Email & Password
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configure Action Bar
        actionBar = supportActionBar!!
        actionBar.title = "Login"
        actionBar.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_edt1))

        //Configure ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging in...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()


        //handle click, Not have account, SignUp? TV
        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }


        //handle click, Login Button
        binding.loginBtn.setOnClickListener {
            //Before Logging in Validating Data.
            validateData()
        }

    }

    private fun validateData() {
        //Fetching data from the Layout.
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //Validating Data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //Invalid email format
            binding.emailEt.error = "Invalid email format!.."
        }
        else if(TextUtils.isEmpty(password)){
            //No password Entered
            binding.passwordEt.error = "Please enter password!.."
        }
        else{
            //Data is validated, Begin Login Process..
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {
        //Show Progress
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //Login Success
                progressDialog.dismiss()

                //Get the user information
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Logged in as $email", Toast.LENGTH_SHORT).show()

                //Opening the Main Activity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener{
                //Login Failed, Dismiss the ProgressDialog
                progressDialog.dismiss()
                Toast.makeText(this, "Login falied due to ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        //User is already Logged in, go to profile activity
        //get current user

        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            //User is already logged in
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}