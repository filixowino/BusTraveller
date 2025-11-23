const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const crypto = require('crypto');

const app = express();
const PORT = process.env.PORT || 3000;

// Store active admin tokens (in production, use Redis or database)
const activeTokens = new Map();

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Serve static files for admin management UI (before API routes)
app.use(express.static(path.join(__dirname, 'public')));

// Database setup
const dbPath = path.join(__dirname, 'database.sqlite');
const db = new sqlite3.Database(dbPath, (err) => {
    if (err) {
        console.error('Error opening database:', err.message);
    } else {
        console.log('Connected to SQLite database');
        initializeDatabase();
    }
});

function initializeDatabase() {
    // Create admin table
    db.run(`CREATE TABLE IF NOT EXISTS admin (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        username TEXT UNIQUE NOT NULL,
        password TEXT NOT NULL,
        createdAt INTEGER NOT NULL
    )`, (err) => {
        if (err) {
            console.error('Error creating admin table:', err.message);
        } else {
            // Create default admin if not exists
            db.get('SELECT * FROM admin WHERE username = ?', ['admin'], (err, row) => {
                if (err) {
                    console.error('Error checking admin:', err.message);
                } else if (!row) {
                    // Default password: admin123 (in production, use proper hashing)
                    const defaultPassword = crypto.createHash('sha256').update('admin123').digest('hex');
                    db.run(
                        'INSERT INTO admin (username, password, createdAt) VALUES (?, ?, ?)',
                        ['admin', defaultPassword, Date.now()],
                        (err) => {
                            if (err) {
                                console.error('Error creating default admin:', err.message);
                            } else {
                                console.log('Default admin created: username=admin, password=admin123');
                            }
                        }
                    );
                }
            });
        }
    });

    // Create vehicles table
    db.run(`CREATE TABLE IF NOT EXISTS vehicles (
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        latitude REAL NOT NULL,
        longitude REAL NOT NULL,
        lastUpdateTime INTEGER NOT NULL,
        status TEXT NOT NULL,
        routeNumber TEXT NOT NULL,
        driverName TEXT,
        speed REAL DEFAULT 0,
        heading REAL DEFAULT 0,
        departureLocation TEXT,
        arrivalLocation TEXT
    )`, (err) => {
        if (err) {
            console.error('Error creating vehicles table:', err.message);
        }
    });

    // Create parcels table
    db.run(`CREATE TABLE IF NOT EXISTS parcels (
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        latitude REAL NOT NULL,
        longitude REAL NOT NULL,
        lastUpdateTime INTEGER NOT NULL,
        status TEXT NOT NULL,
        trackingNumber TEXT NOT NULL,
        estimatedDelivery INTEGER,
        carrierName TEXT
    )`, (err) => {
        if (err) {
            console.error('Error creating parcels table:', err.message);
        }
    });
}

// Authentication middleware
function requireAdmin(req, res, next) {
    const token = req.headers.authorization?.replace('Bearer ', '');
    
    if (!token) {
        return res.status(401).json({ error: 'Authentication required' });
    }
    
    if (!activeTokens.has(token)) {
        return res.status(401).json({ error: 'Invalid or expired token' });
    }
    
    req.adminToken = token;
    next();
}

// Generate secure token
function generateToken() {
    return crypto.randomBytes(32).toString('hex');
}

// Authentication endpoints
app.post('/api/auth/login', (req, res) => {
    const { username, password } = req.body;
    
    if (!username || !password) {
        return res.status(400).json({ error: 'Username and password required' });
    }
    
    const hashedPassword = crypto.createHash('sha256').update(password).digest('hex');
    
    db.get('SELECT * FROM admin WHERE username = ? AND password = ?', [username, hashedPassword], (err, row) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        
        if (!row) {
            return res.status(401).json({ error: 'Invalid username or password' });
        }
        
        const token = generateToken();
        activeTokens.set(token, {
            username: row.username,
            createdAt: Date.now()
        });
        
        res.json({
            token,
            username: row.username,
            message: 'Login successful'
        });
    });
});

app.post('/api/auth/logout', requireAdmin, (req, res) => {
    activeTokens.delete(req.adminToken);
    res.json({ message: 'Logout successful' });
});

app.get('/api/auth/verify', requireAdmin, (req, res) => {
    res.json({ valid: true, message: 'Token is valid' });
});

