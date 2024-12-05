package com.example.coffee

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.coffee.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding
    private var startX = 0f
    private var startY = 0f
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setOnTouchListener { _, event ->
            handleTouch(event)
        }


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
                    isSwipeRight(startX, endX) -> {
                        handleSwipeRight()
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
        val intent = Intent(this@MainMenuActivity, StartActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleSwipeRight() {
        // Действие на свайп вправо
        val intent = Intent(this@MainMenuActivity, SaidMenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleSwipeLeft() {
        // Действие на свайп влево
        val intent = Intent(this@MainMenuActivity, SupportActivity::class.java)
        startActivity(intent)
        finish()
    }
    // Проверки направлений свайпа
    private fun isSwipeDown(startY: Float, endY: Float): Boolean {
        return (endY - startY > 40) // Минимальная длина свайпа вниз
    }

    private fun isSwipeRight(startX: Float, endX: Float): Boolean {
        return (endX - startX > 40) // Минимальная длина свайпа вправо
    }

    private fun isSwipeLeft(startX: Float, endX: Float): Boolean {
        return (startX - endX > 40) // Минимальная длина свайпа влево
    }
}