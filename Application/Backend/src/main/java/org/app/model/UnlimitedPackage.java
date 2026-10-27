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
        throw new IllegalStateException("Unlimited package has no finite lesson count");
    }

    @Override
    public CoursePackage copy() {
        return new UnlimitedPackage(this);
    }
}