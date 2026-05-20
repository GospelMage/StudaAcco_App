# GabsStudentStay Demo Script

## 1. Introduction
- Introduce the app: `GabsStudentStay – Smart Accommodation Finder App`.
- State the problem: tertiary students in Gaborone need a simple way to find safe accommodation by price, location, and availability.

## 2. Purpose of the App
- Explain that the app helps students browse rooms, filter results, save preferences, receive smart alerts, pay a simulated deposit, reserve a room, and navigate to the location.

## 3. Design and Database Overview
- Mention that the UI was built in Android XML layouts inspired by the provided Stitch design folder.
- Mention that persistent local storage uses Room Database.
- Briefly summarize the main tables: students, listings, payments, reservations, and saved preferences.

## 4. Software Tools Used
- Android Studio
- Kotlin
- XML layouts
- Room Database
- RecyclerView
- Material Components
- Glide

## 5. Functionality Demonstration
- Show app launch on the Welcome screen.
- Show registration validation and duplicate email prevention.
- Log in with a seeded account.
- Show the Home screen and listing cards.
- Open Filter, apply filters, and save preferences.
- Open Smart Alerts, check matching rooms, and send a test notification.
- Open listing details and demonstrate route navigation.
- Open Payment, complete simulated payment, and show the receipt.
- Return Home and show that the listing is now reserved and cannot be reserved again.

## 6. Testing Explanation
- Explain that the app was manually tested for launch, authentication, filtering, alerts, payment flow, reservation update, and double reservation prevention.
- Mention that the project builds successfully into a debug APK.

## 7. Closing Statement
- Conclude that the app meets the main assignment requirements:
  registration/login, listings, filtering, smart alerts, simulated payment, reservation, route navigation, and persistent local storage.
