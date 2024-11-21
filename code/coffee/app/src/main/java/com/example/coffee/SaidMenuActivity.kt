package com.example.coffee

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffee.databinding.ActivitySaidMenuBinding

class SaidMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaidMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaidMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewMainMenu.setOnClickListener{
            val intent = Intent(this@SaidMenuActivity, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.textViewSupport.setOnClickListener{
            val intent = Intent(this@SaidMenuActivity, SupportActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.textViewCart.setOnClickListener{
            val intent = Intent(this@SaidMenuActivity, CartActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.textViewOrders.setOnClickListener{
            val intent = Intent(this@SaidMenuActivity, OrdersActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.textViewExit.setOnClickListener{
            val intent = Intent(this@SaidMenuActivity, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}