# How to Access the Database

## Database Location

The database file is located at:
```
C:\Users\felix\AndroidStudioProjects\BusTraveller\backend\database.sqlite
```

This file is created automatically when you start the backend server for the first time.

## Method 1: Web Interface (Easiest) ⭐ Recommended

### Steps:

1. **Start the backend server:**
   ```bash
   cd C:\Users\felix\AndroidStudioProjects\BusTraveller\backend
   npm start
   ```

2. **Open your web browser** and go to:
   ```
   http://localhost:3000/admin-management.html
   ```

3. **Login** with:
   - Username: `admin`
   - Password: `admin123`

4. **You can now:**
   - ✅ View all admins in a table
   - ✅ Add new admin users
   - ✅ Delete admin users
   - ✅ See all data in a nice interface

## Method 2: Command Line Script

### View All Admins:
```bash
cd C:\Users\felix\AndroidStudioProjects\BusTraveller\backend
node add-admin.js list
```

### Add New Admin:
```bash
cd C:\Users\felix\AndroidStudioProjects\BusTraveller\backend
node add-admin.js add
```
Then enter username and password when prompted.

### Delete Admin:
```bash
cd C:\Users\felix\AndroidStudioProjects\BusTraveller\backend
node add-admin.js delete
```

## Method 3: SQLite Command Line

### Step 1: Install SQLite (if needed)

**Windows:**
- Download from: https://www.sqlite.org/download.html
- Or use: `choco install sqlite` (if you have Chocolatey)

### Step 2: Open the Database

```bash
cd C:\Users\felix\AndroidStudioProjects\BusTraveller\backend
sqlite3 database.sqlite
```

### Step 3: View Data

**View all admins:**
```sql
SELECT * FROM admin;
```

**View all vehicles:**
```sql
SELECT * FROM vehicles;
```

**View all parcels:**
```sql
SELECT * FROM parcels;
```

**View table structure:**
```sql
.schema admin
.schema vehicles
.schema parcels
```

### Step 4: Exit SQLite

```sql
.exit
```

## Method 4: Database Browser (GUI Tool)

### Option A: DB Browser for SQLite (Recommended)

1. **Download:** https://sqlitebrowser.org/
2. **Install** the application
3. **Open DB Browser**
4. **Click "Open Database"**
5. **Navigate to:** `C:\Users\felix\AndroidStudioProjects\BusTraveller\backend\database.sqlite`
6. **Click "Browse Data"** tab to view tables
7. **Click "Execute SQL"** tab to run queries

### Option B: SQLiteStudio

1. **Download:** https://sqlitestudio.pl/
2. **Install** the application
3. **Open SQLiteStudio**
4. **Add Database** → Select `database.sqlite` file
5. **Browse** tables and data

## Quick Reference: Database Tables

### Admin Table
- `id` - Unique ID
- `username` - Admin username
- `password` - Hashed password (SHA-256)
- `createdAt` - Creation timestamp

### Vehicles Table
- `id` - Vehicle ID
- `name` - Vehicle name
- `latitude`, `longitude` - Location
- `routeNumber` - Route number
- `status` - Vehicle status
- And more...

### Parcels Table
- `id` - Parcel ID
- `name` - Parcel name
- `latitude`, `longitude` - Location
- `trackingNumber` - Tracking number
- `status` - Parcel status
- And more...

## Common SQL Queries

### View All Admins:
```sql
SELECT id, username, datetime(createdAt/1000, 'unixepoch') as created FROM admin;
```

### Count Records:
```sql
SELECT COUNT(*) FROM admin;
SELECT COUNT(*) FROM vehicles;
SELECT COUNT(*) FROM parcels;
```

### Find Specific Admin:
```sql
SELECT * FROM admin WHERE username = 'admin';
```

### View Recent Vehicles:
```sql
SELECT * FROM vehicles ORDER BY lastUpdateTime DESC LIMIT 10;
```

## Important Notes

⚠️ **Before Editing Database:**
- Make sure the backend server is **stopped** (press Ctrl+C)
- Or use the web interface/API which handles it safely

⚠️ **Password Hashing:**
- Passwords are stored as SHA-256 hashes
- To add a password manually, hash it first:
  ```bash
  node -e "const crypto = require('crypto'); console.log(crypto.createHash('sha256').update('yourpassword').digest('hex'));"
  ```

## Which Method Should I Use?

- **For quick admin management:** Use Web Interface (Method 1)
- **For command line:** Use Script (Method 2)
- **For advanced queries:** Use SQLite Command Line (Method 3)
- **For visual browsing:** Use Database Browser (Method 4)


