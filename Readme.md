# Student Management System

Short description
- A simple Java desktop application to manage student records using a MySQL database.
- Includes database initialization, a Student DAO, validation, and a Swing GUI.

Important: sensitive configuration
- Do NOT upload sensitive configuration to GitHub. Keep a local `config.properties` out of the repository.
- This project contains an example file: `src/main/resources/config.example.properties`.
  - Create a local configuration file from the example and fill your credentials:

```properties
# copy this file to `src/main/resources/config.properties` and fill values
db.url=jdbc:mysql://localhost:3306/student_db
db.user=YOUR_DB_USER
db.password=YOUR_DB_PASSWORD
```

Make sure `src/main/resources/config.properties` is listed in `.gitignore` so it will not be committed.

Dependencies
- The project uses MySQL Connector/J: `lib/mysql-connector-j-8.0.33.jar`.
- You can either keep the JAR under `lib/` (already present) or use a dependency manager such as Maven/Gradle.

Running in IntelliJ

1. Open the project folder in IntelliJ: `File  Open` and select the project root.
2. Mark `src` as Sources Root: right-click `src`  `Mark Directory as`  `Sources Root`.
3. Set the Project SDK to a valid JDK (11 or 17): `File  Project Structure  Project`  `Project SDK`.
4. If IntelliJ does not detect the MySQL connector automatically, add it: `File  Project Structure  Libraries  +  Java` and select `lib/mysql-connector-j-8.0.33.jar`.
5. Create the local config file by copying `config.example.properties` to `config.properties` and updating the values.
6. Run the application: right-click `src/view/Main.java`  `Run 'Main'`. The application will attempt to initialize the database using the `config.properties` values.

Notes and best practices
- Keep `config.properties` local and excluded by `.gitignore`.
- Prefer using a dependency manager (Maven/Gradle) instead of checking large JARs into the repo.
- It's uncommon to commit `out/` (build output) or IDE settings (`.idea/`, `*.iml`) unless you intend to share project-level IDE configuration.

Author
- Oussama Sghir
