package com.alexandershtanko.psychotests.utils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.io.IOException;

import rx.Observable;
import rx.subscriptions.Subscriptions;

/**
 * Created by alexander on 08/07/16.
 */
public class RxFirebase {
    public static class ChildEvent<T> {
        private final DataSnapshot dataSnapshot;
        private final Class<T> childClass;
        private EventType eventType;
        private String oldKey;

        public ChildEvent(Class<T> childClass, EventType eventType, DataSnapshot dataSnapshot, String oldKey) {
            this.eventType = eventType;
            this.oldKey = oldKey;
            this.dataSnapshot = dataSnapshot;
            this.childClass = childClass;
        }


        public String getKey() {
            return dataSnapshot.getKey();
        }

        public String getOldKey() {
            return oldKey;
        }

        public EventType getEventType() {
            return eventType;
        }

        public T getValue() {
            return getValue(dataSnapshot);
        }

        public Object getPriority(DataSnapshot dataSnapshot) {
            return dataSnapshot.getPriority();
        }


        private T getValue(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(childClass);
        }

        public enum EventType {ADDED, CHANGED, REMOVED, MOVED}

    }

    public static class DatabaseException extends IOException {
        private final DatabaseError error;

        public DatabaseException(DatabaseError error) {
            super(error.toException());
            this.error = error;
        }

        public DatabaseError getError() {
            return error;
        }
    }

    public static <T> Observable<ChildEvent<T>> getChildrenObservable(Query query, Class<T> childClass) {
        return Observable.create((Observable.OnSubscribe<ChildEvent<T>>) subscriber -> {
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    subscriber.onNext(new ChildEvent<T>(childClass, ChildEvent.EventType.ADDED, dataSnapshot, s));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    subscriber.onNext(new ChildEvent<T>(childClass, ChildEvent.EventType.CHANGED, dataSnapshot, s));

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    subscriber.onNext(new ChildEvent<T>(childClass, ChildEvent.EventType.REMOVED, dataSnapshot, null));

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    subscriber.onNext(new ChildEvent<T>(childClass, ChildEvent.EventType.MOVED, dataSnapshot, s));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    subscriber.onError(new DatabaseException(databaseError));
                }
            };
            query.addChildEventListener(childEventListener);
            subscriber.add(Subscriptions.create(() -> query.removeEventListener(childEventListener)));

        });
    }
}
