package com.example.bruno_lipovac_rma

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bruno_lipovac_rma.models.Delivery

class DeliveryListAdapter():
    ListAdapter<Delivery, DeliveryListAdapter.ViewHolder>(DeliveryDiffCallback) {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val pickupTextView: TextView = view.findViewById(R.id.recycler_pickup_address)
        val deliveryTextView: TextView = view.findViewById(R.id.recycler_delivery_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.delivery_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val delivery = getItem(position)
        holder.pickupTextView.text = delivery.pickupAddress
        holder.deliveryTextView.text = delivery.deliverAddress
    }
}

object DeliveryDiffCallback: DiffUtil.ItemCallback<Delivery>() {
    override fun areItemsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
        return oldItem == newItem
    }
}
