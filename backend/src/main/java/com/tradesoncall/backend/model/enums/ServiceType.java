package com.tradesoncall.backend.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of tradesperson services")
public enum ServiceType {
    // Construction & Building
    GENERAL_CONTRACTOR("General Contractor", "contractor"),
    CARPENTER("Carpenter", "carpenter"),
    MASON("Mason", "mason"),
    ROOFER("Roofer", "roofing contractor"),

    // Electrical & Plumbing
    ELECTRICIAN("Electrician", "electrician"),
    PLUMBER("Plumber", "plumber"),
    HVAC("HVAC Technician", "hvac contractor"),

    // Home Improvement
    PAINTER("Painter", "painter"),
    FLOORING("Flooring Specialist", "flooring contractor"),
    LANDSCAPER("Landscaper", "landscaping"),
    POOL_SERVICE("Pool Service", "pool service"),

    // Specialized Services
    LOCKSMITH("Locksmith", "locksmith"),
    APPLIANCE_REPAIR("Appliance Repair", "appliance repair"),
    PEST_CONTROL("Pest Control", "pest control"),
    WINDOW_CLEANER("Window Cleaner", "window cleaning"),

    // Other
    HANDYMAN("Handyman", "handyman"),
    OTHER("Other", "home services");

    private final String displayName;
    private final String searchQuery;

    ServiceType(String displayName, String searchQuery) {
        this.displayName = displayName;
        this.searchQuery = searchQuery;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSearchQuery() {
        return searchQuery;
    }
}