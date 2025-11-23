# BusTraveller - Database and Backend Setup

## Database (Room)

The app now uses **Room Database** for local data persistence. All data is automatically saved to the local SQLite database.

### Features:
- ✅ Local persistence - data survives app restarts
- ✅ Automatic syncing with backend (when available)
- ✅ Offline-first architecture - works without internet

## Backend Server

A Node.js/Express backend server has been added for data synchronization across devices.

### Setup Instructions:

1. **Navigate to the backend directory:**
   ```bash
   cd backend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start the server:**
   ```bash
   npm start
   ```
   
   For development with auto-reload:
   ```bash
   npm run dev
   ```

4. **The server will run on:** `http://localhost:3000`

### Backend API Endpoints:

- **Vehicles:**
  - `GET /api/vehicles` - Get all vehicles
  - `GET /api/vehicles/:id` - Get vehicle by ID
  - `POST /api/vehicles` - Create vehicle
  - `PUT /api/vehicles/:id` - Update vehicle
  - `PATCH /api/vehicles/:id/location` - Update location
  - `DELETE /api/vehicles/:id` - Delete vehicle

- **Parcels:**
  - `GET /api/parcels` - Get all parcels
  - `GET /api/parcels/:id` - Get parcel by ID
  - `POST /api/parcels` - Create parcel
  - `PUT /api/parcels/:id` - Update parcel
  - `PATCH /api/parcels/:id/location` - Update location
  - `DELETE /api/parcels/:id` - Delete parcel

### Android App Configuration:

The app is configured to connect to the backend at:
- **Android Emulator:** `http://10.0.2.2:3000/api/` (default)
- **Physical Device:** Update `RetrofitClient.kt` with your computer's IP address

To change the backend URL, edit:
```
app/src/main/java/com/example/bustraveller/data/remote/RetrofitClient.kt
```

Change the `BASE_URL` constant to your server's address.

### How It Works:

1. **Local Database (Room):** All operations save to local SQLite database first
2. **Backend Sync:** When backend is available, changes are automatically synced
3. **Offline Mode:** App works completely offline - backend sync is optional
4. **Data Sync:** Use `repository.syncFromBackend()` to fetch latest data from server

### Admin Authentication:

The backend now includes **admin authentication** to protect add/remove operations:

- **Default Admin Credentials:**
  - Username: `admin`
  - Password: `admin123`

- **Protected Operations:**
  - Adding vehicles (POST /api/vehicles)
  - Adding parcels (POST /api/parcels)
  - Deleting vehicles (DELETE /api/vehicles/:id)
  - Deleting parcels (DELETE /api/parcels/:id)

- **Authentication Endpoints:**
  - `POST /api/auth/login` - Login with username and password
  - `POST /api/auth/logout` - Logout (requires token)
  - `GET /api/auth/verify` - Verify token validity

- **How It Works:**
  - Admin password is stored in the database (hashed with SHA-256)
  - Login returns a token that must be sent with protected requests
  - The Android app requires admin login before allowing add/remove operations
  - Viewing and location updates don't require authentication

### Notes:

- The app works **offline-first** - all data is stored locally
- Backend sync happens automatically but failures are silently handled
- If backend is unavailable, the app continues to work with local data only
- To sync data from backend, call `syncFromBackend()` method in the repository
- **Admin login is required** in the app to add or remove vehicles/parcels
- The login screen appears automatically when trying to register or delete items

