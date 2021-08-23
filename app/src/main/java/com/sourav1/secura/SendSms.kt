package com.sourav1.secura

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sourav1.secura.databinding.ActivitySendSmsBinding

class SendSms : AppCompatActivity() {

    //FirebaseAuth and Firestore and DocumentReference
    private  var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private  var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var documentReference: DocumentReference
    private lateinit var binding:ActivitySendSmsBinding

    //Message and Number
    private var number = MainActivity.userNumber
    private var helpMessage = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendSmsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.textView2.text = "Emergency Number: ${number.toString()}"
        helpMessage = "Please Help -> My Location is:\n" +
                "${intent.getStringExtra("Address")} \n" +
                "${intent.getStringExtra("City")}, ${intent.getStringExtra("State")}\n" +
                "${intent.getStringExtra("Country")}, ${intent.getStringExtra("postalCode")}, ${intent.getStringExtra("knownName")}\n\n" +
                "http://maps.google.com/?q=<${MapsActivity.userLat}>,<${MapsActivity.userLng}>"

        binding.askForHelpBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                sendMessage()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.SEND_SMS),
                    2
                )
            }
        }
    }
    /**
     * Sending the message to the user.
     */

    private fun sendMessage(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            val subs:SubscriptionManager = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
//
//            if(subs != null) {
//
//                Log.d("Sim: ", "No. of sims: ${subs.activeSubscriptionInfoCountMax}")
//
//                if(subs.activeSubscriptionInfoCountMax > 1) {
//
//                    val smsManager1: SmsManager = SmsManager.getSmsManagerForSubscriptionId(0)
//                    val smsManager2: SmsManager = SmsManager.getSmsManagerForSubscriptionId(1)
//
//                    smsManager1.sendTextMessage(number, null, "Sim 1", null, null)
//                    smsManager2.sendTextMessage(number, null, "Sim 2", null, null)
//                }
//            }
//        }


        val smsIntent = Intent(Intent.ACTION_VIEW)
        smsIntent.setData(Uri.parse("smsto:" + number));
        smsIntent.putExtra("sms_body", helpMessage)
        startActivity(smsIntent)
    }


    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //User gives the permission
            sendMessage()
        }
        else{
            Toast.makeText(this, "You have not given the required permission to send the Help Message,So stay in danger", Toast.LENGTH_SHORT).show()
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}