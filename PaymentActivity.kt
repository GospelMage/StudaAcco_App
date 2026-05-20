package com.example.gabsstudentstay

import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.gabsstudentstay.data.local.AppDatabase
import com.example.gabsstudentstay.data.local.entity.PaymentEntity
import com.example.gabsstudentstay.data.local.entity.ReservationEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private val currencyFormatter = NumberFormat.getNumberInstance(Locale.US)
    private var listingId: Int = -1
    private var studentId: Int = 1
    private var studentName: String = "Student"
    private var currentListingTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.paymentRoot)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        database = AppDatabase.getDatabase(this)
        listingId = intent.getIntExtra(EXTRA_LISTING_ID, -1)
        studentId = intent.getIntExtra(EXTRA_STUDENT_ID, 1)
        studentName = intent.getStringExtra(EXTRA_STUDENT_NAME) ?: "Student"
        findViewById<MaterialButton>(R.id.btnPaymentBack).setOnClickListener { finish() }

        if (listingId == -1) {
            Toast.makeText(this, R.string.payment_missing_listing, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            val listing = withContext(Dispatchers.IO) {
                database.listingDao().getListingById(listingId)
            }

            if (listing == null) {
                Toast.makeText(this@PaymentActivity, R.string.payment_missing_listing, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                bindListing(listing)
            }
        }
    }

    private fun bindListing(listing: com.example.gabsstudentstay.data.local.entity.ListingEntity) {
        currentListingTitle = listing.title
        findViewById<TextView>(R.id.tvPaymentRoomTitle).text = listing.title
        findViewById<TextView>(R.id.tvPaymentLocation).text = listing.location
        findViewById<TextView>(R.id.tvPaymentDeposit).text =
            getString(R.string.listing_deposit_format, currencyFormatter.format(listing.depositAmount))

        val confirmButton = findViewById<MaterialButton>(R.id.btnConfirmPayment)
        if (listing.status.equals("Reserved", ignoreCase = true)) {
            confirmButton.isEnabled = false
            confirmButton.alpha = 0.65f
            Toast.makeText(this, R.string.payment_room_reserved, Toast.LENGTH_SHORT).show()
            return
        }

        confirmButton.setOnClickListener {
            processPayment(listing)
        }
    }

    private fun processPayment(listing: com.example.gabsstudentstay.data.local.entity.ListingEntity) {
        val paymentMethodGroup = findViewById<RadioGroup>(R.id.rgPaymentMethod)
        val nameInput = findViewById<TextInputEditText>(R.id.etNameOnPayment)
        val phoneInput = findViewById<TextInputEditText>(R.id.etPhoneNumber)
        val noteInput = findViewById<TextInputEditText>(R.id.etReferenceNote)

        val name = nameInput.text?.toString()?.trim().orEmpty()
        val phone = phoneInput.text?.toString()?.trim().orEmpty()
        val note = noteInput.text?.toString()?.trim().orEmpty()
        val paymentMethod = when (paymentMethodGroup.checkedRadioButtonId) {
            R.id.rbCardPayment -> getString(R.string.payment_card)
            R.id.rbMobileMoney -> getString(R.string.payment_mobile_money)
            R.id.rbBankTransfer -> getString(R.string.payment_bank_transfer)
            else -> ""
        }

        when {
            paymentMethod.isBlank() -> {
                Toast.makeText(this, R.string.payment_method_required, Toast.LENGTH_SHORT).show()
                return
            }

            name.isBlank() -> {
                nameInput.error = getString(R.string.payment_name_on_payment)
                nameInput.requestFocus()
                return
            }

            phone.isBlank() -> {
                phoneInput.error = getString(R.string.payment_phone_number)
                phoneInput.requestFocus()
                return
            }
        }

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                val latestListing = database.listingDao().getListingById(listingId)
                    ?: return@withContext PaymentResult.Missing

                val alreadyReserved = latestListing.status.equals("Reserved", ignoreCase = true) ||
                    database.reservationDao().countReservationsForListing(listingId) > 0

                if (alreadyReserved) {
                    return@withContext PaymentResult.AlreadyReserved
                }

                val referenceNumber = "GST-${System.currentTimeMillis()}"
                val paymentId = database.paymentDao().insertPayment(
                    PaymentEntity(
                        studentId = studentId,
                        listingId = listingId,
                        amountPaid = latestListing.depositAmount,
                        paymentMethod = if (note.isBlank()) paymentMethod else "$paymentMethod • $note",
                        referenceNumber = referenceNumber,
                        paidAt = System.currentTimeMillis(),
                        status = "Successful"
                    )
                ).toInt()

                val reservationCount = database.reservationDao().countReservationsForListing(listingId)
                val refreshedListing = database.listingDao().getListingById(listingId)
                    ?: return@withContext PaymentResult.Missing

                if (reservationCount > 0 || refreshedListing.status.equals("Reserved", ignoreCase = true)) {
                    return@withContext PaymentResult.AlreadyReserved
                }

                database.reservationDao().insertReservation(
                    ReservationEntity(
                        studentId = studentId,
                        listingId = listingId,
                        paymentId = paymentId,
                        reservedAt = System.currentTimeMillis(),
                        status = "Reserved"
                    )
                )
                database.listingDao().updateListingStatus(listingId, "Reserved")

                PaymentResult.Success(paymentId, referenceNumber, latestListing.depositAmount)
            }

            when (result) {
                PaymentResult.Missing -> {
                    Toast.makeText(this@PaymentActivity, R.string.payment_missing_listing, Toast.LENGTH_SHORT).show()
                    finish()
                }

                PaymentResult.AlreadyReserved -> {
                    Toast.makeText(this@PaymentActivity, R.string.payment_room_reserved, Toast.LENGTH_SHORT).show()
                    finish()
                }

                is PaymentResult.Success -> {
                    startActivity(
                        Intent(this@PaymentActivity, ReceiptActivity::class.java).apply {
                            putExtra(ReceiptActivity.EXTRA_PAYMENT_ID, result.paymentId)
                            putExtra(ReceiptActivity.EXTRA_LISTING_ID, listingId)
                            putExtra(ReceiptActivity.EXTRA_REFERENCE_NUMBER, result.referenceNumber)
                            putExtra(ReceiptActivity.EXTRA_AMOUNT_PAID, result.amountPaid)
                            putExtra(ReceiptActivity.EXTRA_STUDENT_NAME, studentName)
                            putExtra(ReceiptActivity.EXTRA_STUDENT_ID, studentId)
                            putExtra(ReceiptActivity.EXTRA_ROOM_TITLE, currentListingTitle)
                        }
                    )
                    finish()
                }
            }
        }
    }

    companion object {
        const val EXTRA_LISTING_ID = "extra_listing_id"
        const val EXTRA_STUDENT_ID = "extra_student_id"
        const val EXTRA_STUDENT_NAME = "extra_student_name"
    }
}

private sealed class PaymentResult {
    data object Missing : PaymentResult()
    data object AlreadyReserved : PaymentResult()
    data class Success(
        val paymentId: Int,
        val referenceNumber: String,
        val amountPaid: Double
    ) : PaymentResult()
}
