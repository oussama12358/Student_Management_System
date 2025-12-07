# Student Management System

A Java project for managing student data using a MySQL database.
Includes database creation, student table setup, and CRUD operations (Create, Read, Update, Delete).

## Features
- Connect to MySQL database using JDBC
- Automatic database and table initialization
- Manage students: name, email, major, GPA, and enrollment date
  






<img width="1227" height="738" alt="image" src="https://github.com/user-attachments/assets/52d82a07-c712-4540-8957-ccecaf03ed80" />











## Getting Started

1. The project expects a `config.properties` file to be present on the classpath (recommended in `src/main/resources`) or in the project root (this file should **not** be uploaded to GitHub).

	 - A `config.properties` file with default values has been included in `src/main/resources` for convenience. Update the values to match your local MySQL installation if needed:

	 ```properties
	 db.url=jdbc:mysql://localhost:3306/student_db
	 db.user=root
	 db.password=YOUR_PASSWORD
	 ```

2. Run `DatabaseConnection.initializeDatabase()` (the GUI calls it automatically on startup) to initialize the database and tables.
3. Use the classes in the `src/` folder to perform CRUD operations on students.

## Notes
- Make sure `config.properties` is listed in `.gitignore` to prevent sensitive data from being uploaded to GitHub.
- The project uses the MySQL Connector/J library (`lib/mysql-connector-j-8.0.33`), which is also ignored by Git.

## Running in IntelliJ

- Open the project directory in IntelliJ (`File → Open` → select project root).
- Make sure the `src` folder is marked as `Sources Root` (right-click `src` → `Mark Directory as → Sources Root`).
- Set the Project SDK to a valid JDK (11 or 17) under `File → Project Structure → Project`.
- Add the MySQL connector jar as a library if IntelliJ doesn't pick it automatically: `File → Project Structure → Libraries → + → Java → select lib/mysql-connector-j-8.0.33.jar`.
- Run the `view.Main` class (right-click `Main.java` → Run) — the app will attempt to initialize the database using `config.properties`.

## Author
Oussama Sghir
