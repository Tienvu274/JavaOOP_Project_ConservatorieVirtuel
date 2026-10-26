package org.app.model;

import java.time.LocalDateTime;

public interface Schedulable {
    LocalDateTime getStart();
    LocalDateTime getEnd();
}
