package com.example.gabsstudentstay

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gabsstudentstay.data.local.AppDatabase
import com.example.gabsstudentstay.data.local.entity.ListingEntity
import com.example.gabsstudentstay.data.seed.DatabaseSeeder
import com.example.gabsstudentstay.ui.home.ListingAdapter
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var listingAdapter: ListingAdapter
    private var allListings: List<ListingEntity> = emptyList()
    private var currentFilters = FilterCriteria()
    private var currentStudentId: Int = 1
    private var currentStudentName: String = "Student"

    private val filterLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                currentFilters = FilterCriteria(
                    maxPrice = data?.getStringExtra(FilterActivity.EXTRA_MAX_PRICE)?.toDoubleOrNull(),
                    location = data?.getStringExtra(FilterActivity.EXTRA_LOCATION).orEmpty(),
                    availabilityDate = data?.getStringExtra(FilterActivity.EXTRA_AVAILABILITY_DATE).orEmpty(),
                    amenities = data?.getStringArrayListExtra(FilterActivity.EXTRA_AMENITIES)?.toList().orEmpty()
                )
                applyFilters()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val studentName = intent.getStringExtra("studentName") ?: "Student"
        currentStudentName = studentName
        // Fallback to studentId = 1 if no session was passed. Real apps should rely on a true logged-in session.
        currentStudentId = intent.getIntExtra("studentId", 1)
        findViewById<TextView>(R.id.tvGreeting).text =
            getString(R.string.home_greeting, studentName)

        database = AppDatabase.getDatabase(this)

        listingAdapter = ListingAdapter { listing ->
            startActivity(
                Intent(this, ListingDetailsActivity::class.java).apply {
                    putExtra(ListingDetailsActivity.EXTRA_LISTING_ID, listing.listingId)
                    putExtra(ListingDetailsActivity.EXTRA_STUDENT_ID, currentStudentId)
                    putExtra(ListingDetailsActivity.EXTRA_STUDENT_NAME, currentStudentName)
                }
            )
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvListings)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listingAdapter

        findViewById<MaterialButton>(R.id.btnFilterPlaceholder).setOnClickListener {
            openFilterScreen()
        }

        findViewById<MaterialButton>(R.id.btnClearFilters).setOnClickListener {
            currentFilters = FilterCriteria()
            applyFilters()
        }
        findViewById<MaterialButton>(R.id.btnSmartAlerts).setOnClickListener {
            startActivity(
                Intent(this, SmartAlertsActivity::class.java).apply {
                    putExtra("studentId", currentStudentId)
                    putExtra("studentName", currentStudentName)
                }
            )
        }

        observeListings()
        lifecycleScope.launch {
            DatabaseSeeder.seedIfNeeded(database)
        }
    }

    private fun observeListings() {
        val emptyState = findViewById<TextView>(R.id.tvEmptyState)
        database.listingDao().getAllListings().observe(this) { listings ->
            allListings = listings ?: emptyList()
            applyFilters()
            emptyState.visibility = if (listingAdapter.itemCount == 0) View.VISIBLE else View.GONE
        }
    }

    private fun openFilterScreen() {
        val intent = Intent(this, FilterActivity::class.java).apply {
            putExtra("studentId", currentStudentId)
            currentFilters.maxPrice?.let { putExtra(FilterActivity.EXTRA_MAX_PRICE, it.toString()) }
            putExtra(FilterActivity.EXTRA_LOCATION, currentFilters.location)
            putExtra(FilterActivity.EXTRA_AVAILABILITY_DATE, currentFilters.availabilityDate)
            putStringArrayListExtra(
                FilterActivity.EXTRA_AMENITIES,
                ArrayList(currentFilters.amenities)
            )
        }
        filterLauncher.launch(intent)
    }

    private fun applyFilters() {
        val filteredListings = allListings.filter { listing ->
            val matchesPrice = currentFilters.maxPrice == null || listing.price <= currentFilters.maxPrice!!
            val matchesLocation = currentFilters.location.isBlank() ||
                listing.location.equals(currentFilters.location, ignoreCase = true)
            val matchesDate = currentFilters.availabilityDate.isBlank() ||
                listing.availabilityDate <= currentFilters.availabilityDate
            val matchesAmenities = currentFilters.amenities.isEmpty() ||
                currentFilters.amenities.all { amenity ->
                    listing.amenities.contains(amenity, ignoreCase = true)
                }

            matchesPrice && matchesLocation && matchesDate && matchesAmenities
        }

        listingAdapter.submitList(filteredListings)
        updateFilterState(filteredListings.isEmpty())
    }

    private fun updateFilterState(noMatches: Boolean) {
        val filterStateSection = findViewById<View>(R.id.filterStateSection)
        val filterStatus = findViewById<TextView>(R.id.tvFilterStatus)
        val emptyState = findViewById<TextView>(R.id.tvEmptyState)
        val hasFilters = currentFilters.hasAny()

        filterStateSection.visibility = if (hasFilters) View.VISIBLE else View.GONE
        if (hasFilters) {
            filterStatus.text = getString(R.string.filter_showing_results)
        }
        emptyState.text = if (hasFilters && noMatches) {
            getString(R.string.filter_no_match)
        } else {
            getString(R.string.home_empty)
        }
        emptyState.visibility = if (listingAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }
}

data class FilterCriteria(
    val maxPrice: Double? = null,
    val location: String = "",
    val availabilityDate: String = "",
    val amenities: List<String> = emptyList()
) {
    fun hasAny(): Boolean {
        return maxPrice != null || location.isNotBlank() || availabilityDate.isNotBlank() || amenities.isNotEmpty()
    }
}
