package org.example.fairnessawareloaddistribution.common;

import java.util.List;

/**
 * @author ankushs
 */
public abstract class Observer<U> {

    public abstract void update(List<U> records);

    public final void update(U record) {
        update(List.of(record));
    }
}
