# Troubleshooting Connection Issues

## Error: "Failed to connect to 10.0.2.2 from 10.0.2.15"

This error means the Android app cannot reach the backend server. Here's how to fix it:

### Step 1: Verify Backend Server is Running

1. **Open Command Prompt or PowerShell**
2. **Navigate to backend folder:**
   ```bash
   cd C:\Users\felix\AndroidStudioProjects\BusTraveller\backend
   ```

3. **Start the server:**
   ```bash
   npm start
   ```

4. **You should see:**
   ```
   Connected to SQLite database
   Default admin created: username=admin, password=admin123
   Server is running on http://localhost:3000
   ```

### Step 2: Test Server in Browser

Open your web browser and go to:
```
http://localhost:3000/api/health
```

You should see:
```json
{"status":"ok","message":"BusTraveller API is running"}
```

If this doesn't work, the server is not running correctly.

### Step 3: Check Server Port

Make sure the server is running on port **3000**. Check the console output when you start the server.

If you need to change the port, edit `backend/server.js`:
```javascript
const PORT = process.env.PORT || 3000;  // Change 3000 to your desired port
```

Then update `app/src/main/java/com/example/bustraveller/data/remote/RetrofitClient.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:YOUR_PORT/api/"
```

### Step 4: Verify Android Emulator Network

**For Android Emulator:**
- `10.0.2.2` is the special IP that maps to your computer's `localhost`
- This should work automatically if the server is running on your computer

**For Physical Device:**
1. Find your computer's IP address:
   - Windows: Open Command Prompt and type `ipconfig`
   - Look for "IPv4 Address" (usually something like `192.168.1.100`)
   
2. Update `RetrofitClient.kt`:
   ```kotlin
   private const val BASE_URL = "http://YOUR_IP_ADDRESS:3000/api/"
   ```
   Replace `YOUR_IP_ADDRESS` with your actual IP (e.g., `192.168.1.100`)

3. Make sure your phone and computer are on the same Wi-Fi network

### Step 5: Check Firewall

Windows Firewall might be blocking the connection:

1. Open Windows Defender Firewall
2. Click "Allow an app or feature through Windows Firewall"
3. Make sure Node.js is allowed, or add port 3000 as an exception

### Step 6: Verify Network Security Config

Make sure `app/src/main/res/xml/network_security_config.xml` exists and allows cleartext traffic.

### Step 7: Test Connection Manually

You can test the connection using `curl` or Postman:

```bash
curl http://localhost:3000/api/health
```

Or in the Android emulator's browser, try:
```
http://10.0.2.2:3000/api/health
```

## Common Issues

### Issue: "Cannot find module" when starting server
**Solution:** Run `npm install` in the backend folder first

### Issue: Port 3000 already in use
**Solution:** 
1. Find what's using port 3000:
   ```bash
   netstat -ano | findstr :3000
   ```
2. Kill that process or change the port in `server.js`

### Issue: Server starts but app still can't connect
**Solution:**
1. Restart the Android emulator
2. Rebuild the app
3. Check Logcat for detailed error messages

### Issue: Works on emulator but not on physical device
**Solution:**
1. Use your computer's IP address instead of `10.0.2.2`
2. Ensure phone and computer are on the same network
3. Check that your router isn't blocking device-to-device communication

## Quick Checklist

- [ ] Backend server is running (`npm start` in backend folder)
- [ ] Server shows "Server is running on http://localhost:3000"
- [ ] Browser can access `http://localhost:3000/api/health`
- [ ] Using `10.0.2.2:3000` for emulator OR your computer's IP for physical device
- [ ] Network security config allows cleartext traffic
- [ ] Firewall allows Node.js/port 3000
- [ ] App has INTERNET permission in AndroidManifest.xml

## Still Having Issues?

1. Check the backend server console for errors
2. Check Android Logcat for detailed error messages
3. Try restarting both the server and the app
4. Verify the URL in `RetrofitClient.kt` matches your setup