// Admin management endpoints (require admin authentication)
app.get('/api/admins', requireAdmin, (req, res) => {
    db.all('SELECT id, username, createdAt FROM admin ORDER BY createdAt DESC', [], (err, rows) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json(rows);
    });
});

app.post('/api/admins', requireAdmin, (req, res) => {
    const { username, password } = req.body;
    
    if (!username || !password) {
        return res.status(400).json({ error: 'Username and password are required' });
    }
    
    const hashedPassword = crypto.createHash('sha256').update(password).digest('hex');
    
    db.run(
        'INSERT INTO admin (username, password, createdAt) VALUES (?, ?, ?)',
        [username, hashedPassword, Date.now()],
        function(err) {
            if (err) {
                if (err.message.includes('UNIQUE constraint')) {
                    return res.status(409).json({ error: 'Username already exists' });
                }
                return res.status(500).json({ error: err.message });
            }
            res.status(201).json({
                id: this.lastID,
                username: username,
                message: 'Admin created successfully'
            });
        }
    );
});

app.put('/api/admins/:id', requireAdmin, (req, res) => {
    const { id } = req.params;
    const { username, password } = req.body;
    
    if (!username) {
        return res.status(400).json({ error: 'Username is required' });
    }
    
    if (password) {
        const hashedPassword = crypto.createHash('sha256').update(password).digest('hex');
        db.run(
            'UPDATE admin SET username = ?, password = ? WHERE id = ?',
            [username, hashedPassword, id],
            function(err) {
                if (err) {
                    if (err.message.includes('UNIQUE constraint')) {
                        return res.status(409).json({ error: 'Username already exists' });
                    }
                    return res.status(500).json({ error: err.message });
                }
                if (this.changes === 0) {
                    return res.status(404).json({ error: 'Admin not found' });
                }
                res.json({ message: 'Admin updated successfully' });
            }
        );
    } else {
        db.run(
            'UPDATE admin SET username = ? WHERE id = ?',
            [username, id],
            function(err) {
                if (err) {
                    if (err.message.includes('UNIQUE constraint')) {
                        return res.status(409).json({ error: 'Username already exists' });
                    }
                    return res.status(500).json({ error: err.message });
                }
                if (this.changes === 0) {
                    return res.status(404).json({ error: 'Admin not found' });
                }
                res.json({ message: 'Admin updated successfully' });
            }
        );
    }
});

app.delete('/api/admins/:id', requireAdmin, (req, res) => {
    const { id } = req.params;
    
    db.run('DELETE FROM admin WHERE id = ?', [id], function(err) {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        if (this.changes === 0) {
            return res.status(404).json({ error: 'Admin not found' });
        }
        res.json({ message: 'Admin deleted successfully' });
    });
});

// Vehicles endpoints
app.get('/api/vehicles', (req, res) => {
    db.all('SELECT * FROM vehicles', [], (err, rows) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json(rows);
    });
});

app.get('/api/vehicles/:id', (req, res) => {
    const { id } = req.params;
    db.get('SELECT * FROM vehicles WHERE id = ?', [id], (err, row) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        if (!row) {
            res.status(404).json({ error: 'Vehicle not found' });
            return;
        }
        res.json(row);
    });
});

