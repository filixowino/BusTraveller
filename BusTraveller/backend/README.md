# BusTraveller Backend API

REST API server for the BusTraveller Android application.

## Setup

1. Install dependencies:
```bash
npm install
```

2. Start the server:
```bash
npm start
```

For development with auto-reload:
```bash
npm run dev
```

The server will run on `http://localhost:3000` by default.

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login as admin (returns token)
  - Body: `{ "username": "admin", "password": "admin123" }`
  - Response: `{ "token": "...", "username": "admin", "message": "Login successful" }`
- `POST /api/auth/logout` - Logout (requires Authorization header with Bearer token)
- `GET /api/auth/verify` - Verify token validity (requires Authorization header)

### Vehicles
- `GET /api/vehicles` - Get all vehicles (public)
- `GET /api/vehicles/:id` - Get vehicle by ID (public)
- `POST /api/vehicles` - Create a new vehicle (**requires admin token**)
- `PUT /api/vehicles/:id` - Update a vehicle (public)
- `PATCH /api/vehicles/:id/location` - Update vehicle location (public)
- `DELETE /api/vehicles/:id` - Delete a vehicle (**requires admin token**)

### Parcels
- `GET /api/parcels` - Get all parcels (public)
- `GET /api/parcels/:id` - Get parcel by ID (public)
- `POST /api/parcels` - Create a new parcel (**requires admin token**)
- `PUT /api/parcels/:id` - Update a parcel (public)
- `PATCH /api/parcels/:id/location` - Update parcel location (public)
- `DELETE /api/parcels/:id` - Delete a parcel (**requires admin token**)

### Health Check
- `GET /api/health` - Check API status

## Authentication

The backend uses token-based authentication for admin operations:

1. **Default Admin Account:**
   - Username: `admin`
   - Password: `admin123`
   - Created automatically on first run

2. **How to Use:**
   - Login via `POST /api/auth/login` to get a token
   - Include token in requests: `Authorization: Bearer <token>`
   - Protected endpoints require valid token

3. **Protected Operations:**
   - Adding vehicles/parcels (POST)
   - Deleting vehicles/parcels (DELETE)
   - Viewing and updating locations are public

## Database

The API uses SQLite for data storage. The database file (`database.sqlite`) is created automatically on first run.

### Database Schema

- **admin** - Stores admin credentials (username, hashed password)
- **vehicles** - Stores vehicle tracking data
- **parcels** - Stores parcel tracking data

The admin table is created automatically with a default admin account (username: `admin`, password: `admin123`).

