package org.app.model;

public class FinitePackage extends CoursePackage {

    private final int lessonLimit;

    public FinitePackage(String id, String name, long priceCents, int lessonLimit) {
        super(id, name, priceCents);
        ModelChecks.require(lessonLimit > 0 && lessonLimit <= Integer.MAX_VALUE / 60, "Invalid lesson count");
        this.lessonLimit = lessonLimit;
    }

    public FinitePackage(FinitePackage other) {
        this(other.getId(), other.getName(), other.getPriceCents(), other.lessonLimit);
    }

    @Override
    public boolean isUnlimited() {
        return false;
    }

    @Override
    public int getLessonLimit() {
        return lessonLimit;
    }

    @Override
    public CoursePackage copy() {
        return new FinitePackage(this);
    }
}