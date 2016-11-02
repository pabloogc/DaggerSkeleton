package com.bq.daggerskeleton.common;


public final class SharedEvent<T> {

    private static final boolean DEBUG_MODE = false;

    private final boolean includeTrace;
    private Exception trace;
    private T event;
    private boolean consumed = false;
    private Class<?> consumer = null;
    private Class<?> consumerCandidate = null;


    public static <T> SharedEvent<T> create() {
        return create(DEBUG_MODE);
    }

    //Not final api
    private static <T> SharedEvent<T> create(boolean includeTrace) {
        return new SharedEvent<>(includeTrace);
    }

    private SharedEvent(boolean includeTrace) {
        this.includeTrace = includeTrace;
    }


    public void setConsumerCandidate(Class<?> consumerCandidate) {
        this.consumerCandidate = consumerCandidate;
    }

    public void reset() {
        reset(null);
    }

    public void reset(T event) {
        this.event = event;
        this.consumer = null;
        this.consumerCandidate = null;
        this.consumed = false;
    }

    public T take() {
        if (consumed()) {
            throw new IllegalStateException(
                    "This event was already consumed by: " + (consumer != null ? consumer.getName() : "UNKNOWN") + "" +
                            "See the exception cause for trace call where the original take was made.", trace);
        }
        if (includeTrace) {
            try {
                throw new Exception();
            } catch (Exception trace) {
                this.trace = trace; //This will keep the trace that led to the event being consumed
            }
        }
        consumer = consumerCandidate;
        consumed = true;
        return event;
    }

    public boolean consumed() {
        return consumed;
    }

    public T peek() {
        return event;
    }

    @Override
    public String toString() {
        return "SharedEvent{" +
                "event=" + event +
                ", consumer=" + (consumer != null ? consumer.getSimpleName() : "none") +
                '}';
    }
}
