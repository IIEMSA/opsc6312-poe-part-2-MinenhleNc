# MoodTracker Mobile App

![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange)
![Android](https://img.shields.io/badge/Platform-Android-brightgreen)
![Firebase](https://img.shields.io/badge/Firebase-Authentication-blue)
![RoomDB](https://img.shields.io/badge/Database-RoomDB-lightgrey)

---

## Team Members
1. **Abongile Mazongolo** – st10043271@imconnect.edu.za  
2. **Jamie-Lee Simelane** – st10263509@imconnect.edu.za  
3. **Minenhle Ncongwane** – st10266839@imconnect.edu.za  
4. **Brandon Choga** – st10389397@imconnect.edu.za  

---

## Project Overview
**MoodTracker** is a mobile application designed to allow users to track and manage their moods, view mood history, export mood data, and get real-time weather updates for their location. The app provides a simple and interactive interface, ensuring a seamless user experience on Android devices.

---

## Features

### 1. Mood Logging
- Select a mood from a predefined list of emotions.
- Add main and additional notes for context.
- Logs are saved in **RoomDB** and optionally synced to **Firebase Realtime Database**.

### 2. Mood History
- View chronological mood entries with:
  - Mood selected
  - Main and additional notes
  - Location and weather at logging time
  - Timestamp
- Delete all mood data if needed.

### 3. Export Data
- Export mood history to a `.txt` file in the device's Downloads folder.
- Includes all details: mood, notes, location, weather, timestamp.

### 4. Weather Display
- Fetches and displays current weather for a user-specified location.
- Uses the **OpenWeather API** to fetch temperature and conditions.
- Weather data is linked to mood logs for context.

### 5. Settings Page
- Edit profile information.
- Upload/change profile picture.
- Enable/disable offline mode.
- Access notifications, help/support, and other app settings.
- Additional features are planned for future updates.

---

## Navigation Flow
1. **Home Page** → Quick access to mood logging and weather display.  
2. **Settings Page** → Profile editing, picture upload, app settings.  
3. **Mood Logging Page** → Log mood with notes and view weather.  
4. **Mood History Page** → View past entries and export data.  
5. **Logout** → Returns to login page.

---

## Software and Tools Used
- **Android Studio** – IDE for app development.  
- **Kotlin** – Programming language for app logic.  
- **RoomDB** – Local database for mood logs.  
- **Firebase Authentication** – Secure user login.  
- **Firebase Realtime Database** – Optional cloud sync for mood logs.  
- **OpenWeather API** – Real-time weather data based on user location.  
- **GridLayout & RecyclerView** – UI components for mood selection and history display.

---

## Purpose of the App
MoodTracker helps users:
- Track and understand their emotions over time.
- Connect moods with location and weather.
- Export mood history for personal analysis or journaling.
- Manage profile and customize app experience.

---

## Device Compatibility
- Runs on Android mobile devices.
- Fully tested and functional on physical devices.
- Smooth navigation and reliable data management.

---

## Future Enhancements
- Analytics and charts for mood trends.
- Enhanced notifications and reminders.
- More personalization options in the settings page.

---

## API Integration
- **OpenWeather API** is integrated using HTTP requests.
- Weather data fetched based on user-input location.
- Displayed in real-time alongside mood logging.
- API responses parsed and stored locally for offline display.

## Video Demonstration

A demonstration of the MoodTracker app in action can be viewed here:  
[Watch the demo video](https://youtu.be/bTU-zZaIHnE)  


