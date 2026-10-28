package org.app.controller;

import org.app.model.CoursePackage;
import org.app.service.PackageService;

import java.sql.SQLException;
import java.util.List;

public class PackageController {

    private final PackageService service;

    public PackageController(PackageService service) {
        this.service = service;
    }

    public CoursePackage createPackage(CoursePackage coursePackage) throws SQLException {
        return service.createPackage(coursePackage);
    }

    public List<CoursePackage> getAllPackages() throws SQLException {
        return service.getAllPackages();
    }
}