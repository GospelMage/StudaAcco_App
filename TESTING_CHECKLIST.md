# GabsStudentStay Testing Checklist

## Launch and Navigation
- Launch the app and confirm `WelcomeActivity` opens without crashing.
- Tap `Get Started` and confirm the Register screen opens.
- Tap `Login` on the welcome screen and confirm the Login screen opens.
- Use back navigation on Filter, Details, Payment, Smart Alerts, and confirm the previous screen returns correctly.

## Registration and Login
- Register a new student with valid data and confirm success message/navigation.
- Try registering with an email that already exists and confirm duplicate prevention message appears.
- Try registering with empty fields and confirm validation errors appear.
- Log in with a valid seeded account such as `student1@gabsstudentstay.com / password123`.
- Log in with an invalid email or password and confirm failure message appears.

## Listings and Home Screen
- Confirm the home screen loads accommodation listings from Room.
- Confirm listing cards show title, price, location, room type, availability date, deposit, amenities, and status.
- Confirm reserved listings look visually different from available listings.
- Confirm missing room images fall back to the placeholder without crashing.

## Filtering and Preferences
- Open Filter and apply a max price filter. Confirm results update.
- Apply a location filter. Confirm only matching locations appear.
- Apply an availability date filter. Confirm only eligible rooms appear.
- Apply amenities and confirm matches narrow down correctly.
- Clear filters and confirm all listings return.
- Save preferences for alerts and confirm the success toast appears.

## Smart Alerts
- Open Smart Alerts and confirm saved preference summary is shown.
- Tap `Check Matching Rooms` and confirm matching rooms appear or the no-match message is shown.
- Tap `Send Test Alert` and confirm a local notification is sent.
- On Android 13+, deny notification permission once and confirm the permission warning toast appears.

## Listing Details and Route
- Tap a listing and confirm `ListingDetailsActivity` opens.
- Confirm image, price, deposit, amenities, provider, campus, and description load correctly.
- Tap `View Route` and confirm Google Maps or browser fallback opens.

## Payment and Reservation
- Open an available listing and tap `Reserve Room`.
- Try confirming payment without selecting a payment method and confirm validation message appears.
- Try confirming payment without payer name or phone number and confirm field errors appear.
- Complete a simulated payment and confirm receipt screen opens.
- Confirm a receipt/reference number is displayed.
- Return to Home and confirm the room status changes to `Reserved`.
- Re-open the same room and confirm reservation/payment is blocked.
- Confirm double reservation prevention works if the room is already reserved.

## Persistence
- Close and reopen the app.
- Confirm Room data persists, including reserved room status and saved preferences.
