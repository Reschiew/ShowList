package eu.weischer.showlist;

/*
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;

import eu.weischer.root.application.Logger;

public class RootTimer {
    private static final Logger.LogAdapter log = Logger.getLogAdapter("RootTimer");

    public enum TimerState {
        running,
        cancelled,
        finished
    }
    public final static class TimerQuery {
        private long duration;
        private ExecutorService responseExecutor;
        private Consumer<TimerResponse> responseFinish;
        private long firstTime = 0;
        private long lastTime = 0;
        private long synchronize = 0;
        private Consumer<TimerResponse> responseTrigger = null;
        private Consumer<TimerResponse> responseCancel = null;
        private Object tag = null;
        public TimerQuery(long duration, ExecutorService responseExecutor, Consumer<TimerResponse> responseFinish) {
            this.duration = duration;
            this.responseExecutor = responseExecutor;
            this.responseFinish = responseFinish;
        }
        public TimerQuery setFirstTime(long firstTime) {
            this.firstTime = firstTime;
            return (this);
        }
        public TimerQuery setLastTime(long lastTime) {
            this.lastTime = lastTime;
            return (this);
        }
        public TimerQuery setSynchronize(long synchronize) {
            this.synchronize = synchronize;
            return (this);
        }
        public TimerQuery setResponseCancel(Consumer<TimerResponse> responseCancel) {
            this.responseCancel = responseCancel;
            return (this);
        }
        public TimerQuery setResponseFinish(Consumer<TimerResponse> responseFinish) {
            this.responseFinish = responseFinish;
            return (this);
        }
        public TimerQuery setTag(Object tag) {
            this.tag = tag;
            return (this);
        }
    }
    public final static class TimerResponse {
        private long id;
        private TimerState state;
        private Object tag;
        public TimerResponse(long id, TimerState state, Object tag) {
            this.id = id;
            this.state = state;
            this.tag = tag;
        }
        public TimerState getState() {
            return state;
        }
        public Object getTag() {
            return tag;
        }
        public long getId() {
            return id;
        }
    }
    private final static class TimerAttributes implements Comparable{
        private ExecutorService queueExecutor;
        private TimerQuery timerQuery;
        private ExecutorService timerExecutor = Executors.newSingleThreadExecutor();
        private RootTimer rootTimer;
        private boolean running = true;
        private TimerState state = TimerState.running;
        private TimerAttributes(RootTimer rootTimer, TimerQuery timerQuery) {
            this.rootTimer = rootTimer;
            this.timerQuery = timerQuery;
        }

        @Override
        public int compareTo(Object compareObject) {
            int compareValue =-2;
            TimerAttributes comparePunkt = (TimerAttributes)compareObject;
            // Vergleich auf Gleichheit
            if(this.x == comparePunkt.getX())
            {
                compareValue = 0;
            }
            // Vergleich auf kleiner
            if(this.x < comparePunkt.getX())
            {
                compareValue = -1;
            }
            // Vergleich auf größer
            if(this.x > comparePunkt.getX())
            {
                compareValue = 1;
            }
            return compareValue;
        }
    }

    private TreeSet<TimerQuery> treeSet;
}
*/

import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import eu.weischer.root.application.Logger;

public class RootTimer {
    private static final Logger.LogAdapter log = Logger.getLogAdapter("RootTimer");

    public enum TimerState {
        running,
        cancelled,
        finished
    }
    public final static class TimerQuery {
        private long duration;
        private long firstTime = 0;
        private long lastTime = 0;
        private long synchronize = 0;
        private ExecutorService responseExecutor;
        private Consumer<TimerResponse> responseTrigger = null;
        private Consumer<TimerResponse> responseCancel = null;
        private Consumer<TimerResponse> responseFinish;
        private Object tag = null;
        private Future<?> future = null;
        private long id;
        public TimerQuery(long duration, ExecutorService responseExecutor, Consumer<TimerResponse> responseFinish) {
            this.duration = duration;
            this.responseExecutor = responseExecutor;
            this.responseFinish = responseFinish;
            id = currentId++;
        }
        public TimerQuery setFirstTime(long firstTime) {
            this.firstTime = firstTime;
            return (this);
        }
        public TimerQuery setLastTime(long lastTime) {
            this.lastTime = lastTime;
            return (this);
       }
        public TimerQuery setSynchronize(long synchronize) {
            this.synchronize = synchronize;
            return (this);
        }
        public TimerQuery setResponseCancel(Consumer<TimerResponse> responseCancel) {
            this.responseCancel = responseCancel;
            return (this);
        }
        public TimerQuery setResponseFinish(Consumer<TimerResponse> responseFinish) {
            this.responseFinish = responseFinish;
            return (this);
        }
        public TimerQuery setTag(Object tag) {
            this.tag = tag;
            return (this);
        }
        public long getId() {
            return id;
        }
    }
    public final static class TimerResponse {
        private TimerState state;
        private Object tag = null;
        private TimerResponse(TimerState state, Object tag) {
            this.state = state;
            this.tag = tag;
        }
        public TimerState getState() {
            return state;
        }
        public Object getTag() {
            return tag;
        }
    }
    private final static class TimerAttributes {
        private ExecutorService queueExecutor;
        private TimerQuery timerQuery;
        private ExecutorService timerExecutor = Executors.newSingleThreadExecutor();
        private RootTimer rootTimer;
        private boolean running = true;
        private TimerState state = TimerState.running;
        private TimerAttributes(RootTimer rootTimer, TimerQuery timerQuery) {
            this.rootTimer = rootTimer;
            this.timerQuery = timerQuery;
        }
    }

