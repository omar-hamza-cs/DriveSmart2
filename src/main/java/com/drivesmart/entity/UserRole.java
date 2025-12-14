package com.drivesmart.entity;

public enum UserRole {
    ADMIN,      // Full access - manage everything
    WORKER,     // Can manage cars, view rentals
    CUSTOMER    // Can browse and book cars (future)
}