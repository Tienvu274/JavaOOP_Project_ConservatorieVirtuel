package org.app.service;

import org.app.model.CoursePackage;
import org.app.repository.PackageRepository;

import java.sql.SQLException;
import java.util.List;

public class PackageService {

    private final PackageRepository repository;

    public PackageService(PackageRepository repository) {
        this.repository = repository;
    }

    public CoursePackage createPackage(CoursePackage coursePackage) throws SQLException {
        if (coursePackage == null) {
            throw new IllegalArgumentException("Package is required.");
        }

        if (coursePackage.getName().length() > 128) {
            throw new IllegalArgumentException("Package name must not exceed 128 characters.");
        }

        try {
            repository.insert(coursePackage);
        } catch (SQLException error) {
            if (error.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Package ID already exists: " + coursePackage.getId(), error);
            }
            throw error;
        }
        return coursePackage.copy();
    }

    public List<CoursePackage> getAllPackages() throws SQLException {
        return repository.findAll();
    }
}