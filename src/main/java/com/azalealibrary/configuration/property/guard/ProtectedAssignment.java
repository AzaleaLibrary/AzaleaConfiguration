package com.azalealibrary.configuration.property.guard;

import javax.annotation.Nullable;
import java.util.List;

public interface ProtectedAssignment<T> {

    List<AssignmentPolicy<T>> getAssignmentPolicies();

    default T verify(@Nullable T value) throws RuntimeException {
        AssignmentPolicy<T> failedCheck = getAssignmentPolicies().stream()
                .filter(validator -> !validator.canAssign(value))
                .findAny().orElse(null);

        if (failedCheck != null) {
            throw new RuntimeException(failedCheck.getMessage(value));
        }
        return value;
    }
}
