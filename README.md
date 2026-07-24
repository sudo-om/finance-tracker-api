# ⚡ FIN-TRACK: Neo-Brutalist Finance Tracker & Telegram Bot Sync

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge&logo=openjdk)](https://oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.0-green.svg?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.0-blue.svg?style=for-the-badge&logo=react)](https://reactjs.org/)
[![Vite](https://img.shields.io/badge/Vite-5.4-purple.svg?style=for-the-badge&logo=vite)](https://vitejs.dev/)
[![CI/CD Pipeline](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-2088FF.svg?style=for-the-badge&logo=githubactions)](https://github.com/sudo-om/finance-tracker-api/actions)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-black.svg?style=for-the-badge)](LICENSE)

> A full-stack, high-performance financial management platform featuring a bold **Neo-Brutalist Web Dashboard**, **Real-Time Telegram Bot Synchronization**, and automated **GitHub Actions CI/CD Pipelines**. Record transactions, track budgets, and receive instant spending alerts straight from Telegram or your desktop browser.

---

## 📸 Screenshots & UI Previews

### 1. Financial Overview Dashboard
The main dashboard provides real-time balance metrics, active category budget progress bars with status tags (`ON_TRACK`, `WARNING`, `OVER_BUDGET`), and recent transaction records.

![Financial Overview Dashboard](docs/images/dashboard_preview.png)

### 2. Telegram Bot Control Center
Generate a secure, 1-time 8-character OTP code to link your Telegram account in under 10 seconds. Includes a live countdown timer and interactive command cheatsheet.

![Telegram Bot Synchronization](docs/images/telegram_sync_preview.png)

---

## 📂 Repository File Structure

```
finance-tracker-api/
├── .github/
│   └── workflows/
│       ├── ci.yml                    # CI/CD Pipeline (Build, Test & Artifact Validation)
│       └── release.yml               # Automated GitHub Production Release Pipeline
├── docs/
│   └── images/                       # Screenshots and visual previews for documentation
│       ├── dashboard_preview.png
│       └── telegram_sync_preview.png
├── frontend/                         # Vite + React Neo-Brutalist Web App
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   │   ├── Navbar.jsx            # Top navigation bar & Gen-Z Dark Mode toggle
│   │   │   ├── AuthView.jsx          # Login, Register & View Password toggle
│   │   │   ├── DashboardView.jsx     # Financial summary cards & budget progress
│   │   │   ├── TransactionsView.jsx  # Expense/Income manager & dynamic category modals
│   │   │   ├── BudgetsView.jsx       # Budget tracker & category limit modals
│   │   │   └── TelegramView.jsx      # Telegram OTP generator & command cheatsheet
│   │   ├── api.js                    # API client with token & response unwrapping
│   │   ├── App.jsx                   # Main application shell
│   │   └── index.css                 # Gen-Z Neo-Brutalist CSS design system tokens
│   ├── package.json
│   └── vite.config.js
├── src/                              # Spring Boot REST API & Telegram Engine
│   └── main/java/com/financetracker/finance_tracker_api/
│       ├── config/                   # SecurityConfig, CorsConfig, CategoryDataInitializer
│       ├── controller/               # AuthController, ExpenseController, BudgetController, TelegramController, CategoryController
│       ├── dto/                      # Request & Response Data Transfer Objects
│       ├── entity/                   # JPA Entities (User, Expense, Income, Budget, Category, TelegramLinkCode)
│       ├── exception/                # GlobalExceptionHandler & custom exceptions
│       ├── mapper/                   # MapStruct mappers
│       ├── repository/               # JPA Repositories with EntityGraph annotations
│       ├── security/                 # JwtService, JwtAuthenticationFilter, CustomUserDetailsService
│       ├── service/                  # Service interfaces & implementations
│       └── telegram/                 # TelegramScheduler polling engine & TelegramCommandParser
├── .gitignore                        # Standard Git exclusion rules
├── Dockerfile                        # Multi-stage production container build
├── docker-compose.yml                # Multi-container service setup (PostgreSQL + API)
├── pom.xml                           # Maven dependencies & build metadata
└── README.md                         # Main repository documentation
```

---

## ⚙️ GitHub Actions CI/CD Pipeline

The project features a **2-stage GitHub Actions Automation Pipeline** located in `.github/workflows/`:

### 1. Continuous Integration (`.github/workflows/ci.yml`)
Triggers on every `push` and `pull_request` to `main` / `master`:
- **☕ Backend CI (Java 21 / Spring Boot)**:
  - Sets up JDK 21 Temurin with Maven dependency caching.
  - Compiles source files (`./mvnw compile`).
  - Runs automated unit & integration test suites (`./mvnw test`).
  - Packages production JAR (`./mvnw package -DskipTests`) and stores it as a workflow artifact.
- **⚡ Frontend CI (Node.js 20 / React 18)**:
  - Sets up Node.js 20 with `npm` dependency caching.
  - Installs packages (`npm ci`).
  - Builds production Vite static bundle (`npm run build`).
  - Uploads `dist/` build bundle as a workflow artifact.
- **🐳 Docker Validation**:
  - Validates multi-stage `Dockerfile` image builds automatically.

### 2. Production Release (`.github/workflows/release.yml`)
Triggers on tag creation (`v*`) or manual execution (`workflow_dispatch`):
- Packages release binaries (`.jar` and `frontend-dist.zip`).
- Publishes automated GitHub Release notes and downloadable assets.

---

## 🔥 Key Features

- **⚡ Real-Time Telegram Sync**: Log expenses or income via simple text messages (e.g. `/spent 450 Food Domino's` or `/income 50000 Salary`). The bot immediately processes the transaction and sends instant notifications.
- **🎨 Gen-Z Neo-Brutalist Web UI**: Designed with high contrast, 3px solid black borders, hard drop shadows, and dark slate `#1E1E26` containers in Dark Mode.
- **📊 Budget & Threshold Monitoring**: Set weekly, monthly, or yearly spending limits per category. Visual progress bars indicate percent used, remaining allowance, and status alerts (`ON_TRACK`, `WARNING`, `OVER_BUDGET`).
- **📂 Dynamic Category Engine**: Auto-seeds default expense and income categories with icons and color tokens (`Food`, `Transport`, `Shopping`, `Bills`, `Salary`, etc.).
- **🔐 Secure JWT Authentication**: Stateless security architecture using JSON Web Tokens, BCrypt password hashing, and user-scoped data protection.

---

## 🤖 Telegram Bot Command Reference

Send any of the following commands to your configured Telegram Bot:

| Command | Arguments | Description | Example |
| :--- | :--- | :--- | :--- |
| `/start` | `[code]` | Start the bot or link account via deep link | `/start A3B7K9X2` |
| `/link` | `<code>` | Link your Telegram account using 1-time OTP code | `/link A3B7K9X2` |
| `/spent` | `<amount> <category> [description]` | Record a new expense transaction | `/spent 450 Food Domino's` |
| `/income` | `<amount> <category> [description]` | Record a new income deposit | `/income 50000 Salary` |
| `/balance` | None | Check current balance, total income, and total expense | `/balance` |
| `/summary` | None | Fetch monthly financial summary breakdown | `/summary` |
| `/categories` | None | List available expense & income categories | `/categories` |
| `/budgets` | None | View active category budgets and spending limits | `/budgets` |
| `/setbudget` | `<amount> <category> [period]` | Create or update a category budget limit | `/setbudget 5000 Food monthly` |

---

## 🚀 Getting Started & Local Setup

### Option 1: Using Docker Compose (Recommended)

1. Clone the repository:
   ```bash
   git clone https://github.com/sudo-om/finance-tracker-api.git
   cd finance-tracker-api
   ```

2. Start PostgreSQL & API using Docker Compose:
   ```bash
   docker-compose up -d --build
   ```

---

### Option 2: Manual Local Setup

#### Backend Setup
1. Configure credentials in `src/main/resources/application.yml` or set environment variables.
2. Build and run:
   ```bash
   ./mvnw clean spring-boot:run
   ```
   Backend API runs at **`http://localhost:8081`**.

#### Frontend Setup
1. Navigate to the `frontend` folder:
   ```bash
   cd frontend
   ```
2. Install & run:
   ```bash
   npm install
   npm run dev -- --host
   ```
   Frontend App runs at **`http://localhost:5173`**.

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  Made with ⚡ by <strong>Om Patil</strong>
</p>