    private static long currentId = 0;
    private final ExecutorService queueExecutor;
    private Map<Long, TimerAttributes> attribueMap = new HashMap<>();

    public RootTimer() {
        queueExecutor = Executors.newSingleThreadExecutor();
    }

    public long queueTimer (long duration, ExecutorService responseExecutor, Consumer<TimerResponse> responseTrigger) {
        return queueTimer(new TimerQuery(duration, responseExecutor, responseTrigger));
    }
    public long queueTimer (TimerQuery timerQuery) {
        try {
            if (isReady()) {
                timerQuery.future = queueExecutor.submit(getQueueTimer(timerQuery));
                return timerQuery.getId();
            } else
                return -1;
        } catch (Exception ex) {
            log.e(ex, "Excpeion during queueTimer");
            throw ex;
        }
    }

    private Runnable getQueueTimer(TimerQuery timerQuery) {
        return new RootRunnable((query) -> {
            TimerAttributes timerAttributes = new TimerAttributes(this, timerQuery);

/*
            Runnable timerEvent = new RootRunnable<TimerAttributes>(
                    (attributes) -> {
                        log.v("Timer event");
                        if (attributes.state == TimerState.running)
                            respond(new TimerResponse(attributes.timerQuery, TimerState.running));
                        return;
                    }, timerAttributes);
            Runnable timerFinished = new RootRunnable<TimerAttributes>(
                    (attributes) -> {
                        log.v("Timer finished");
                        if (attributes.state == TimerState.running) {
                            respond(new TimerResponse(attributes.timerQuery, TimerState.finished));
                        }
                        return;
                    }, timerAttributes);
*/


            timerAttributes.timerExecutor.submit(() -> {
                RootTimer rootTimer = timerAttributes.rootTimer;
                rootTimer.attribueMap.put(timerAttributes.timerQuery.id, timerAttributes);
                log.v("Timerthread start");
                SystemClock.sleep(2000);
                timerAttributes.state = TimerState.finished;
                respond(timerAttributes);
                log.v("Timerthread end");
            });
            return;
        }, timerQuery);
    }
    private void respond (TimerAttributes timerAttributes) {
        try {
            if (timerAttributes.timerQuery.responseExecutor != null) {
                TimerResponse timerResponse = new TimerResponse(timerAttributes.state, timerAttributes.timerQuery.tag);
                Consumer<TimerResponse> consumer = null;
                switch (timerAttributes.state) {
                    case running:
                        consumer = timerAttributes.timerQuery.responseTrigger;
                        break;
                    case finished:
                        consumer = timerAttributes.timerQuery.responseFinish;
                        break;
                    case cancelled:
                        consumer = timerAttributes.timerQuery.responseCancel;
                        break;
                    default:
                        break;
                }
                if (consumer != null)
                    timerAttributes.timerQuery.responseExecutor.submit(
                        new RootRunnable<>(consumer, timerResponse));
                if (timerAttributes.state != TimerState.running)
                    timerAttributes.rootTimer.attribueMap.remove(timerAttributes.timerQuery.id);
            }
        } catch (Exception ex) {
            log.e(ex, "Exception during respond");
        }
    }
    private void close(TimerAttributes timerAttributes) {
        try {
            if (isReady()) {

            }
        } catch (Exception ex) {
            log.e (ex, "Error during close");
        }
    }
    private boolean isReady() {
        return  (queueExecutor != null) &&
                (! queueExecutor.isTerminated()) &&
                (! queueExecutor.isShutdown());
    }
}
