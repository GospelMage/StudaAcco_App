package com.example.gabsstudentstay.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gabsstudentstay.R
import com.example.gabsstudentstay.data.local.entity.ListingEntity
import java.text.NumberFormat
import java.util.Locale

class AlertMatchAdapter(
    private val onItemClick: (ListingEntity) -> Unit
) : RecyclerView.Adapter<AlertMatchAdapter.AlertMatchViewHolder>() {

    private val items = mutableListOf<ListingEntity>()
    private val currencyFormatter = NumberFormat.getNumberInstance(Locale.US)

    fun submitList(listings: List<ListingEntity>) {
        items.clear()
        items.addAll(listings)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertMatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert_match, parent, false)
        return AlertMatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertMatchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class AlertMatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(listing: ListingEntity) {
            val context = itemView.context
            itemView.findViewById<TextView>(R.id.tvMatchTitle).text = listing.title
            itemView.findViewById<TextView>(R.id.tvMatchLocation).text = listing.location
            itemView.findViewById<TextView>(R.id.tvMatchPrice).text =
                context.getString(R.string.listing_price_format, currencyFormatter.format(listing.price))
            itemView.findViewById<TextView>(R.id.tvMatchAvailability).text =
                context.getString(R.string.listing_available_from, listing.availabilityDate)
            itemView.setOnClickListener { onItemClick(listing) }
        }
    }
}
