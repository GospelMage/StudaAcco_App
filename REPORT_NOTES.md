# GabsStudentStay Report Notes

## Project Title
GabsStudentStay – Smart Accommodation Finder App

## Problem Background
Students in Gaborone often struggle to find affordable and safe accommodation near campus. They need a simple mobile tool to compare listings by price, location, and availability while also supporting reservation and navigation.

## App Objectives
- Allow students and providers to register and log in.
- Display accommodation listings with key details.
- Filter listings by price, location, and availability date.
- Save user preferences for smart alerts.
- Simulate deposit payment and confirm room reservation.
- Prevent double reservation of already reserved rooms.
- Provide campus distance and route navigation.

## Tools and Software Used
- Android Studio
- Kotlin
- XML layouts
- Room Database
- RecyclerView
- Material Components
- Glide
- Google Maps intent for route navigation

## Database Schema Summary
- `StudentEntity`: student/provider user records
- `ListingEntity`: accommodation listing information
- `SavedPreferenceEntity`: stored filter preferences for alerts
- `PaymentEntity`: simulated deposit payment records
- `ReservationEntity`: room reservation records

## Screen List
- Welcome screen
- Login screen
- Register screen
- Home/Listings screen
- Filter screen
- Listing Details screen
- Payment screen
- Receipt screen
- Smart Alerts screen

## Main Features
- Student registration and login
- 50 seeded student records
- 50 seeded accommodation listings
- Room listings with images/placeholders and full details
- Filtering by price, location, availability date, and amenities
- Saved preferences for smart alerts
- Local notification test for matching rooms
- Simulated deposit payment
- Receipt/reference number generation
- Reservation creation and listing status update
- Double reservation prevention
- Route navigation using Google Maps intent
- Persistent storage using Room Database

## Testing Summary
- Manual testing covered launch, registration, duplicate email prevention, login success/failure, listings load, filtering, preference saving, alerts, route navigation, payment validation, receipt display, reservation update, and double reservation prevention.
- Debug build assembled successfully.

## Screenshots Needed
- Welcome screen
- Login screen
- Register screen
- Home/Listings screen
- Filter screen
- Smart Alerts screen
- Listing Details screen
- Payment screen
- Receipt screen
- Reserved listing shown on Home or Details

## Conclusion
GabsStudentStay provides a clean and functional academic demo of a student accommodation finder app with persistent local storage, filtering, smart alerts, simulated reservation payment, and route navigation. It is suitable for the assignment submission and demonstration.
