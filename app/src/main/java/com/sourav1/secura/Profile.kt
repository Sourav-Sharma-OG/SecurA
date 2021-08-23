package com.sourav1.secura

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sourav1.secura.databinding.ActivityProfileBinding

class Profile : AppCompatActivity() {

    private lateinit var  binding:ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userNameEt.text = "Username: ${intent.getStringExtra("Name")}"
        binding.userNumberEt.text = "Number: ${intent.getStringExtra("Number")}"
        binding.userEmailEt.text = "Email: ${intent.getStringExtra("Email")}"

    }
}