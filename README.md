# gamified-expense-manager

A gamified personal expense management application built using the Spring Framework. This project is designed with an MVC architecture and integrates a database to help users build better financial habits through a system of points, levels, and achievements.

## Features

- **Expense Management**: Full CRUD functionality to add, view, edit, and delete personal expenses.
- **Interactive Dashboard**: A central dashboard that provides a quick overview of financial activity, including:
    - Spending totals for today, the current week, and the current month.
    - A donut chart visualizing spending distribution by category.
    - A line chart showing monthly spending trends over the last six months.
- **Gamification System**:
    - **Levels & Points**: Earn points for tracking expenses and unlocking achievements to level up.
    - **Achievements**: Unlock a variety of achievements like "Frugal Day" (no spending), "Consistent Tracker" (tracking for consecutive days), and "Category Master" (using all expense categories).
    - **Progress Tracking**: View unlocked achievements, progress on locked ones, and level progression.
- **Automated Checks**: A daily scheduled task checks for no-spend days and updates tracking streaks automatically.

## Technologies Used

- **Backend**: Java 17, Spring Boot (Web, Data JPA, Thymeleaf)
- **Database**: H2 In-Memory Database
- **Frontend**: Thymeleaf, HTML, CSS, Chart.js for data visualization
- **Build Tool**: Apache Maven

## Project Structure

The application follows a standard Model-View-Controller (MVC) pattern:

- **`controller`**: Handles incoming web requests and orchestrates responses.
  - `DashboardController`: Manages the main dashboard view.
  - `PersonalExpenseController`: Manages CRUD operations for expenses.
  - `GamificationController`: Manages views for achievements and user progress.
- **`model`**: Defines the data structure with JPA entities.
  - `PersonalExpense`: Represents a single expense entry.
  - `Achievement`: Defines an unlockable achievement.
  - `UserAchievement`: Tracks a user's progress towards an achievement.
  - `UserStats`: Stores user-specific gamification data like points and level.
- **`service`**: Contains the core business logic.
  - `PersonalExpenseService`: Handles calculations and data manipulation for expenses. It uses an `ApplicationEventPublisher` to decouple the expense logging from the gamification logic.
  - `GamificationService`: Manages all gamification logic, including awarding points, checking for achievements, and handling daily scheduled checks.
- **`repository`**: Data access layer using Spring Data JPA.
- **`templates`**: Thymeleaf templates for rendering the user interface.

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

- Java Development Kit (JDK) 17 or later
- Apache Maven

### Installation & Running

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/jay007mach/gamified-expense-manager.git
    ```

2.  **Navigate to the project directory:**
    ```sh
    cd gamified-expense-manager
    ```

3.  **Run the application using the Maven wrapper:**
    - On macOS/Linux:
      ```sh
      ./mvnw spring-boot:run
      ```
    - On Windows:
      ```sh
      .\mvnw.cmd spring-boot:run
      ```

4.  **Access the application:**
    Open your web browser and go to `http://localhost:8080`.

### Database Access

The application uses an in-memory H2 database. You can access its console to view the data and schema.

-   **URL**: `http://localhost:8080/h2-console`
-   **JDBC URL**: `jdbc:h2:mem:expensedb`
-   **Username**: `sa`
-   **Password**: (leave blank)
