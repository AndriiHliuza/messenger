<a name="readme-top"></a>
<h1 align="center"><b>WebTalk Messenger</b></h1>


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#prerequisites">Prerequisites</a></li>
    <li><a href="#installation-steps">Installation Steps</a></li>
  </ol>
</details>


<!-- About The Project -->
## About The Project
<p>This repository is a BACK-END ReactJS part of a safe and secure messeneger project for a real-time communication between users.</p>
<p>
  Safety of communication is achieved by encrypting messages before sending them to other users. For this purpose messenger uses a combination of asymmetric and symmetric encryption methods.
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>


## Prerequisites

<ul>
  <li>Java 17+ recommended</li>
  <li>Docker</li>
</ul>

<p align="right">(<a href="#readme-top">back to top</a>)</p>


## Installation steps

1. Set these environment variables on your system:
<ul>
    <li>
        <p><code>DATABASE_URL</code></p>
        <p>This variable is needed to connect to the database where application data will be stored.</p>
        <p>For provided docker-compose.yml usage: <code>DATABASE_URL=jdbc:postgresql://localhost:5432/postgres</code></p>
        <br/>
    </li>
    <li>
        <p><code>DATABASE_USERNAME</code></p>
        <p>Provide DB's user username to connect to database.</p>
        <p>For provided docker-compose.yml usage: <code>DATABASE_USERNAME=postgres</code></p>
        <br/>
    </li>
    <li>
        <p><code>DATABASE_PASSWORD</code></p>
        <p>Provide DB's user password to connect to database.</p>
        <p>For provided docker-compose.yml usage: <code>DATABASE_PASSWORD=postgres</code></p>
        <br/>
    </li>
    <li>
        <p><code>FLYWAY_USERNAME</code></p>
        <p>Provide DB's user username to connect to database.</p>
        <p>For provided docker-compose.yml usage: <code>FLYWAY_USERNAME=postgres</code></p>
        <br/>
    </li>
    <li>
        <p><code>FLYWAY_PASSWORD</code></p>
        <p>Provide DB's user password to connect to database.</p>
        <p>For provided docker-compose.yml usage: <code>FLYWAY_PASSWORD=postgres</code></p>
        <br/>
    </li>
    <li>
        <p><code>EMAIL_HOST</code></p>
        <p>These variable is needed to send emails with account activation code on successful user registration.</p>
        <p>For Gmail: <code>EMAIL_HOST=smtp.gmail.com</code></p>
        <br/>
    </li>
    <li>
        <p><code>EMAIL_PORT</code></p>
        <p>Port for the used host.</p>
        <p>For Gmail: <code>EMAIL_PORT=587</code></p>
        <br/>
    </li>
    <li>
        <p><code>EMAIL_USERNAME</code></p>
        <p>Username of your email address.</p>
        <p>For Gmail: <code>EMAIL_USERNAME=johnsmith@gmail.com</code></p>
        <br/>
    </li>
    <li>
        <p><code>EMAIL_APP_PASSWORD</code></p>
        <p>App password for your gmail host. </p>
        <p>If you use Gmail, you can create your app password in your account settings.</p>
        <br/>
    </li>
    <li>
        <p><code>JWT_SECRET_ENCRYPTION_KEY</code></p>
        <p>Provide HMAC SHA-256 code for this value. It is used to generate JWT that are used for user's authentication and authorization</p>
        <p>Recommended size of the code is 32 or 64 characters when represented in hexadecimal format.</p>
        <p>For test purposes you can use the code provided in the <code>application-dev.yml</code> file.</p>
        <p>Or you can use this repository to generate your own HMAC SHA-256 code: <a href="https://github.com/AndriiHliuza/key-generator">KEY-GENERATOR</a></p>
        <br/>
    </li>
    <li>
        <p><code>ENCRYPTION_AES_KEY</code></p>
        <p>Provide AES key for this value. It is used for symmetric encryption of data before storing it to database or sending it to the frontend application.</p>
        <p>Recommended size of the code is 128, 192 or 256 bits.</p>
        <p>For test purposes you can use the code provided in the <code>application-dev.yml</code> file.</p>
<p>Or you can use this repository to generate your own AES key: <a href="https://github.com/AndriiHliuza/key-generator">KEY-GENERATOR</a></p>
        <br/>
    </li>
    <li>
        <p><code>APP_ROOT_USERNAME</code>, <code>APP_ROOT_PASSWORD</code>, <code>APP_ROOT_UNIQUE_NAME</code>, <code>APP_ROOT_FIRST_NAME</code>, <code>APP_ROOT_LAST_NAME</code></p>
        <p>These variable sets the email as username, password, uniqueName as username, firstname and lastname for the ROOT user in the application.</p>
        <p>ROOT is type of the ADMIN user in application, but with higher privileges then regular ADMIN.</p>        
        <p>ROOT user is created on application startup.</p>
        <br/>
    </li>
    <li>
        <p><code>APP_ADMIN_USERNAME</code>, <code>APP_ADMIN_PASSWORD</code>, <code>APP_ADMIN_UNIQUE_NAME</code>, <code>APP_ADMIN_FIRST_NAME</code>, <code>APP_ADMIN_LAST_NAME</code></p>
        <p>These variable sets the email as username, password, uniqueName as username, firstname and lastname for the ADMIN user in the application.</p> 
        <p>ADMIN user is created on application startup.</p>
        <br/>
    </li>
    <li>
        <p><code>FROM_EMAIL</code></p>
        <p>This variable is needed to set the email address that sends emails with activation codes for accounts.</p> 
        <p>For Gmail: <code>FROM_EMAIL=johnsmith@gmail.com</code></p>
        <br/>
    </li>
    <li>
        <p><code>CORS_ORIGINS_URL</code></p>
        <p>Provide link to your front-end project here.</p> 
        <p>For example: <code>http://localhost:3000</code></p>
        <p>Follow this link to set-up the front-end part of the application: <a href="https://github.com/AndriiHliuza/messenger-frontend-app">FRONT-END</a></p>
        <br/>
    </li>
</ul>

2. Clone the repository.
```
git clone https://github.com/AndriiHliuza/messenger.git
```

3. Navigate to the project's directory.
```
cd messenger
```

4. Build project
```
./gradlew build
```

5. Run docker-compose.yml
```
docker-compose up -d
```

5. Run application
```
java -jar build/libs/messenger.jar
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>