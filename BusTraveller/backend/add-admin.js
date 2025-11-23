const sqlite3 = require('sqlite3').verbose();
const crypto = require('crypto');
const path = require('path');
const readline = require('readline');

const dbPath = path.join(__dirname, 'database.sqlite');
const db = new sqlite3.Database(dbPath, (err) => {
    if (err) {
        console.error('Error opening database:', err.message);
        process.exit(1);
    }
    console.log('Connected to database');
});

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

function question(query) {
    return new Promise(resolve => rl.question(query, resolve));
}

async function addAdmin() {
    try {
        const username = await question('Enter username: ');
        const password = await question('Enter password: ');
        
        if (!username || !password) {
            console.error('Username and password are required!');
            rl.close();
            db.close();
            return;
        }
        
        // Hash the password
        const hashedPassword = crypto.createHash('sha256').update(password).digest('hex');
        
        // Check if username already exists
        db.get('SELECT * FROM admin WHERE username = ?', [username], (err, row) => {
            if (err) {
                console.error('Error checking admin:', err.message);
                rl.close();
                db.close();
                return;
            }
            
            if (row) {
                console.log(`Admin with username "${username}" already exists!`);
                console.log('Do you want to update the password? (y/n)');
                rl.question('', (answer) => {
                    if (answer.toLowerCase() === 'y') {
                        db.run(
                            'UPDATE admin SET password = ?, createdAt = ? WHERE username = ?',
                            [hashedPassword, Date.now(), username],
                            function(err) {
                                if (err) {
                                    console.error('Error updating admin:', err.message);
                                } else {
                                    console.log(`✓ Password updated for admin "${username}"`);
                                }
                                rl.close();
                                db.close();
                            }
                        );
                    } else {
                        console.log('Cancelled.');
                        rl.close();
                        db.close();
                    }
                });
            } else {
                // Insert new admin
                db.run(
                    'INSERT INTO admin (username, password, createdAt) VALUES (?, ?, ?)',
                    [username, hashedPassword, Date.now()],
                    function(err) {
                        if (err) {
                            console.error('Error adding admin:', err.message);
                        } else {
                            console.log(`✓ Admin "${username}" added successfully!`);
                        }
                        rl.close();
                        db.close();
                    }
                );
            }
        });
    } catch (error) {
        console.error('Error:', error.message);
        rl.close();
        db.close();
    }
}

function listAdmins() {
    db.all('SELECT id, username, createdAt FROM admin', [], (err, rows) => {
        if (err) {
            console.error('Error listing admins:', err.message);
            db.close();
            return;
        }
        
        if (rows.length === 0) {
            console.log('No admins found.');
        } else {
            console.log('\nAdmins in database:');
            console.log('-------------------');
            rows.forEach(row => {
                const date = new Date(row.createdAt);
                console.log(`ID: ${row.id} | Username: ${row.username} | Created: ${date.toLocaleString()}`);
            });
        }
        db.close();
    });
}

function deleteAdmin() {
    rl.question('Enter username to delete: ', (username) => {
        if (!username) {
            console.error('Username is required!');
            rl.close();
            db.close();
            return;
        }
        
        db.run('DELETE FROM admin WHERE username = ?', [username], function(err) {
            if (err) {
                console.error('Error deleting admin:', err.message);
            } else if (this.changes === 0) {
                console.log(`Admin "${username}" not found.`);
            } else {
                console.log(`✓ Admin "${username}" deleted successfully!`);
            }
            rl.close();
            db.close();
        });
    });
}

// Main menu
const command = process.argv[2];

if (command === 'add') {
    addAdmin();
} else if (command === 'list') {
    listAdmins();
} else if (command === 'delete') {
    deleteAdmin();
} else {
    console.log('Usage:');
    console.log('  node add-admin.js add      - Add a new admin user');
    console.log('  node add-admin.js list      - List all admin users');
    console.log('  node add-admin.js delete    - Delete an admin user');
    console.log('');
    console.log('Examples:');
    console.log('  node add-admin.js add');
    console.log('  node add-admin.js list');
    console.log('  node add-admin.js delete');
    db.close();
}

