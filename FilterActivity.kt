package com.example.gabsstudentstay

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.gabsstudentstay.data.local.AppDatabase
import com.example.gabsstudentstay.data.local.entity.SavedPreferenceEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilterActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var spinnerLocation: Spinner
    private lateinit var maxPriceInput: TextInputEditText
    private lateinit var availabilityDateInput: TextInputEditText
    private lateinit var amenityCheckboxes: List<CheckBox>
    private var studentId: Int = 1

    private val locations = listOf(
        "Any location",
        "Block 6",
        "Village",
        "Tlokweng",
        "Gaborone West",
        "Broadhurst",
        "Phakalane",
        "Extension 2",
        "Mogoditshane",
        "Block 8",
        "Main Mall",
        "Partial",
        "Maruapula",
        "Kgale View",
        "Extension 10",
        "Bontleng"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_filter)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.filterRoot)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        database = AppDatabase.getDatabase(this)
        studentId = intent.getIntExtra("studentId", 1)

        maxPriceInput = findViewById(R.id.etMaxPrice)
        availabilityDateInput = findViewById(R.id.etAvailabilityDate)
        spinnerLocation = findViewById(R.id.spinnerLocation)
        amenityCheckboxes = listOf(
            findViewById(R.id.cbWifi),
            findViewById(R.id.cbWaterIncluded),
            findViewById(R.id.cbElectricityIncluded),
            findViewById(R.id.cbSecurity),
            findViewById(R.id.cbFurnished),
            findViewById(R.id.cbParking)
        )

        spinnerLocation.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            locations
        )

        populateExistingValues()

        findViewById<MaterialButton>(R.id.btnApplyFilters).setOnClickListener {
            val validatedFilters = validateInputs() ?: return@setOnClickListener
            setResult(
                Activity.RESULT_OK,
                intent.apply {
                    putExtra(EXTRA_MAX_PRICE, validatedFilters.maxPriceText)
                    putExtra(EXTRA_LOCATION, validatedFilters.location)
                    putExtra(EXTRA_AVAILABILITY_DATE, validatedFilters.availabilityDate)
                    putStringArrayListExtra(EXTRA_AMENITIES, ArrayList(validatedFilters.amenities))
                }
            )
            finish()
        }

        findViewById<MaterialButton>(R.id.btnSavePreferences).setOnClickListener {
            val validatedFilters = validateInputs() ?: return@setOnClickListener
            savePreferences(validatedFilters)
        }

        findViewById<MaterialButton>(R.id.btnFilterBack).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnClearFilters).setOnClickListener {
            maxPriceInput.setText("")
            availabilityDateInput.setText("")
            spinnerLocation.setSelection(0)
            amenityCheckboxes.forEach { it.isChecked = false }
            setResult(
                Activity.RESULT_OK,
                intent.apply {
                    removeExtra(EXTRA_MAX_PRICE)
                    putExtra(EXTRA_LOCATION, "")
                    putExtra(EXTRA_AVAILABILITY_DATE, "")
                    putStringArrayListExtra(EXTRA_AMENITIES, arrayListOf())
                }
            )
            finish()
        }
    }

    private fun populateExistingValues() {
        maxPriceInput.setText(intent.getStringExtra(EXTRA_MAX_PRICE).orEmpty())
        availabilityDateInput.setText(intent.getStringExtra(EXTRA_AVAILABILITY_DATE).orEmpty())

        val selectedLocation = intent.getStringExtra(EXTRA_LOCATION).orEmpty()
        val locationIndex = locations.indexOfFirst { it.equals(selectedLocation, ignoreCase = true) }
        spinnerLocation.setSelection(if (locationIndex >= 0) locationIndex else 0)

        val selectedAmenities = intent.getStringArrayListExtra(EXTRA_AMENITIES)?.toSet().orEmpty()
        amenityCheckboxes.forEach { checkbox ->
            checkbox.isChecked = selectedAmenities.contains(checkbox.text.toString())
        }
    }

    private fun validateInputs(): ValidatedFilters? {
        val maxPriceText = maxPriceInput.text?.toString()?.trim().orEmpty()
        val availabilityDate = availabilityDateInput.text?.toString()?.trim().orEmpty()
        val location = spinnerLocation.selectedItem?.toString().orEmpty()
        val normalizedLocation = if (location == "Any location") "" else location
        val amenities = amenityCheckboxes.filter { it.isChecked }.map { it.text.toString() }

        if (maxPriceText.isNotBlank() && maxPriceText.toDoubleOrNull() == null) {
            maxPriceInput.error = getString(R.string.filter_invalid_price)
            maxPriceInput.requestFocus()
            return null
        }

        if (availabilityDate.isNotBlank() && !DATE_REGEX.matches(availabilityDate)) {
            availabilityDateInput.error = getString(R.string.filter_invalid_date)
            availabilityDateInput.requestFocus()
            return null
        }

        return ValidatedFilters(
            maxPriceText = maxPriceText,
            maxPriceValue = maxPriceText.toDoubleOrNull(),
            location = normalizedLocation,
            availabilityDate = availabilityDate,
            amenities = amenities
        )
    }

    private fun savePreferences(filters: ValidatedFilters) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val existingPreference = database.savedPreferenceDao().getPreferenceForStudent(studentId)
                val preference = SavedPreferenceEntity(
                    preferenceId = existingPreference?.preferenceId ?: 0,
                    studentId = studentId,
                    maxPrice = filters.maxPriceValue ?: 0.0,
                    preferredLocation = filters.location,
                    availabilityDate = filters.availabilityDate,
                    preferredAmenities = filters.amenities.joinToString(", "),
                    notificationsEnabled = true,
                    createdAt = System.currentTimeMillis()
                )

                if (existingPreference == null) {
                    database.savedPreferenceDao().insertPreference(preference)
                } else {
                    database.savedPreferenceDao().updatePreference(preference)
                }
            }

            Toast.makeText(
                this@FilterActivity,
                getString(R.string.filter_saved_success),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val EXTRA_MAX_PRICE = "extra_max_price"
        const val EXTRA_LOCATION = "extra_location"
        const val EXTRA_AVAILABILITY_DATE = "extra_availability_date"
        const val EXTRA_AMENITIES = "extra_amenities"
        private val DATE_REGEX = Regex("""\d{4}-\d{2}-\d{2}""")
    }
}

private data class ValidatedFilters(
    val maxPriceText: String,
    val maxPriceValue: Double?,
    val location: String,
    val availabilityDate: String,
    val amenities: List<String>
)
