# Database Guide - Adding Admin Users

## Database Location

The SQLite database file is located at:
```
backend/database.sqlite
```

This file is created automatically when you start the backend server for the first time.

## Adding Admin Users

### Method 1: Using the Utility Script (Recommended)

Use the provided script to add, list, or delete admin users:

#### Add a new admin:
```bash
cd backend
node add-admin.js add
```

The script will prompt you for:
- Username
- Password

#### List all admins:
```bash
node add-admin.js list
```

#### Delete an admin:
```bash
node add-admin.js delete
```

### Method 2: Using SQLite Command Line

1. **Install SQLite** (if not already installed):
   - Windows: Download from https://www.sqlite.org/download.html
   - Mac: `brew install sqlite`
   - Linux: `sudo apt-get install sqlite3`

2. **Open the database**:
   ```bash
   cd backend
   sqlite3 database.sqlite
   ```

3. **View existing admins**:
   ```sql
   SELECT id, username, createdAt FROM admin;
   ```

4. **Add a new admin** (password will be hashed):
   ```sql
   -- Replace 'newadmin' and 'password123' with your desired credentials
   -- The password is hashed using SHA-256
   INSERT INTO admin (username, password, createdAt) 
   VALUES ('newadmin', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', strftime('%s', 'now') * 1000);
   ```
   
   **Note:** To hash a password, you can use Node.js:
   ```bash
   node -e "const crypto = require('crypto'); console.log(crypto.createHash('sha256').update('yourpassword').digest('hex'));"
   ```

5. **Update an admin password**:
   ```sql
   -- First, hash your new password using the command above
   UPDATE admin SET password = 'hashed_password_here' WHERE username = 'admin';
   ```

6. **Delete an admin**:
   ```sql
   DELETE FROM admin WHERE username = 'username_to_delete';
   ```

7. **Exit SQLite**:
   ```sql
   .exit
   ```

### Method 3: Using a Database Browser

You can use a GUI tool like:
- **DB Browser for SQLite** (https://sqlitebrowser.org/)
- **SQLiteStudio** (https://sqlitestudio.pl/)

1. Open the tool
2. Open the database file: `backend/database.sqlite`
3. Navigate to the `admin` table
4. Add/edit records manually

**Important:** When adding passwords manually, you must hash them first using SHA-256.

## Default Admin Account

When the server starts for the first time, it automatically creates:
- **Username:** `admin`
- **Password:** `admin123`

## Password Hashing

Passwords are stored as SHA-256 hashes. To hash a password:

**Using Node.js:**
```bash
node -e "const crypto = require('crypto'); console.log(crypto.createHash('sha256').update('yourpassword').digest('hex'));"
```

**Using Python:**
```python
import hashlib
print(hashlib.sha256('yourpassword'.encode()).hexdigest())
```

## Database Schema

The `admin` table structure:
```sql
CREATE TABLE admin (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    createdAt INTEGER NOT NULL
);
```

## Security Notes

⚠️ **Important Security Considerations:**

1. **Change the default password** immediately in production
2. **Use strong passwords** for admin accounts
3. **Don't commit** the database file to version control (it's in `.gitignore`)
4. **Consider using** bcrypt or Argon2 instead of SHA-256 for production (more secure)
5. **Limit admin access** to trusted users only

## Troubleshooting

### Database file not found
- Make sure you've started the server at least once
- Check that you're in the `backend` directory
- The file should be created automatically on first server start

### Cannot add admin
- Check that the username is unique
- Ensure the database file has write permissions
- Make sure the server isn't locking the database file

### Forgot admin password
- Use the `add-admin.js` script to update the password
- Or use SQLite to update the password hash directly

