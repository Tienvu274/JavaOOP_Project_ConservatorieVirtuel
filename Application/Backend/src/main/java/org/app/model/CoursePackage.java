package org.app.model;

public abstract class CoursePackage implements Billable {

    private final String id;
    private final String name;
    private final long priceCents;

    protected CoursePackage(String id, String name, long priceCents) {
        this.id = ModelChecks.id(id);
        this.name = ModelChecks.text(name, "Package name");
        this.priceCents = ModelChecks.price(priceCents);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public long getPriceCents() {
        return priceCents;
    }

    public abstract boolean isUnlimited();

    public abstract int getLessonLimit();

    public abstract CoursePackage copy();

    @Override
    public String toString() {
        return id + " - " + name + " | " + priceCents + " cents" + " | " + (isUnlimited() ? "UNLIMITED" : getLessonLimit() + " lessons");
    }
}