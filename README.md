# StudaAcco_App
# GabsStudentStay – Smart Accommodation Finder App

## Overview
GabsStudentStay is an Android mobile application developed using Kotlin in Android Studio. The application helps tertiary students in Gaborone find affordable and safe accommodation based on price, location, and availability date.

The system allows students to browse accommodation listings, filter results according to preferences, reserve rooms through a simulated payment system, and receive smart notifications when suitable listings become available.

---

## Features

### User Management
- Student registration
- Student login
- Input validation
- Persistent user data storage

### Accommodation Listings
- View accommodation listings
- Listing details include:
  - Title
  - Price
  - Location
  - Amenities
  - Availability date
  - Deposit amount
  - Images

### Smart Filtering
- Filter by price range
- Filter by location
- Filter by availability date

### Smart Alerts
- Local notifications for matching listings
- Preference-based recommendations

### Reservation System
- Simulated deposit payment
- Receipt/reference number generation
- Room reservation after successful payment
- Prevents double booking

### Extension Feature
- Chat system between student and landlord
OR
- Campus distance and route navigation

---

## Technologies Used

- Kotlin
- Android Studio
- Room Database / Firebase
- RecyclerView
- Material Design Components
- Notifications API
- Gradle

---

## System Requirements

- Android Studio 7 or above
- Minimum SDK 26
- Gradle 4.10.0 or above
- Android Emulator or Android Device

---

## Project Structure

```text
GabsStudentStay/
│
├── app/
├── gradle/
├── screenshots/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
