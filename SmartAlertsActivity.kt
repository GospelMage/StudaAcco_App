package com.example.gabsstudentstay

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gabsstudentstay.data.local.AppDatabase
import com.example.gabsstudentstay.data.local.entity.ListingEntity
import com.example.gabsstudentstay.data.local.entity.SavedPreferenceEntity
import com.example.gabsstudentstay.util.NotificationHelper
import com.example.gabsstudentstay.ui.home.AlertMatchAdapter
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class SmartAlertsActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var adapter: AlertMatchAdapter
    private var studentId: Int = 1
    private var studentName: String = "Student"
    private var savedPreference: SavedPreferenceEntity? = null
    private var currentMatches: List<ListingEntity> = emptyList()
    private val currencyFormatter = NumberFormat.getNumberInstance(Locale.US)

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                sendAppropriateNotification()
            } else {
                Toast.makeText(this, R.string.smart_alerts_permission_denied, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_smart_alerts)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.smartAlertsRoot)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        database = AppDatabase.getDatabase(this)
        NotificationHelper.createChannel(this)

        // Fallback to studentId = 1 if no real session exists. Real apps should use logged-in user context.
        studentId = intent.getIntExtra("studentId", 1)
        studentName = intent.getStringExtra("studentName") ?: "Student"

        adapter = AlertMatchAdapter { listing ->
            startActivity(
                Intent(this, ListingDetailsActivity::class.java).apply {
                    putExtra(ListingDetailsActivity.EXTRA_LISTING_ID, listing.listingId)
                    putExtra(ListingDetailsActivity.EXTRA_STUDENT_ID, studentId)
                    putExtra(ListingDetailsActivity.EXTRA_STUDENT_NAME, studentName)
                }
            )
        }

        findViewById<RecyclerView>(R.id.rvAlertMatches).apply {
            layoutManager = LinearLayoutManager(this@SmartAlertsActivity)
            adapter = this@SmartAlertsActivity.adapter
            isNestedScrollingEnabled = false
        }

        findViewById<MaterialButton>(R.id.btnBackToHome).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnCheckMatchingRooms).setOnClickListener { checkMatchingRooms() }
        findViewById<MaterialButton>(R.id.btnSendTestAlert).setOnClickListener { requestPermissionAndNotify() }

        loadPreferenceSummary()
    }

    private fun loadPreferenceSummary() {
        lifecycleScope.launch {
            val preference = withContext(Dispatchers.IO) {
                database.savedPreferenceDao().getPreferenceForStudent(studentId)
            }
            savedPreference = preference
            bindPreferenceSummary(preference)
        }
    }

    private fun bindPreferenceSummary(preference: SavedPreferenceEntity?) {
        val statusText = findViewById<TextView>(R.id.tvPreferenceStatus)
        val maxPrice = findViewById<TextView>(R.id.tvPrefMaxPrice)
        val location = findViewById<TextView>(R.id.tvPrefLocation)
        val availability = findViewById<TextView>(R.id.tvPrefAvailability)
        val amenities = findViewById<TextView>(R.id.tvPrefAmenities)
        val notifications = findViewById<TextView>(R.id.tvPrefNotifications)

        if (preference == null) {
            statusText.text = getString(R.string.smart_alerts_no_preferences)
            maxPrice.text = getString(R.string.smart_alerts_pref_max_price, getString(R.string.smart_alerts_no_summary_value))
            location.text = getString(R.string.smart_alerts_pref_location, getString(R.string.smart_alerts_no_summary_value))
            availability.text = getString(R.string.smart_alerts_pref_date, getString(R.string.smart_alerts_no_summary_value))
            amenities.text = getString(R.string.smart_alerts_pref_amenities, getString(R.string.smart_alerts_no_summary_value))
            notifications.text = getString(R.string.smart_alerts_pref_notifications, getString(R.string.smart_alerts_no))
            return
        }

        statusText.text = getString(R.string.smart_alerts_subtitle)
        maxPrice.text = getString(
            R.string.smart_alerts_pref_max_price,
            if (preference.maxPrice > 0.0) "BWP ${currencyFormatter.format(preference.maxPrice)}" else getString(R.string.smart_alerts_no_summary_value)
        )
        location.text = getString(
            R.string.smart_alerts_pref_location,
            preference.preferredLocation.ifBlank { getString(R.string.smart_alerts_no_summary_value) }
        )
        availability.text = getString(
            R.string.smart_alerts_pref_date,
            preference.availabilityDate.ifBlank { getString(R.string.smart_alerts_no_summary_value) }
        )
        amenities.text = getString(
            R.string.smart_alerts_pref_amenities,
            preference.preferredAmenities.ifBlank { getString(R.string.smart_alerts_no_summary_value) }
        )
        notifications.text = getString(
            R.string.smart_alerts_pref_notifications,
            if (preference.notificationsEnabled) getString(R.string.smart_alerts_yes) else getString(R.string.smart_alerts_no)
        )
    }

    private fun checkMatchingRooms() {
        lifecycleScope.launch {
            val preference = savedPreference
            if (preference == null) {
                findViewById<TextView>(R.id.tvMatchStatus).text = getString(R.string.smart_alerts_no_preferences)
                adapter.submitList(emptyList())
                return@launch
            }

            val listings = withContext(Dispatchers.IO) {
                database.listingDao().getAllListingsNow()
            }
            currentMatches = filterMatches(listings, preference)
            adapter.submitList(currentMatches)
            findViewById<TextView>(R.id.tvMatchStatus).text =
                if (currentMatches.isEmpty()) {
                    getString(R.string.smart_alerts_no_matches)
                } else {
                    getString(R.string.smart_alerts_matches_found, currentMatches.size)
                }
        }
    }

    private fun filterMatches(
        listings: List<ListingEntity>,
        preference: SavedPreferenceEntity
    ): List<ListingEntity> {
        val preferredAmenities = preference.preferredAmenities
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        return listings.filter { listing ->
            val matchesStatus = listing.status.equals("Available", ignoreCase = true)
            val matchesPrice = preference.maxPrice <= 0.0 || listing.price <= preference.maxPrice
            val matchesLocation = preference.preferredLocation.isBlank() ||
                listing.location.equals(preference.preferredLocation, ignoreCase = true)
            val matchesDate = preference.availabilityDate.isBlank() ||
                listing.availabilityDate <= preference.availabilityDate
            val matchesAmenities = preferredAmenities.isEmpty() ||
                preferredAmenities.all { amenity ->
                    listing.amenities.contains(amenity, ignoreCase = true)
                }

            matchesStatus && matchesPrice && matchesLocation && matchesDate && matchesAmenities
        }
    }

    private fun requestPermissionAndNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                sendAppropriateNotification()
            } else {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            sendAppropriateNotification()
        }
    }

    private fun sendAppropriateNotification() {
        if (currentMatches.isNotEmpty()) {
            val firstMatch = currentMatches.first()
            NotificationHelper.sendNotification(
                this,
                getString(R.string.smart_alerts_notification_title),
                getString(R.string.smart_alerts_notification_message, firstMatch.location)
            )
        } else {
            NotificationHelper.sendNotification(
                this,
                getString(R.string.smart_alerts_demo_title),
                getString(R.string.smart_alerts_demo_message)
            )
        }
    }
}
