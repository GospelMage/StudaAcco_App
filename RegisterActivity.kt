package com.example.gabsstudentstay

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.gabsstudentstay.data.local.AppDatabase
import com.example.gabsstudentstay.data.local.entity.StudentEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerRoot)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        database = AppDatabase.getDatabase(this)

        val fullNameInput = findViewById<TextInputEditText>(R.id.etRegisterFullName)
        val emailInput = findViewById<TextInputEditText>(R.id.etRegisterEmail)
        val phoneInput = findViewById<TextInputEditText>(R.id.etRegisterPhone)
        val passwordInput = findViewById<TextInputEditText>(R.id.etRegisterPassword)
        val confirmPasswordInput = findViewById<TextInputEditText>(R.id.etRegisterConfirmPassword)
        val roleSpinner = findViewById<Spinner>(R.id.spinnerRole)

        val roles = listOf("Student", "Provider")
        roleSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            roles
        )

        findViewById<MaterialButton>(R.id.btnGoToLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<MaterialButton>(R.id.btnCreateAccount).setOnClickListener {
            val fullName = fullNameInput.text?.toString()?.trim().orEmpty()
            val email = emailInput.text?.toString()?.trim().orEmpty()
            val phone = phoneInput.text?.toString()?.trim().orEmpty()
            val password = passwordInput.text?.toString()?.trim().orEmpty()
            val confirmPassword = confirmPasswordInput.text?.toString()?.trim().orEmpty()
            val role = roleSpinner.selectedItem?.toString().orEmpty()

            when {
                fullName.isEmpty() -> {
                    fullNameInput.error = "Full name is required"
                    fullNameInput.requestFocus()
                }

                email.isEmpty() -> {
                    emailInput.error = "Email is required"
                    emailInput.requestFocus()
                }

                phone.isEmpty() -> {
                    phoneInput.error = "Phone number is required"
                    phoneInput.requestFocus()
                }

                password.isEmpty() -> {
                    passwordInput.error = "Password is required"
                    passwordInput.requestFocus()
                }

                confirmPassword.isEmpty() -> {
                    confirmPasswordInput.error = "Confirm password is required"
                    confirmPasswordInput.requestFocus()
                }

                password != confirmPassword -> {
                    confirmPasswordInput.error = "Passwords do not match"
                    confirmPasswordInput.requestFocus()
                }

                role.isEmpty() -> {
                    Toast.makeText(this, "Role is required", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    registerStudent(fullName, email, phone, password, role)
                }
            }
        }
    }

    private fun registerStudent(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        role: String
    ) {
        lifecycleScope.launch {
            val existingStudent = withContext(Dispatchers.IO) {
                database.studentDao().getStudentByEmail(email)
            }

            if (existingStudent != null) {
                Toast.makeText(
                    this@RegisterActivity,
                    "An account with this email already exists",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            withContext(Dispatchers.IO) {
                database.studentDao().insertStudent(
                    StudentEntity(
                        fullName = fullName,
                        email = email,
                        phoneNumber = phone,
                        password = password,
                        role = role,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }

            Toast.makeText(
                this@RegisterActivity,
                "Registration successful. Please login.",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }
}
