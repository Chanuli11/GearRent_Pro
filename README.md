# GearRent Pro — Multi-Branch Equipment Rental System

A Java + JavaFX + MySQL desktop application built for **CMJD Course Work 2 (IJSE)**.
It manages branches, equipment, categories, customers, reservations, rentals,
returns, overdue tracking, and reports — using a strict **layered architecture**
(Entity → DAO → Service → Controller → UI).

---

## 1. Project Structure

```
GearRentPro/
├── pom.xml                 ← Maven build file (handles JavaFX + MySQL connector)
├── database.sql            ← Full MySQL schema + sample data
├── README.md
└── src/main/
    ├── java/com/gearrent/
    │   ├── entity/         ← Branch, Category, Customer, Equipment, Rental, etc.
    │   ├── dao/            ← JDBC database access (one file per entity)
    │   ├── service/        ← Business logic (pricing, transactions, validation)
    │   ├── controller/     ← JavaFX FXML controllers
    │   ├── ui/             ← MainApp + Launcher
    │   └── util/           ← DBConnection, DateUtil, ValidationUtil, Session, AlertUtil
    └── resources/ui/       ← All .fxml screens
```

---

## 2. Prerequisites

Install these on your computer:

1. **JDK 17 or higher** — https://adoptium.net
2. **MySQL Server 8.x** — https://dev.mysql.com/downloads/mysql/
3. **IntelliJ IDEA Community** (recommended) — https://www.jetbrains.com/idea/download/
4. **Maven** (bundled with IntelliJ — nothing extra needed)
5. **Git** — https://git-scm.com

---

## 3. Step-by-Step Setup

### Step 1 — Create the database

Open **MySQL Workbench** (or any MySQL client) and run the file `database.sql`.
It will:

* drop & recreate the `gearrent_pro` database,
* create all tables,
* insert 3 branches, 5 categories, 21 equipment items, 10 customers,
  sample rentals (active / overdue / returned / damaged) and reservations,
* create 6 system users covering all 3 roles.

### Step 2 — Configure DB credentials

Open `src/main/java/com/gearrent/util/DBConnection.java` and change the
`USER` / `PASS` constants to match your local MySQL setup. Default:

```java
private static final String USER = "root";
private static final String PASS = "root";
```

### Step 3 — Open the project in IntelliJ

1. `File → Open` → select the `GearRentPro` folder.
2. IntelliJ will detect the `pom.xml` and download dependencies automatically
   (JavaFX 21 + MySQL Connector 8.0.33). Wait for indexing to finish.
3. Make sure **Project SDK** is set to JDK 17+ (`File → Project Structure → Project`).

### Step 4 — Run the application

Two ways:

**A. Maven (recommended):**
```
mvn clean javafx:run
```

**B. From IntelliJ:**
Right-click `src/main/java/com/gearrent/ui/Launcher.java` → **Run 'Launcher.main()'**.

> Why a `Launcher` class? JavaFX modules require a non-`Application`
> entry point when launched from the classpath — `Launcher` just calls
> `MainApp.main(args)`.

### Step 5 — Push to GitHub

```bash
cd GearRentPro
git init
git add .
git commit -m "Initial commit: GearRent Pro complete project"
git branch -M main
git remote add origin https://github.com/<your-username>/GearRent-Pro.git
git push -u origin main
```

---

## 4. Default Login Credentials

| Role            | Username   | Password     | Branch       |
|-----------------|------------|--------------|--------------|
| Admin           | `admin`    | `admin123`   | All branches |
| Branch Manager  | `manager1` | `manager123` | Panadura Main |
| Branch Manager  | `manager2` | `manager123` | Galle Coastal |
| Staff           | `staff1`   | `staff123`   | Panadura Main |
| Staff           | `staff2`   | `staff123`   | Galle Coastal |
| Staff           | `staff3`   | `staff123`   | Colombo Hub  |

---

## 5. Feature Map (matches assignment requirements)

| Requirement                                 | Where it lives                                                |
|---------------------------------------------|---------------------------------------------------------------|
| Login with role-based access                | `LoginController` + `DashboardController`                     |
| Manage Branches (Admin only)                | `BranchController` + `BranchManagement.fxml`                  |
| Manage Categories (Admin/Manager)           | `CategoryController` + `CategoryManagement.fxml`              |
| Manage Equipment + filter/search            | `EquipmentController` + `EquipmentManagement.fxml`            |
| Manage Customers + active deposit display   | `CustomerController` + `CustomerManagement.fxml`              |
| Membership configuration                    | `MembershipConfigController`                                  |
| Reservations + overlap check                | `ReservationService` (transactional)                          |
| Rentals + price calc + 30-day max + deposit limit | `RentalService` + `RentalPriceService`                  |
| Convert Reservation → Rental                | `RentalService.convertReservationToRental()`                  |
| Returns: late fee + damage + deposit settlement | `ReturnService` (transactional)                           |
| Overdue rentals view                        | `OverdueController`                                           |
| Branch revenue report                       | `ReportService.branchRevenue()`                               |
| Equipment utilization report                | `ReportService.equipmentUtilization()`                        |
| Input validation                            | `ValidationUtil`, throws `IllegalArgumentException`           |
| Transactions (rental, return, reservation)  | `try (Connection c) { c.setAutoCommit(false); ... commit/rollback }` |
| Concurrency (status check inside txn)       | `RentalDAO.hasOverlap()` runs inside same transaction         |

---

## 6. Pricing Formula (implemented in `RentalPriceService`)

```
weekday_price = equipment.daily_base_price × category.base_price_factor
weekend_price = weekday_price × category.weekend_multiplier
rental_amount = (weekday_price × weekday_count) + (weekend_price × weekend_count)

if days >= 7:  long_rental_disc = 10% of rental_amount
membership_disc = (rental_amount - long_rental_disc) × membership.discount_pct
final_payable   = rental_amount - long_rental_disc - membership_disc
```

The configurable deposit limit per customer (default **LKR 500,000**)
lives in `RentalService.DEPOSIT_LIMIT`.

---

## 7. Troubleshooting

| Problem | Fix |
|---|---|
| `Communications link failure` | MySQL not running, or wrong port — check `DBConnection.java` URL. |
| `Access denied for user 'root'` | Wrong password in `DBConnection.java`. |
| `Error: JavaFX runtime components are missing` | You ran `MainApp.main()` directly — use `Launcher.main()` or `mvn javafx:run`. |
| FXML "Location is not set" | Make sure `src/main/resources` is marked as **Resources Root** in IntelliJ. |
| No data shows | Re-run `database.sql` in MySQL Workbench. |

---

## 8. Submission Checklist

- [x] GitHub repo with full source code
- [x] `database.sql` with schema + sample data (3 branches, 5 categories, 21 equipment, 10 customers, sample rentals incl. overdue + damaged)
- [x] `README.md` with setup instructions and default credentials
- [x] Layered architecture (Entity / DAO / Service / Controller / UI)
- [x] JDBC + transactions for critical operations
- [x] Input validation with friendly error messages
- [x] Role-based access control after login
