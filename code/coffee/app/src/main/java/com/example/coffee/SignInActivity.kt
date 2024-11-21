package com.example.coffee

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffee.databinding.ActivitySignInBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goUserSignUpBtn.setOnClickListener {
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signInBtn.setOnClickListener {
            val email = binding.UserEmail.text.toString()
            val password = binding.UserPassword.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                val isSuccessful = sendPostRequest(email, password)
                if (isSuccessful) {
                    val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("email", email)
                    editor.apply()
                    val intent = Intent(this@SignInActivity, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Handle unsuccessful login here
                    binding.textError.setText("Incorrect data")
                    println("Login failed")
                }
            }
        }
    }
    suspend fun sendPostRequest(userName: String, password: String): Boolean {
        val mURL = URL("http://10.0.2.2:8080/auth/login")  // URL сервера для POST-запроса

        val params = """
        {
            "email": "$userName",
            "password": "$password"
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
}