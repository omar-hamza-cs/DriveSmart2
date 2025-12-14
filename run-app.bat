@echo off
echo ============================================
echo       DriveSmart Application Launcher
echo ============================================
echo Starting application on port 8081...
echo.
echo Wait for "Tomcat started on port(s): 8081" message
echo Then visit: http://localhost:8081
echo.
echo Demo credentials:
echo Admin: admin@drivesmart.com / admin123
echo Worker: worker@drivesmart.com / worker123
echo Customer: user@drivesmart.com / user123
echo.
echo Press Ctrl+C to stop the application
echo ============================================
echo.
mvn spring-boot:run
echo.
echo Application stopped.
pause
