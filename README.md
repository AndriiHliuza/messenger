<a name="readme-top"></a>
<h1 align="center"><b>WebTalk Messenger</b></h1>


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#description">About The Project</a>
    </li>
    <li>
      <a href="#projects-functionality-and-features">Project's Functionality and Features</a>
    </li>
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
1. Clone the repository.
```
git clone https://github.com/AndriiHliuza/messenger.git
```

2. Navigate to the project's directory.
```
cd messenger
```

3. Build project to jar file
```
./gradlew build
```

4. Set environment variable
- DATABASE_URL (database url)
- DATABASE_USERNAME (database user username)
- DATABASE_PASSWORD (database password)
- FLYWAY_USERNAME (database user username)
- FLYWAY_PASSWORD (database password)
- EMAIL_HOST (smtp.gmail.com for gmail)
- EMAIL_PORT (587 for gmail)
- EMAIL_USERNAME (Messenger email)
- EMAIL_APP_PASSWORD (Messenger email password)
- JWT_SECRET_ENCRYPTION_KEY (Key to encrypt jwts)
- ENCRYPTION_AES_KEY (AES key for encryption)
- APP_ROOT_USERNAME (ROOT username)
- APP_ROOT_PASSWORD (ROOT password)
- APP_ROOT_UNIQUE_NAME (ROOT uniqueName)
- APP_ROOT_FIRST_NAME (ROOT firstname)
- APP_ROOT_LAST_NAME (ROOT lastname)
- APP_ADMIN_USERNAME (ADMIN email)
- APP_ADMIN_PASSWORD (ADMIN password)
- APP_MESSENGER_ADMIN_UNIQUE_NAME (ROOT uniqueName)
- APP_ROOT_FIRST_NAME (ROOT firstname)
- APP_ROOT_LAST_NAME (ROOT lastname)
- FROM_EMAIL (Messenger email)
- CORS_ORIGINS_URL (FRONT-END project url)

5. Run docker-compose.yml
```
docker-compose up -d
```

5. Run application
```
java -jar build/libs/messenger.jar
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>