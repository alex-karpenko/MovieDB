package com.example.leshik.moviedb.utils;

import android.util.Log;

import com.example.leshik.moviedb.R;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by alex on 3/19/17.
 *
 * Events' queue class to propagate system-wide events,
 * such as network state changes, list refreshing, etc.
 *
 */

public class EventsUtils {
    private static final String TAG = "EventsUtils";

    private static Subject<EventType> eventSubject;
    private static Observable<EventType> eventObservable;

    static {
        Log.i(TAG, "EventsUtils: initializing");
        eventSubject = PublishSubject.create();
        eventObservable = eventSubject.distinctUntilChanged();
        eventObservable.subscribe(new Consumer<EventType>() {
            @Override
            public void accept(@NonNull EventType eventType) throws Exception {
                Log.i(TAG, "accept: " + eventType.toString());
            }
        });
    }

    private EventsUtils() {
    }

    public static Observable<EventType> getEventObservable() {
        return eventObservable;
    }

    public static void postEvent(EventType event) {
        eventSubject.onNext(event);
    }

    public enum EventType {
        Refreshing(R.string.event_message_refreshing),
        NetworkUnavailable(R.string.event_message_network_down),
        NetworkAvailable(R.string.event_message_network_up);

        private int messageId;

        EventType(int messageId) {
            this.messageId = messageId;
        }

        public int getMessageId() {
            return messageId;
        }
    }
}
