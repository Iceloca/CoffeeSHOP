package com.example.coffee

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffee.databinding.ActivitySaidMenuBinding

class SaidMenuActivity : AppCompatActivity() {
    private var startX = 0f
    private var startY = 0f
    private lateinit var binding: ActivitySaidMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaidMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setOnTouchListener { _, event ->
            handleTouch(event)
        }
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
    private fun handleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Запоминаем начальные координаты касания
                startX = event.x
                startY = event.y
            }

            MotionEvent.ACTION_UP -> {
                // Получаем конечные координаты касания
                val endX = event.x
                val endY = event.y

                // Определяем направление свайпа
                when {
                    isSwipeDown(startY, endY) -> {
                        handleSwipeDown()
                    }

                    isSwipeLeft(startX, endX) -> {
                        handleSwipeLeft()
                    }
                }
            }
        }
        return true
    }

    // Логика для каждого жеста
    private fun handleSwipeDown() {
        // Действие на свайп вниз
        val intent = Intent(this@SaidMenuActivity, StartActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun handleSwipeLeft() {
        // Действие на свайп влево
        val intent = Intent(this@SaidMenuActivity, SupportActivity::class.java)
        startActivity(intent)
        finish()
    }
    // Проверки направлений свайпа
    private fun isSwipeDown(startY: Float, endY: Float): Boolean {
        return (endY - startY > 40) // Минимальная длина свайпа вниз
    }


    private fun isSwipeLeft(startX: Float, endX: Float): Boolean {
        return (startX - endX > 40) // Минимальная длина свайпа влево
    }
}