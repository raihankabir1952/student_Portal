# Student Portal – Android Application

Student Portal is an Android mobile application developed using Kotlin in Android Studio.
The app is designed for managing student and faculty academic activities
through a single platform. Firebase is used as the backend database and
authentication system.

## Project Overview
This application has two main user roles: Student and Faculty.
Students can manage their profile, courses,registration, and academic routine,
while faculty members can manage notices and student results.
The app also supports Light Mode and Dark Mode for better user experience.

## Technologies Used
- Kotlin
- Android Studio
- Firebase Authentication
- Firebase Realtime Database / Firestore
- XML (UI Design)

## User Roles and Features

### Student Features
- Student login system
- Forgot password using email verification
- New student registration
- View personal profile information
- Edit profile details (except email, semester, and CGPA)
- Course registration
- View registered courses in "My Courses"
- Remove courses from the course list
- View class routine based on registered courses
- View results submitted by faculty
- Light mode and dark mode support

### Faculty Features
- Faculty login system
- Faculty dashboard
- Post notices that are visible to all students
- View registered students
- View courses taken by each student
- Submit marks for specific courses
- Published results are visible in student accounts

## Authentication and Database
- Firebase Authentication is used for secure login and password recovery
- Firebase database is used to store user data, courses, routines, notices, and results

## How to Run the Project
1. Clone the repository
2. Open the project in Android Studio
3. Connect Firebase with the project
4. Sync Gradle files
5. Run the app on an emulator or physical Android device

## Purpose of the Project
This project was developed to practice Android application development
using Kotlin and Firebase, and to understand real-world features such as
authentication, role-based access, database integration, and UI theming.

## Screenshots
(Add screenshots of the app here)
