# Admin Management UI Guide

## Access the Admin Management Interface

Once your backend server is running, you can access the admin management web interface at:

```
http://localhost:3000/admin-management.html
```

## Features

### 1. **Login**
- Use your admin credentials to login
- Default credentials: `admin` / `admin123`
- Token is saved in browser localStorage for convenience

### 2. **View All Admins**
- See all admin users in a table format
- View ID, Username, and Creation Date
- Table automatically refreshes after adding/deleting

### 3. **Add New Admin**
- Enter username and password in the form
- Click "Add Admin" button
- Password is automatically hashed and stored securely

### 4. **Delete Admin**
- Click the "Delete" button next to any admin
- Confirm the deletion
- Admin will be removed from the database

## How to Use

1. **Start the backend server:**
   ```bash
   cd backend
   npm start
   ```

2. **Open your web browser** and navigate to:
   ```
   http://localhost:3000/admin-management.html
   ```

3. **Login** with admin credentials:
   - Username: `admin`
   - Password: `admin123`

4. **Add a new admin:**
   - Enter username in the "Username" field
   - Enter password in the "Password" field
   - Click "Add Admin" button
   - The new admin will appear in the table

5. **Delete an admin:**
   - Find the admin in the table
   - Click the "Delete" button
   - Confirm the deletion

## API Endpoints Used

The UI uses these API endpoints:

- `POST /api/auth/login` - Login
- `GET /api/auth/verify` - Verify token
- `GET /api/admins` - Get all admins (requires auth)
- `POST /api/admins` - Add new admin (requires auth)
- `DELETE /api/admins/:id` - Delete admin (requires auth)

## Security Notes

- All admin management operations require authentication
- Passwords are hashed using SHA-256 before storage
- Tokens expire when the server restarts (in-memory storage)
- For production, consider using more secure password hashing (bcrypt)

## Troubleshooting

**Can't access the page:**
- Make sure the backend server is running
- Check that you're using the correct URL: `http://localhost:3000/admin-management.html`
- Check browser console for errors

**Login fails:**
- Verify you're using correct credentials
- Default: username=`admin`, password=`admin123`
- Check server console for error messages

**Can't add admin:**
- Make sure you're logged in
- Check that username is unique (no duplicates allowed)
- Verify both username and password fields are filled

**Table not loading:**
- Check browser console for API errors
- Verify your token is still valid (try logging out and back in)
- Check server logs for errors

