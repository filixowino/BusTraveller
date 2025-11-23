# How to Start the Backend Server

## Quick Start Guide

### Step 1: Open Command Prompt or PowerShell

**On Windows:**
- Press `Win + R`
- Type `cmd` or `powershell` and press Enter
- OR search for "Command Prompt" or "PowerShell" in Start menu

### Step 2: Navigate to Backend Folder

Type this command and press Enter:

```bash
cd C:\Users\felix\AndroidStudioProjects\BusTraveller\backend
```

**Alternative:** Open File Explorer, navigate to the `backend` folder, then type `cmd` in the address bar and press Enter.

### Step 3: Install Dependencies (First Time Only)

If you haven't installed dependencies yet, run:

```bash
npm install
```

This will install all required packages. You only need to do this once.

### Step 4: Start the Server

Run this command:

```bash
npm start
```

### Step 5: Verify Server is Running

You should see output like this:

```
Connected to SQLite database
Default admin created: username=admin, password=admin123
Server is running on http://localhost:3000
```

### Step 6: Test in Browser (Optional)

Open your web browser and go to:
```
http://localhost:3000/api/health
```

You should see:
```json
{"status":"ok","message":"BusTraveller API is running"}
```

## Complete Command Sequence

Copy and paste these commands one by one:

```bash
cd C:\Users\felix\AndroidStudioProjects\BusTraveller\backend
npm install
npm start
```

## Troubleshooting

### Error: "npm is not recognized"
**Solution:** Install Node.js from https://nodejs.org/

### Error: "Cannot find module"
**Solution:** Run `npm install` first

### Error: "Port 3000 already in use"
**Solution:** 
1. Find what's using port 3000:
   ```bash
   netstat -ano | findstr :3000
   ```
2. Close that program or change the port in `server.js`

### Server starts but stops immediately
**Solution:** Check the error message in the console. Common issues:
- Missing dependencies: Run `npm install`
- Database file locked: Close any other programs using it
- Port conflict: Change port in `server.js`

## Alternative: Using VS Code Terminal

1. Open VS Code
2. Open the `backend` folder in VS Code
3. Press `Ctrl + ~` to open terminal
4. Run: `npm start`

## Alternative: Using Android Studio Terminal

1. Open Android Studio
2. Open the project
3. Click "Terminal" tab at the bottom
4. Type: `cd backend`
5. Type: `npm start`

## Keep Server Running

- **Don't close** the Command Prompt/PowerShell window while using the app
- The server must stay running for the app to connect
- To stop the server, press `Ctrl + C` in the terminal

## What the Server Does

Once running, the server:
- ✅ Listens on `http://localhost:3000`
- ✅ Provides API endpoints for the Android app
- ✅ Stores data in SQLite database (`database.sqlite`)
- ✅ Handles admin authentication
- ✅ Manages vehicles and parcels

## Next Steps

After the server is running:
1. ✅ Server is ready at `http://localhost:3000`
2. ✅ Open your Android app
3. ✅ Try logging in with: username=`admin`, password=`admin123`
4. ✅ The app should now connect successfully!

