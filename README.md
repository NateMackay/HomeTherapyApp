# Home Therapy App

This app was developed by BYU-i students Eric Burns, Emile Moosman, and Nate Mackay for their sponsor, Achieve Center Pediatric Therapy, a pediatric therapy clinic located in Central in Eastern Washington, in fulfillment of a team project assignment for CS246, Software Design and Development, during Winter semester, 2019.

The app is designed to help Achieve's therapists keep their clients on target with their goals through working on home exercises in between treatment sessions. Therapists are able to assign home exercises to clients in the app, and clients then see their assigned exercises, and upon completing the exercises, receive rewards points, which can later be redeemed for prizes in the office. The app allows not only therapist users and client users but also admin users who can add new and manage existing users.

## Features

The following features were established as minimum requirements by the project sponsor, which were met in the current version (Beta 1.0):

* All users have an account, including clients, therapists, and administrators.
* Therapist can add exercises to client accounts, which can be saved for re-use for other clients.
* A therapist dashboard will allow therapists to manage their clients' home exercise programs.
* The program will track the progress of each client.
* App will feature a reward system, tracking points for each exercise completed.

Additional stretch goals not achieved for the current version (Beta 1.0) were established as follows:

* Messaging features: recorded video messages, live video chat, and in-app text messaging
* Parent dashboard allowing parent to track progress of their child(ren)'s home exercise programs.
* Exercise reminders / notifications to either email, text, and/or in-app

## App Platforms

The Home Therapy App currently only has one build that is designed for the Android platform. The app was developed in Android Studio 3.3.

## Project Design

The app was developed with a basic MVC design. There are multiple activities that serve as controllers between the layouts/views and the model classes. Activities are defined as follows:

* MainActivity: landing page, which serves as a splash screen
* AddEditUser: for admin users to add new or edit existing users
* AddExerciseToClient: for therapist users to assign exercises from the exercise library to clients
* AddExerciseToLibrary: for admin and therapist users to add new or edit existing exercises in the exercise library
* ClientExerciseLibrary: a specific view used by a therapist to select existing exercises from the exercise library to assign to clients
* ClientExercises: the therapist's view of the exercises that have been assigned to a given client
* Exercises: the exercise library, to which both therapist and admin users can add or edit existing exercises, which are then made available to all therapist users
* MyClients: the therapist's view of a list of their clients, which after selecting a client, takes the therapist to a view of that client's assigned exercises
* MyExercise: client's view of a given exercise assigned to them
* MyExercises: client's view of a list of their assigned exercises
* MyMessages: future feature under development - used for in-app messaging between therapists and clients
* MyMessaging: future feature under development - used for in-app messaging between therapists and clients
* MyProfile: used by all users to see their own profile, change password, email, and other user details
* MyRewards: client's view of the number of points-rewards that they have earned with the ability to redeem points
* SignIn: sign-in screen for all users
* Users: admin user's view of all users

## Version

* Current version: Beta 1.0
* Since: April 6, 2019

A prior version that was forked from this project was developed using Shared Preferences and the GSON library for storing data. While this worked fine for testing purposes, it did not allow for multiple users sharing the same dataset.

## Authors

* Eric Burns (https://github.com/eburns000)
* Emile Moosman (https://github.com/emilemoosman)
* Nate Mackay (https://github.com/natemackay)



