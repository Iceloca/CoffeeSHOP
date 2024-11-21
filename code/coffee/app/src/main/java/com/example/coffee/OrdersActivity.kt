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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee.databinding.ActivityOrdersBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class OrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Обработка нажатия на меню
        binding.imageView3.setOnClickListener {
            val intent = Intent(this@OrdersActivity, SaidMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ordersRecyclerView.setHasFixedSize(true)

        // Загрузка заказов
        loadOrders()


    }

    suspend fun fetchOrders(email: String): List<Order> {
        val url = URL("http://10.0.2.2:8080/order/get?email=$email") // Формируем URL с параметром email

        return withContext(Dispatchers.IO) {
            try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET" // Указываем метод запроса
                    connectTimeout = 5000 // Таймаут подключения
                    readTimeout = 5000    // Таймаут чтения

                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            val response = StringBuilder()
                            var inputLine = reader.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = reader.readLine()
                            }

                            // Обработка JSON-ответа
                            val jsonResponse = JSONObject(response.toString())
                            if (jsonResponse.getString("status") == "OK") {
                                val ordersArray = jsonResponse.getJSONArray("orders")
                                return@withContext parseOrders(ordersArray)
                            } else {
                                println("Server responded with an error status")
                                return@withContext emptyList<Order>()
                            }
                        }
                    } else {
                        println("Server returned non-OK code: $responseCode")
                        return@withContext emptyList<Order>()
                    }
                }
            } catch (e: Exception) {
                println("Exception occurred: ${e.message}")
                e.printStackTrace()
                emptyList<Order>()
            }
        }
    }
    private fun loadOrders() {
        CoroutineScope(Dispatchers.Main).launch {

            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val email = sharedPreferences.getString("email","test@gmail.com").toString()
            val orders = fetchOrders(email)
            if (orders.isNotEmpty()) {
                val ordersAdapter = OrdersAdapter(orders)
                binding.ordersRecyclerView.adapter = ordersAdapter
            } else {
                Toast.makeText(this@OrdersActivity, "No orders found.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Функция для парсинга массива заказов из JSON
    fun parseOrders(ordersArray: JSONArray): List<Order> {
        val orders = mutableListOf<Order>()
        for (i in 0 until ordersArray.length()) {
            val orderObject = ordersArray.getJSONObject(i)
            val order = Order(
                id = orderObject.getInt("id"),
                sum = orderObject.getInt("sum"),
                status = orderObject.getString("status"),
                ordersText = orderObject.getString("orders_text")
            )
            orders.add(order)
        }
        return orders
    }

    // Модель данных для заказа
    data class Order(
        val id: Int,
        val sum: Int,
        val status: String,
        val ordersText: String
    )
}
