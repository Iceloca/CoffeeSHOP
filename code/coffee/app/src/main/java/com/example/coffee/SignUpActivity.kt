package com.example.coffee

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffee.databinding.ActivitySignInBinding
import com.example.coffee.databinding.ActivitySignUpBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.goUserSignInTxt.setOnClickListener {
            val intent = Intent(this@SignUpActivity,SignInActivity :: class.java)
            startActivity(intent)
            finish()
        }

        binding.signUpBtn.setOnClickListener {
            val email = binding.UserEmail.text.toString()
            val password = binding.UserPassword.text.toString()
            println(email)
            CoroutineScope(Dispatchers.Main).launch {
                val isSuccessful = sendPostRequest(email, password)
                if (isSuccessful) {
                    val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("email", email)
                    editor.apply()

                    val intent = Intent(this@SignUpActivity, MainMenuActivity::class.java)
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
        val mURL = URL("http://10.0.2.2:8080/auth/register")  // URL сервера для POST-запроса

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