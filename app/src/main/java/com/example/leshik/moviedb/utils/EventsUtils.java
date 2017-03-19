package com.example.leshik.moviedb.utils;

import android.util.Log;

import com.example.leshik.moviedb.R;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by alex on 3/19/17.
 * <p>
 * Events' queue class to propagate system-wide events,
 * such as network state changes, list refreshing, etc.
 */

public class EventsUtils {
    private static final String TAG = "EventsUtils";

    private static PublishSubject<EventType> eventSubject;
    private static Observable<EventType> eventObservable;
    private static EventsUtils INSTANCE = new EventsUtils();

    private EventsUtils() {
        Log.i(TAG, "EventsUtils: create instance");
        eventSubject = PublishSubject.create();
        eventObservable = eventSubject.distinctUntilChanged();
    }

    private static EventsUtils getInstance() {
        return INSTANCE;
    }

    public static Observable<EventType> getEventObservable() {
        return getInstance().eventObservable;
    }

    public static void postEvent(EventType event) {
        getInstance().eventSubject.onNext(event);
    }

    public enum EventType {
        Refreshing(R.string.event_message_refreshing),
        NetworkUnavailable(R.string.event_message_network_down),
        NetworkAvailable(R.string.event_message_network_up);

        int messageId;

        EventType(int messageId) {
            this.messageId = messageId;
        }

        public int getMessageId() {
            return messageId;
        }
    }
}
