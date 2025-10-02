package com.multimedia.media_library.exception;

import com.multimedia.media_library.model.Violation;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {
    private final transient List<Violation> violations;

    public ValidationException(List<Violation> violations) {
        super();
        this.violations = violations;
    }

    public String formatViolations() {
        return violations.stream()
                .map(violation -> "• " + violation.getMessage())
                .collect(Collectors.joining("\n"));
    }
}
