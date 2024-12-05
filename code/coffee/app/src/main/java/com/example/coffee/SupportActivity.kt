package com.example.coffee

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffee.databinding.ActivitySupportBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SupportActivity : AppCompatActivity() {
    private var startX = 0f
    private var startY = 0f
    private lateinit var binding: ActivitySupportBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setOnTouchListener { _, event ->
            handleTouch(event)
        }

        binding.imageView3.setOnClickListener {
            val intent = Intent(this@SupportActivity, SaidMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.sendBtn.setOnClickListener {
            val message = binding.UserMessage.text.toString()
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

            val email = sharedPreferences.getString("email", "test@mail.com").toString()
            CoroutineScope(Dispatchers.Main).launch {
                if (sendPostRequest(email, message)){
                    binding.UserMessage.setText("")
                }
            }


        }
    }
    suspend fun sendPostRequest(userName: String, password: String): Boolean {
        val mURL = URL("http://10.0.2.2:8080/support")  // URL сервера для POST-запроса

        val params = """
        {
            "email": "$userName",
            "message": "$password"
        }
    """.trimIndent()
        return withContext(Dispatchers.IO) {
            try {
                // Открываем соединение
                with(mURL.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    doOutput = true  // Включаем возможность отправки данных
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded")


                    // Отправляем параметры в теле запроса
                    outputStream.write(params.toByteArray(Charsets.UTF_8))
                    outputStream.flush()
                    outputStream.close()
                    // Проверяем успешный ответ
                    val responseCode = this.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        println("Request was successful")
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            val response = StringBuilder()
                            var inputLine = reader.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = reader.readLine()
                            }
                            println("Response: $response")  // Выводим ответ сервера
                        }
                        true
                    } else {
                        println("Server returned non-OK code: $responseCode")
                        false
                    }

                }
            } catch (e: Exception) {
                println("Exception occurred: ${e.message}")
                e.printStackTrace()  // Печать стека ошибок для более подробной информации
                false
            }
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

                }
            }
        }
        return true
    }

    // Логика для каждого жеста
    private fun handleSwipeDown() {
        // Действие на свайп вниз
        val intent = Intent(this@SupportActivity, StartActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleSwipeRight() {
        // Действие на свайп вправо
        val intent = Intent(this@SupportActivity, SaidMenuActivity::class.java)
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


}