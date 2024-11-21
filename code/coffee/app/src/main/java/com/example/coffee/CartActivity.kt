package com.example.coffee

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffee.databinding.ActivityCartBinding
import com.example.coffee.databinding.ActivityOrdersBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CartActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        var price = 0;
        setContentView(binding.root)

        binding.imageView3.setOnClickListener{
            val intent = Intent(this@CartActivity, SaidMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.buyBtn.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val products = mapOf(
                "cappuccino" to sharedPreferences.getInt("cappuccino", 0),
                "latte" to sharedPreferences.getInt("latte", 0),
                "espresso" to sharedPreferences.getInt("espresso", 0),
                "tea" to sharedPreferences.getInt("tea", 0)
            )



            // Формируем заказ
            var ordersText = ""
            var totalSum = price

            for ((product, count) in products) {
                if (count > 0) {
                    ordersText += "$count x $product" +'\n'.toString()

                }
            }

            if (ordersText.isEmpty()) {
                Toast.makeText(this@CartActivity, "Нет продуктов в заказе", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Удаляем последний символ переноса строки
            ordersText = ordersText.trim()

            // Запускаем корутину для отправки запроса
            CoroutineScope(Dispatchers.Main).launch {
                val email = sharedPreferences.getString("email","test@gmail.com").toString()
                val status = "cooking" // Статус заказа

                // Отправка POST-запроса
                val isSuccessful = sendOrderRequest(email, status, totalSum, ordersText)

                if (isSuccessful) {
                    Toast.makeText(this@CartActivity, "Order sent successfully!", Toast.LENGTH_SHORT).show()

                    // Обнуляем SharedPreferences после успешного заказа
                    val editor = sharedPreferences.edit()
                    for (product in products.keys) {
                        editor.putInt(product, 0)
                    }
                    editor.apply()
                    recreate()
                } else {
                    Toast.makeText(this@CartActivity, "Failed to send order.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Получение SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val allProducts = mapOf(
            "cappuccino" to sharedPreferences.getInt("cappuccino", 0),
            "latte" to sharedPreferences.getInt("latte", 0),
            "espresso" to sharedPreferences.getInt("espresso", 0),
            "tea" to sharedPreferences.getInt("tea", 0)
        )
        allProducts.forEach{(productName, quantity) ->
            when (productName) {
                "cappuccino" -> price += 7 * quantity
                "latte" -> price += 7 * quantity
                "espresso" -> price += 6 * quantity
                "tea" -> price += 5 * quantity
                else -> println("Продукт неизвестен")
            }
            binding.textView5.setText("$price BYN")
        }

        // Фильтруем продукты с количеством больше 0
        val filteredProducts = allProducts.filter { it.value > 0 }

        // Контейнер для динамических элементов
        val mainLayout = binding.root.findViewById<ConstraintLayout>(R.id.main)

        // Создаем элементы для каждого продукта
        var previousViewId = R.id.imageView // Начинаем добавлять элементы под логотипом
        filteredProducts.forEach { (productName, quantity) ->
            // Создаем TextView для отображения продукта и его количества
            val productTextView = TextView(this).apply {
                text = "$productName: $quantity"
                id = View.generateViewId()
                textSize = 38f
                setTextColor(Color.BLACK)
            }
            mainLayout.addView(productTextView)

            // Создаем кнопку для сброса количества
            val resetButton = Button(this).apply {
                text = "DELETE"
                id = View.generateViewId()
            }
            mainLayout.addView(resetButton)

            // Настраиваем ограничения для TextView
            val textViewConstraint = ConstraintSet().apply {
                clone(mainLayout)
                connect(productTextView.id, ConstraintSet.TOP, previousViewId, ConstraintSet.BOTTOM, 16)
                connect(productTextView.id, ConstraintSet.START, R.id.main, ConstraintSet.START, 16)
                applyTo(mainLayout)
            }

            // Настраиваем ограничения для кнопки
            val buttonConstraint = ConstraintSet().apply {
                clone(mainLayout)
                connect(resetButton.id, ConstraintSet.TOP, productTextView.id, ConstraintSet.BOTTOM, 8)
                connect(resetButton.id, ConstraintSet.START, R.id.main, ConstraintSet.START, 16)
                applyTo(mainLayout)
            }

            // Обработка нажатия кнопки для сброса
            resetButton.setOnClickListener {
                val editor = sharedPreferences.edit()
                editor.putInt(productName, 0)
                editor.apply()
                recreate() // Перезагружаем активность для обновления интерфейса
            }

            // Обновляем предыдущий View для следующего элемента
            previousViewId = resetButton.id
        }
    }
    suspend fun sendOrderRequest(email: String, status: String, sum: Int, ordersText: String): Boolean {
        val url = URL("http://10.0.2.2:8080/order/save") // URL вашего сервера

        // Формируем тело запроса в формате JSON
        val params = """
        {
            "email": "$email",
            "status": "$status",
            "sum": $sum,
            "orders_text": "$ordersText"
        }
    """.trimIndent()

        return withContext(Dispatchers.IO) {
            try {
                // Открываем соединение
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST" // Указываем метод POST
                    doOutput = true        // Разрешаем отправку данных
                    setRequestProperty(
                        "Content-Type",
                        "application/json"
                    ) // Устанавливаем заголовок

                    // Отправляем параметры в теле запроса
                    outputStream.write(params.toByteArray(Charsets.UTF_8))
                    outputStream.flush()
                    outputStream.close()

                    // Проверяем успешный ответ
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        println("Request was successful")
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            val response = StringBuilder()
                            var inputLine = reader.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = reader.readLine()
                            }
                            println("Response: $response") // Выводим ответ сервера
                        }
                        true
                    } else {
                        println("Server returned non-OK code: $responseCode")
                        false
                    }
                }
            } catch (e: Exception) {
                println("Exception occurred: ${e.message}")
                e.printStackTrace() // Печать стека ошибок для отладки
                false
            }
        }
    }
}