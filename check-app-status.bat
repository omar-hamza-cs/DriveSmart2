@echo off
echo ============================================
echo     DriveSmart Application Status Check
echo ============================================
echo.

REM Check if port 8081 is in use
netstat -ano | findstr :8081 >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ APPLICATION IS RUNNING on port 8081
    echo 🌐 Visit: http://localhost:8081
    echo.
    echo Demo credentials:
    echo Admin: admin@drivesmart.com / admin123
    echo Worker: worker@drivesmart.com / worker123
    echo Customer: user@drivesmart.com / user123
    echo.
    echo Database: http://localhost:8081/h2-console
    echo Username: sa, Password: (leave blank)
    echo.
    echo Press any key to test the connection...
    pause >nul
    goto :test_connection
) else (
    echo ❌ Application is NOT running on port 8081
    echo.
    echo Try running 'run-app.bat' to start it.
    pause
    goto :exit
)

:test_connection
echo Testing application connectivity...
curl -s http://localhost:8081/login >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Application connectivity: GOOD
    echo ✅ Spring Boot is responding
) else (
    echo ❌ Cannot connect to application
    echo Check if it's still starting up...
)

:exit
echo.
echo ============================================
echo.
