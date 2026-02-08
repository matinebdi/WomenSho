# CLAUDE.md - Project Context for Claude Code

## Project Overview
WomenShop is a JavaFX desktop application for managing a women's fashion store inventory (Clothes, Shoes, Accessories). It follows the MVC pattern with Oracle DB persistence.

## Tech Stack
- Java 21, JavaFX 17.0.2, Maven
- Oracle XE 21c via Docker (container: `oracle-xe`)
- JDBC with ojdbc8 19.3.0.0

## Database
- Connection: `jdbc:oracle:thin:@localhost:1521/XEPDB1` (user: `system`, password: `admin`)
- Config file: `src/main/java/com/womenshop/dao/DBConnection.java`
- Tables auto-created on startup by `DatabaseInitializer.java`
- Tables: `products`, `clothes`, `shoes`, `store_finances`

## Build & Run
```bash
mvn clean javafx:run
```

## Project Structure
```
src/main/java/com/womenshop/
├── MainApp.java                        # Entry point
├── controller/
│   ├── MainController.java             # Main window (table, actions, theme toggle)
│   └── ProductDialogController.java    # Add product dialog
├── dao/
│   ├── DBConnection.java               # DB connection config
│   ├── DatabaseInitializer.java        # Auto table creation
│   ├── ProductDAO.java                 # Product CRUD
│   └── FinanceDAO.java                 # Finance tracking
├── model/
│   ├── Product.java                    # Abstract base class (income, cost are static)
│   ├── Clothes.java                    # max 30% discount, has size
│   ├── Shoes.java                      # max 20% discount, has shoeSize
│   ├── Accessory.java                  # max 50% discount
│   └── Discount.java                   # Interface: applyDiscount(percent), unApplyDiscount(), getMaxDiscount()
└── util/
    └── AlertHelper.java                # Alert dialog helper

src/main/resources/
├── fxml/main_view.fxml                 # Main window layout
├── fxml/product_dialog.fxml            # Add product dialog layout
├── css/style.css                       # Light theme
└── css/style-dark.css                  # Dark theme
```

## Architecture Notes
- Product is abstract with 3 subclasses (Clothes, Shoes, Accessory) - polymorphism
- Discount is an interface implemented by each subclass with different rates
- `Product.income` and `Product.cost` are static fields tracking global store finances
- Profit = income - cost (no initial capital)
- Products can only be deleted when stock = 0
- Discounts are applied per selected product with a user-chosen %, capped by category max (Clothes 30%, Shoes 20%, Accessories 50%)
- Dark/light theme toggle via CSS swap on the Scene

## Color Palette
- `#F9F7F7` - Background (Snow White)
- `#DBE2EF` - Cards, table rows (Soft Blue)
- `#3F72AF` - Buttons, accents (Ocean Blue)
- `#112D4E` - Text, headers (Dark Navy)

## Module System
Uses JPMS (`module-info.java`). All packages are opened for JavaFX reflection (PropertyValueFactory needs this).

## Known Quirks
- Oracle getGeneratedKeys() doesn't work reliably with SYS/SYSTEM - using SELECT MAX(id) instead
- JavaFX TableView CSS: `.table-view` background must be transparent, white goes on `.virtual-flow`
- Oracle CDB vs PDB: tables must exist in XEPDB1 (PDB), not CDB$ROOT
