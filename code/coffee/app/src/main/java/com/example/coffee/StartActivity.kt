package com.example.coffee

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffee.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
//    private lateinit var goSignInPage : AppCompatButton
    private lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goUserSignInBtn.setOnClickListener{
            val intent = Intent(this@StartActivity,SignInActivity :: class.java)
            startActivity(intent)
            finish()
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("cappuccino",0 )
        editor.putInt("latte",0 )
        editor.putInt("espresso",0 )
        editor.putInt("tea",0 )
        editor.apply()


    }
}