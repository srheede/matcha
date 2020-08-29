# README

## Matcha

A mobile dating web-application that connects users based on their sort/
filter criteria, popularity and geo-location.

## Requirements

-   MAMP V5.7: <https://www.mamp.info/en/downloads/>

-   Node.js V12.18.3.: <https://nodejs.org/en/download/>

-   Android Studio V4.0.1: <https://developer.android.com/studio?hl=es>

-   API Key

## Installation

### How to download the source code:

-   Go to <https://github.com/srheede/camagru>

-   Click Clone or Download

### How to set up and configure the database:

-   Download MAMP

-   Start the MAMP application and click Start Servers

-   Make sure the Apache and MySQL servers are started

-   Open a web browser and go to <http://localhost:8888/phpmyadmin>

-   Create a table called matcha

-   Go to import and upload the file matcha.sql

### How to run the program:

-   Run using APK:

    -   Copy matcha.apk onto any android device

    -   Run matcha.apk

-   Without using APK:

    -   Copy Matcha to the htdocs folder

    -   In the node folder run 'node server.js'

    -   Open Android Studio

    -   Open Matcha as a project

    -   Create a file with the API key in /app/src/main/res/values

    -   Run 'app' using Android Studio's emulator

## Code Breakdown

-   Back end technologies:

    -   Java

    -   JavaScript

    -   Node.js

-   Front end technologies:

    -   XML

-   Libraries/ Dependencies:

    -   Facebook API

    -   Google API

    -   OKHTTP

    -   SquareUp Picasso

-   Database Management Systems

    -   MySQL

    -   PhpMyAdmin

-   app/src/main/java

    -   Account.java

        -   Secure user login area. Made up of 3 fragments: User
            Profile, Matcha and Settings.

    -   ChangePassword.java

        -   Send user email to change their password.

    -   CreateProfile.java

        -   On registration the user is directed to this page to enter
            further user profile information.

    -   EditProfile.java

        -   Allows user to alter their user information.

    -   ForgotPW.java

        -   Send email to enter new password.

    -   FragMatcha.java

        -   Account fragment, which displays suitable matches.

    -   FragSettings.java

        -   Account fragment, where the user can enter their sort and
            filter preferences.

    -   FragUserProfile.java

        -   Account fragment, which displays the user's profile.

    -   GeoHash.java

        -   Enables geo-hashing functionality

    -   JsonPlaceHolderAPI.java

        -   Enables GET and POST functionality when sending and
            receiving information using HTTP protocol.

    -   LatLong.java

        -   Convert LatLong geo-location to latitude and longitude.

    -   MainActivity.java

        -   Landing page to the web-application. Allows user to log in
            to their profile.

    -   Post.java

        -   Post class to POST and GET posts using HTTP protocol

    -   Register.java

        -   Register page for user to create a new profile.

    -   SettingActivity.java

        -   Settings drop-down menu functions in the account area.

    -   User.java

        -   User class to store and manipulate the user information.

-   app/src/main/res/layout

    -   activity_account.xml

        -   Account page front-end

    -   activity_changepassword.xml

        -   Change Password page front-end

    -   activity_createprofile.xml

        -   Create Profile page front-end

    -   activity_editprofile.xml

        -   Edit profile page front-end

    -   activity_forgot_pw.xml

        -   Forgot Password page front-end

    -   activity_main.xml

        -   Main Activity page front-end

    -   activity_register.xml

        -   Register page front-end

    -   fragment_matcha.xml

        -   Matcha fragment front-end

    -   fragment_settings.xml

        -   Settings fragment front-end

    -   fragment_userprofile.xml

        -   User Profile fragment front-end

-   node

    -   app.js

        -   Acts as router redirecting POST and GET

    -   router_get.js

        -   Fetches user profiles from User.json file and send them to
            the GET request, when using HTTP protocol.

    -   router_post.js

        -   Receives user profiles from the POST request, when using
            HTTP protocol and sends them to be added in the database.

    -   server.js

        -   Creates a RESTful API server

-   SQL

    -   Index.php

        -   Receives user profiles from the RESTful API server and adds
            them to the SQL database.

## Testing

<https://github.com/wethinkcode-students/corrections_42_curriculum/blob/master/matcha.markingsheet.pdf>
