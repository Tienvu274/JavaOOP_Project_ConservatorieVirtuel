package org.app.model;

public class UnlimitedPackage extends CoursePackage {

    public UnlimitedPackage(String id, String name, long priceCents) {
        super(id, name, priceCents);
    }

    public UnlimitedPackage(UnlimitedPackage other) {
        this(other.getId(), other.getName(), other.getPriceCents());
    }

    @Override
    public boolean isUnlimited() {
        return true;
    }

    @Override
    public int getLessonLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public CoursePackage copy() {
        return new UnlimitedPackage(this);
    }
}