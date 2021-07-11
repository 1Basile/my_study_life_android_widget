package com.example.myapplication

import DatabaseHandler
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val TOKEN_FIELD = findViewById<EditText>(R.id.msl_token)
        TOKEN_FIELD.setText(DatabaseHandler(this).viewToken())

        server_api_headers["Accept"] = "application/json"
        server_api_headers["Authorization"] = "Bearer ${TOKEN_FIELD.text.toString()}"
        
        // set some startup parameters
        val bundle = intent.extras
        val ifToHide = bundle?.getString("ifToHide", "false")

        val showHideBtn = findViewById<Button>(R.id.showHideBtn)
        val confirmTokenButton = findViewById<Button>(R.id.confirm_token_button)
        val mslTokenInputField = findViewById<EditText>(R.id.msl_token)


        // setup config params
        showHideBtn.setOnClickListener {
            if (showHideBtn.text.toString().equals("Show")) {
                mslTokenInputField.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showHideBtn.text = "Hide"
            } else {
                mslTokenInputField.transformationMethod = PasswordTransformationMethod.getInstance()
                showHideBtn.text = "Show"
            }
        }

        confirmTokenButton.setOnClickListener {
            val success = DatabaseHandler(this).updateToken(TOKEN_FIELD.text.toString())
            Toast.makeText(this, "Token changed $success", Toast.LENGTH_SHORT).show()
            server_api_headers["Authorization"] = "Bearer ${TOKEN_FIELD.text.toString()}"

            confirmTokenButton.text = "Done"
            Timer("SettingUp", false).schedule(500) {
                confirmTokenButton.text = "Confirm"
            }
        }

        // if start program in hide mode
        if (ifToHide == "true") {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(startMain)
        }
    }
}
