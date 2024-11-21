package com.example.coffee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(private val orders: List<OrdersActivity.Order>) :
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.orderId)
        val orderSum: TextView = itemView.findViewById(R.id.orderSum)
        val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        val orderDetails: TextView = itemView.findViewById(R.id.orderDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = "Order ID: ${order.id}"
        holder.orderSum.text = "Total: \$${order.sum}"
        holder.orderStatus.text = "Status: ${order.status}"
        holder.orderDetails.text = order.ordersText
    }

    override fun getItemCount(): Int = orders.size
}
