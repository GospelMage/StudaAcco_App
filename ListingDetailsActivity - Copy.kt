package com.example.gabsstudentstay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.gabsstudentstay.data.local.AppDatabase
import com.example.gabsstudentstay.data.local.entity.ListingEntity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale

class ListingDetailsActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private val currencyFormatter = NumberFormat.getNumberInstance(Locale.US)
    private var currentStudentId: Int = 1
    private var currentStudentName: String = "Student"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listing_details)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailsRoot)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        database = AppDatabase.getDatabase(this)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        currentStudentId = intent.getIntExtra(EXTRA_STUDENT_ID, 1)
        currentStudentName = intent.getStringExtra(EXTRA_STUDENT_NAME) ?: "Student"

        val listingId = intent.getIntExtra(EXTRA_LISTING_ID, -1)
        if (listingId == -1) {
            Toast.makeText(this, R.string.details_missing_listing, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            val listing = withContext(Dispatchers.IO) {
                database.listingDao().getListingById(listingId)
            }

            if (listing == null) {
                Toast.makeText(
                    this@ListingDetailsActivity,
                    R.string.details_missing_listing,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                bindListing(listing)
            }
        }
    }

    private fun bindListing(listing: ListingEntity) {
        findViewById<TextView>(R.id.tvDetailTitle).text = listing.title
        findViewById<TextView>(R.id.tvDetailPrice).text =
            getString(R.string.listing_price_format, currencyFormatter.format(listing.price))
        findViewById<TextView>(R.id.tvDetailLocationType).text =
            getString(R.string.listing_location_type, listing.location, listing.roomType)
        findViewById<TextView>(R.id.tvDetailAvailability).text = listing.availabilityDate
        findViewById<TextView>(R.id.tvDetailDeposit).text =
            getString(R.string.listing_deposit_format, currencyFormatter.format(listing.depositAmount))
        findViewById<TextView>(R.id.tvDetailAmenities).text = listing.amenities
        findViewById<TextView>(R.id.tvProviderName).text = listing.providerName
        findViewById<TextView>(R.id.tvProviderPhone).text = listing.providerPhone
        findViewById<TextView>(R.id.tvCampusName).text = listing.campusName
        findViewById<TextView>(R.id.tvCampusDistance).text =
            getString(R.string.listing_distance_campus, listing.distanceFromCampusKm, listing.campusName)
        findViewById<TextView>(R.id.tvDescription).text = listing.description

        val statusBadge = findViewById<TextView>(R.id.tvDetailStatusBadge)
        val reserveButton = findViewById<MaterialButton>(R.id.btnReserveRoom)
        val isReserved = listing.status.equals("Reserved", ignoreCase = true)
        statusBadge.text = if (isReserved) {
            getString(R.string.listing_status_reserved)
        } else {
            getString(R.string.listing_status_available)
        }
        statusBadge.setBackgroundResource(
            if (isReserved) R.drawable.bg_status_reserved else R.drawable.bg_status_available
        )
        statusBadge.setTextColor(
            getColor(if (isReserved) R.color.gs_reserved_text else R.color.gs_secondary)
        )

        reserveButton.isEnabled = !isReserved
        reserveButton.text = if (isReserved) {
            getString(R.string.details_already_reserved)
        } else {
            getString(R.string.details_reserve_room)
        }
        reserveButton.alpha = if (isReserved) 0.65f else 1f
        reserveButton.setOnClickListener {
            startActivity(
                Intent(this, PaymentActivity::class.java).apply {
                    putExtra(PaymentActivity.EXTRA_LISTING_ID, listing.listingId)
                    putExtra(PaymentActivity.EXTRA_STUDENT_ID, currentStudentId)
                    putExtra(PaymentActivity.EXTRA_STUDENT_NAME, currentStudentName)
                }
            )
        }

        findViewById<MaterialButton>(R.id.btnViewRoute).setOnClickListener {
            openRoute(listing)
        }

        val imageResId = resources.getIdentifier(
            listing.imageName,
            "drawable",
            packageName
        )
        Glide.with(this)
            .load(if (imageResId != 0) imageResId else R.drawable.bg_listing_placeholder)
            .placeholder(R.drawable.bg_listing_placeholder)
            .error(R.drawable.bg_listing_placeholder)
            .into(findViewById<ImageView>(R.id.ivDetailImage))
    }

    private fun openRoute(listing: ListingEntity) {
        val title = URLEncoder.encode(listing.title, "UTF-8")
        val geoUri = Uri.parse("geo:0,0?q=${listing.latitude},${listing.longitude}($title)")
        val geoIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
            `package` = "com.google.android.apps.maps"
        }

        if (geoIntent.resolveActivity(packageManager) != null) {
            startActivity(geoIntent)
            return
        }

        val webUri = Uri.parse(
            "https://www.google.com/maps/search/?api=1&query=${listing.latitude},${listing.longitude}"
        )
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
        if (webIntent.resolveActivity(packageManager) != null) {
            startActivity(webIntent)
        } else {
            Toast.makeText(this, R.string.details_route_unavailable, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_LISTING_ID = "extra_listing_id"
        const val EXTRA_STUDENT_ID = "extra_student_id"
        const val EXTRA_STUDENT_NAME = "extra_student_name"
    }
}
