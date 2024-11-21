package com.example.coffee

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffee.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView3.setOnClickListener {
            val intent = Intent(this@MainMenuActivity, SaidMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.espressoBtn.setOnClickListener{
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            var cnt = sharedPreferences.getInt("espresso",0)
            cnt = cnt + 1;
            editor.putInt("espresso",cnt )

            editor.apply()
        }
        binding.cappucinoBtn.setOnClickListener{
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            var cnt = sharedPreferences.getInt("cappuccino",0)
            cnt = cnt + 1;
            editor.putInt("cappuccino",cnt )

            editor.apply()
        }

        binding.latteBtn.setOnClickListener{
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            var cnt = sharedPreferences.getInt("latte",0)
            cnt = cnt + 1;
            editor.putInt("latte",cnt )

            editor.apply()
        }

        binding.teaBtn.setOnClickListener{
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            var cnt = sharedPreferences.getInt("tea",0)
            cnt = cnt + 1;
            editor.putInt("tea",cnt )
            println(cnt)
            editor.apply()
        }


    }
}