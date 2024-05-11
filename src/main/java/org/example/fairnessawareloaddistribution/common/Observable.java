package org.example.fairnessawareloaddistribution.common;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ankushs
 */
public abstract class Observable<T> {

    private final List<Observer<T>> observers = new ArrayList<>();

    public final void addObserver(@NonNull Observer<T> observer) {
        observers.add(observer);
    }

    public final void removeObserver(@NonNull Observer<T> observer) {
        observers.remove(observer);
    }

    public final void notifyObservers(List<T> records) {
        observers.forEach(observer -> observer.update(records));
    }
}
