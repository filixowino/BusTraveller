# Password Requirements - Running on Phone

## Current Setup: What Requires Password?

### ✅ **REQUIRES Admin Password:**
- **Adding** a new bus/vehicle
- **Adding** a new parcel
- **Deleting** a bus/vehicle
- **Deleting** a parcel

### ✅ **NO Password Required:**
- **Viewing** the map
- **Viewing** the list of buses/parcels
- **Tracking** locations
- **Searching** for items
- **Viewing** item details

## How It Works on Your Phone

1. **First Time:**
   - When you try to add or delete something, the app will ask for admin login
   - Enter: username=`admin`, password=`admin123`
   - Once logged in, you stay logged in (password saved on phone)

2. **After Login:**
   - You can add/delete without entering password again
   - Login persists until you logout or uninstall the app
   - You'll see "Admin" badge in the top bar

3. **To Logout:**
   - Click "Logout" button in the top bar
   - Next time you try to add/delete, you'll need to login again

## Options: Remove Password Requirement

If you want to **remove the password requirement** completely (not recommended for production):

### Option 1: Make All Operations Public (No Password)

**Backend Changes:**
Edit `backend/server.js` and remove `requireAdmin` from these lines:

```javascript
// Change this:
app.post('/api/vehicles', requireAdmin, (req, res) => {
// To this:
app.post('/api/vehicles', (req, res) => {

// Change this:
app.delete('/api/vehicles/:id', requireAdmin, (req, res) => {
// To this:
app.delete('/api/vehicles/:id', (req, res) => {

// Same for parcels:
app.post('/api/parcels', (req, res) => {  // Remove requireAdmin
app.delete('/api/parcels/:id', (req, res) => {  // Remove requireAdmin
```

**App Changes:**
Edit `app/src/main/java/com/example/bustraveller/ui/screens/MainScreen.kt`:

```kotlin
// Change this:
if (isLoggedIn) {
    RegistrationScreen(...)
} else {
    AdminLoginScreen(...)
}

// To this:
RegistrationScreen(...)  // Always show, no login check
```

And remove login check for delete:
```kotlin
// Change this:
if (isLoggedIn) {
    viewModel.deleteItem(item.id)
} else {
    // show login dialog
}

// To this:
viewModel.deleteItem(item.id)  // Always allow delete
```

### Option 2: Keep Password but Make It Optional

You can modify the app to allow operations even without login, but still sync with backend when logged in.

## Recommendation

**For Personal Use:**
- Keep the password system (it's already set up)
- Login once, stay logged in
- Password protects against accidental deletions

**For Production/Public Use:**
- **Keep passwords** - essential for security
- Consider adding user roles (admin vs regular user)
- Regular users can view, admins can add/delete

## Quick Answer

**Yes, you need the password to add/remove items**, but:
- ✅ You only need to login **once**
- ✅ Password is saved on your phone
- ✅ You stay logged in until you logout
- ✅ **Viewing** doesn't require password

**Default credentials:**
- Username: `admin`
- Password: `admin123`


