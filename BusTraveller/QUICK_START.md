# Quick Start - Fix npm Error

## The Problem

You're running `npm install` in the wrong folder. The `package.json` file is in the `backend` folder, not the root project folder.

## The Solution

### Step 1: Navigate to the Backend Folder

You're currently here:
```
C:\Users\felix\AndroidStudioProjects\BusTraveller
```

You need to go here:
```
C:\Users\felix\AndroidStudioProjects\BusTraveller\backend
```

### Step 2: Run These Commands

In your Command Prompt, type:

```bash
cd backend
```

Then:

```bash
npm install
```

Then:

```bash
npm start
```

## Complete Command Sequence

Copy and paste these commands **one at a time**:

```bash
cd C:\Users\felix\AndroidStudioProjects\BusTraveller\backend
```

Press Enter, then:

```bash
npm install
```

Press Enter, then:

```bash
npm start
```

## Visual Guide

```
BusTraveller/                    ← You are here (WRONG)
├── app/
├── backend/                     ← You need to be here (CORRECT)
│   ├── package.json            ← This file exists here
│   ├── server.js
│   └── ...
└── ...
```

## Alternative: Use File Explorer

1. Open File Explorer
2. Navigate to: `C:\Users\felix\AndroidStudioProjects\BusTraveller\backend`
3. In the address bar, type `cmd` and press Enter
4. This opens Command Prompt in the correct folder
5. Now run: `npm install`
6. Then run: `npm start`

## Verify You're in the Right Folder

Before running `npm install`, check that you see `package.json`:

```bash
dir
```

You should see `package.json` in the list. If you don't see it, you're in the wrong folder!

## Still Having Issues?

Make sure:
- ✅ You're in the `backend` folder (not the root `BusTraveller` folder)
- ✅ The `package.json` file exists in the current folder
- ✅ Node.js is installed (check with: `node --version`)


