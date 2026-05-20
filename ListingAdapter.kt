package com.example.gabsstudentstay.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gabsstudentstay.R
import com.example.gabsstudentstay.data.local.entity.ListingEntity
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat
import java.util.Locale

class ListingAdapter(
    private val onItemClick: (ListingEntity) -> Unit
) : RecyclerView.Adapter<ListingAdapter.ListingViewHolder>() {

    private val items = mutableListOf<ListingEntity>()
    private val currencyFormatter = NumberFormat.getNumberInstance(Locale.US)

    fun submitList(listings: List<ListingEntity>) {
        items.clear()
        items.addAll(listings)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_listing, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.cardListing)
        private val image: ImageView = itemView.findViewById(R.id.ivListingImage)
        private val statusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        private val title: TextView = itemView.findViewById(R.id.tvListingTitle)
        private val price: TextView = itemView.findViewById(R.id.tvListingPrice)
        private val locationType: TextView = itemView.findViewById(R.id.tvLocationType)
        private val availability: TextView = itemView.findViewById(R.id.tvAvailability)
        private val deposit: TextView = itemView.findViewById(R.id.tvDeposit)
        private val amenities: TextView = itemView.findViewById(R.id.tvAmenities)
        private val campusDistance: TextView = itemView.findViewById(R.id.tvCampusDistance)

        fun bind(listing: ListingEntity) {
            val context = itemView.context
            title.text = listing.title
            price.text = context.getString(
                R.string.listing_price_format,
                currencyFormatter.format(listing.price)
            )
            locationType.text = context.getString(
                R.string.listing_location_type,
                listing.location,
                listing.roomType
            )
            availability.text = context.getString(
                R.string.listing_available_from,
                listing.availabilityDate
            )
            deposit.text = context.getString(
                R.string.listing_deposit_format,
                currencyFormatter.format(listing.depositAmount)
            )
            amenities.text = context.getString(
                R.string.listing_amenities_prefix,
                listing.amenities
            )
            campusDistance.text = context.getString(
                R.string.listing_distance_campus,
                listing.distanceFromCampusKm,
                listing.campusName
            )

            val isReserved = listing.status.equals("Reserved", ignoreCase = true)
            statusBadge.text = if (isReserved) {
                context.getString(R.string.listing_status_reserved)
            } else {
                context.getString(R.string.listing_status_available)
            }
            statusBadge.setBackgroundResource(
                if (isReserved) R.drawable.bg_status_reserved else R.drawable.bg_status_available
            )
            statusBadge.setTextColor(
                context.getColor(
                    if (isReserved) R.color.gs_reserved_text else R.color.gs_secondary
                )
            )
            card.alpha = if (isReserved) 0.78f else 1f

            val imageResId = context.resources.getIdentifier(
                listing.imageName,
                "drawable",
                context.packageName
            )

            Glide.with(context)
                .load(if (imageResId != 0) imageResId else R.drawable.bg_listing_placeholder)
                .placeholder(R.drawable.bg_listing_placeholder)
                .error(R.drawable.bg_listing_placeholder)
                .into(image)

            itemView.setOnClickListener { onItemClick(listing) }
            card.setOnClickListener { onItemClick(listing) }
        }
    }
}