app.post('/api/vehicles', requireAdmin, (req, res) => {
    const {
        id, name, latitude, longitude, lastUpdateTime,
        status, routeNumber, driverName, speed, heading,
        departureLocation, arrivalLocation
    } = req.body;

    db.run(
        `INSERT INTO vehicles (id, name, latitude, longitude, lastUpdateTime, status, routeNumber, driverName, speed, heading, departureLocation, arrivalLocation)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
        [id, name, latitude, longitude, lastUpdateTime, status, routeNumber, driverName, speed, heading, departureLocation, arrivalLocation],
        function(err) {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            res.status(201).json({ id: this.lastID, ...req.body });
        }
    );
});

app.put('/api/vehicles/:id', (req, res) => {
    const { id } = req.params;
    const {
        name, latitude, longitude, lastUpdateTime,
        status, routeNumber, driverName, speed, heading,
        departureLocation, arrivalLocation
    } = req.body;

    db.run(
        `UPDATE vehicles SET name = ?, latitude = ?, longitude = ?, lastUpdateTime = ?, status = ?, routeNumber = ?, driverName = ?, speed = ?, heading = ?, departureLocation = ?, arrivalLocation = ?
         WHERE id = ?`,
        [name, latitude, longitude, lastUpdateTime, status, routeNumber, driverName, speed, heading, departureLocation, arrivalLocation, id],
        function(err) {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            if (this.changes === 0) {
                res.status(404).json({ error: 'Vehicle not found' });
                return;
            }
            res.json({ message: 'Vehicle updated successfully' });
        }
    );
});

app.patch('/api/vehicles/:id/location', (req, res) => {
    const { id } = req.params;
    const { latitude, longitude, speed, heading } = req.body;
    const lastUpdateTime = Date.now();

    db.run(
        `UPDATE vehicles SET latitude = ?, longitude = ?, lastUpdateTime = ?, speed = ?, heading = ? WHERE id = ?`,
        [latitude, longitude, lastUpdateTime, speed || 0, heading || 0, id],
        function(err) {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            if (this.changes === 0) {
                res.status(404).json({ error: 'Vehicle not found' });
                return;
            }
            res.json({ message: 'Location updated successfully' });
        }
    );
});

app.delete('/api/vehicles/:id', requireAdmin, (req, res) => {
    const { id } = req.params;
    db.run('DELETE FROM vehicles WHERE id = ?', [id], function(err) {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        if (this.changes === 0) {
            res.status(404).json({ error: 'Vehicle not found' });
            return;
        }
        res.json({ message: 'Vehicle deleted successfully' });
    });
});

// Parcels endpoints
app.get('/api/parcels', (req, res) => {
    db.all('SELECT * FROM parcels', [], (err, rows) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json(rows);
    });
});

app.get('/api/parcels/:id', (req, res) => {
    const { id } = req.params;
    db.get('SELECT * FROM parcels WHERE id = ?', [id], (err, row) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        if (!row) {
            res.status(404).json({ error: 'Parcel not found' });
            return;
        }
        res.json(row);
    });
});

app.post('/api/parcels', requireAdmin, (req, res) => {
    const {
        id, name, latitude, longitude, lastUpdateTime,
        status, trackingNumber, estimatedDelivery, carrierName
    } = req.body;

    db.run(
        `INSERT INTO parcels (id, name, latitude, longitude, lastUpdateTime, status, trackingNumber, estimatedDelivery, carrierName)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
        [id, name, latitude, longitude, lastUpdateTime, status, trackingNumber, estimatedDelivery, carrierName],
        function(err) {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            res.status(201).json({ id: this.lastID, ...req.body });
        }
    );
});

app.put('/api/parcels/:id', (req, res) => {
    const { id } = req.params;
    const {
        name, latitude, longitude, lastUpdateTime,
        status, trackingNumber, estimatedDelivery, carrierName
    } = req.body;

    db.run(
        `UPDATE parcels SET name = ?, latitude = ?, longitude = ?, lastUpdateTime = ?, status = ?, trackingNumber = ?, estimatedDelivery = ?, carrierName = ?
         WHERE id = ?`,
        [name, latitude, longitude, lastUpdateTime, status, trackingNumber, estimatedDelivery, carrierName, id],
        function(err) {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            if (this.changes === 0) {
                res.status(404).json({ error: 'Parcel not found' });
                return;
            }
            res.json({ message: 'Parcel updated successfully' });
        }
    );
});

app.patch('/api/parcels/:id/location', (req, res) => {
    const { id } = req.params;
    const { latitude, longitude } = req.body;
    const lastUpdateTime = Date.now();

    db.run(
        `UPDATE parcels SET latitude = ?, longitude = ?, lastUpdateTime = ? WHERE id = ?`,
        [latitude, longitude, lastUpdateTime, id],
        function(err) {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            if (this.changes === 0) {
                res.status(404).json({ error: 'Parcel not found' });
                return;
            }
            res.json({ message: 'Location updated successfully' });
        }
    );
});

app.delete('/api/parcels/:id', requireAdmin, (req, res) => {
    const { id } = req.params;
    db.run('DELETE FROM parcels WHERE id = ?', [id], function(err) {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        if (this.changes === 0) {
            res.status(404).json({ error: 'Parcel not found' });
            return;
        }
        res.json({ message: 'Parcel deleted successfully' });
    });
});

// Health check endpoint
app.get('/api/health', (req, res) => {
    res.json({ status: 'ok', message: 'BusTraveller API is running' });
});

// Start server
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});

// Graceful shutdown
process.on('SIGINT', () => {
    db.close((err) => {
        if (err) {
            console.error('Error closing database:', err.message);
        } else {
            console.log('Database connection closed');
        }
        process.exit(0);
    });
});

