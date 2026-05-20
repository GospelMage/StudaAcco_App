package com.example.gabsstudentstay

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.gabsstudentstay.data.local.AppDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReceiptActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private val currencyFormatter = NumberFormat.getNumberInstance(Locale.US)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_receipt)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.receiptRoot)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        database = AppDatabase.getDatabase(this)
        val paymentId = intent.getIntExtra(EXTRA_PAYMENT_ID, -1)
        val listingId = intent.getIntExtra(EXTRA_LISTING_ID, -1)
        val studentName = intent.getStringExtra(EXTRA_STUDENT_NAME)
            ?: getString(R.string.receipt_student_fallback)

        if (paymentId == -1 || listingId == -1) {
            Toast.makeText(this, R.string.receipt_missing_data, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<MaterialButton>(R.id.btnBackToHome).setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("studentId", intent.getIntExtra(EXTRA_STUDENT_ID, 1))
                    putExtra("studentName", studentName)
                }
            )
            finish()
        }

        lifecycleScope.launch {
            val payment = withContext(Dispatchers.IO) { database.paymentDao().getPaymentById(paymentId) }
            val listing = withContext(Dispatchers.IO) { database.listingDao().getListingById(listingId) }

            if (payment == null || listing == null) {
                Toast.makeText(this@ReceiptActivity, R.string.receipt_missing_data, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                findViewById<TextView>(R.id.tvReceiptReference).text = payment.referenceNumber
                findViewById<TextView>(R.id.tvReceiptRoom).text = listing.title
                findViewById<TextView>(R.id.tvReceiptLocation).text = listing.location
                findViewById<TextView>(R.id.tvReceiptStudent).text = studentName
                findViewById<TextView>(R.id.tvReceiptAmount).text =
                    getString(R.string.listing_deposit_format, currencyFormatter.format(payment.amountPaid))
                findViewById<TextView>(R.id.tvReceiptDate).text =
                    dateFormat.format(Date(payment.paidAt))
                findViewById<TextView>(R.id.tvReceiptStatus).text =
                    getString(R.string.receipt_reserved_status)
            }
        }
    }

    companion object {
        const val EXTRA_PAYMENT_ID = "extra_payment_id"
        const val EXTRA_LISTING_ID = "extra_listing_id"
        const val EXTRA_REFERENCE_NUMBER = "extra_reference_number"
        const val EXTRA_AMOUNT_PAID = "extra_amount_paid"
        const val EXTRA_STUDENT_NAME = "extra_student_name"
        const val EXTRA_STUDENT_ID = "extra_student_id"
        const val EXTRA_ROOM_TITLE = "extra_room_title"
    }
}
