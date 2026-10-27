package org.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExamResult {

    private final boolean passed;
    private final BigDecimal score;
    private final LocalDateTime recordedAt;

    public ExamResult(boolean passed, BigDecimal score, LocalDateTime recordedAt) {
        ModelChecks.require(score == null || (score.signum() >= 0
                                && score.stripTrailingZeros().scale() <= 4
                                && score.compareTo(new BigDecimal("999999.9999")) <= 0),
                        "Score must fit nonnegative DECIMAL(10,4)");
        this.passed = passed;
        this.score = score;
        this.recordedAt = ModelChecks.time(recordedAt);
    }

    public ExamResult(ExamResult other) {
        this(other.passed, other.score, other.recordedAt);
    }

    public boolean isPassed() {
        return passed;
    }

    public BigDecimal getScore() {
        return score;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    @Override
    public String toString() {
        return (passed ? "PASS" : "FAIL")
                + " | score="
                + (score == null ? "not provided" : score);
    }
}