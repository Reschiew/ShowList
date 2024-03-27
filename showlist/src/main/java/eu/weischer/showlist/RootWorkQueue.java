package eu.weischer.showlist;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class RootWorkQueue<R extends RootWorkQueue.Response> {
    public class Query {
        public Query(ExecutorService responseExecutor, Consumer<R> responseConsumer) {
            this.responseExecutor = responseExecutor;
            this.responseConsumer = responseConsumer;
        }
        public ExecutorService responseExecutor;
        public Consumer<R> responseConsumer;
    }
    public class Response {
        public Response(Query query) {
            this.query = query;
        }
        public Query query;
    }
    protected final ExecutorService queueExecutor;

    public RootWorkQueue() {
        queueExecutor = Executors.newSingleThreadExecutor();
    }

    public <A> CompletableFuture<Void> queueOrder(A argument) {
        return
            CompletableFuture.runAsync(getOrder(argument), queueExecutor);
    }
    public CompletableFuture<Void> queue(Runnable runnable) {
        return
                CompletableFuture.runAsync(runnable, queueExecutor);
    }

    protected <A> Runnable getOrder (A argument) {return null;}
    protected CompletableFuture<Void> respond(R answer) {
        return CompletableFuture.runAsync(
                new RootRunnable<R>(answer.query.responseConsumer, answer),
                answer.query.responseExecutor);
    }
    protected boolean isReady() {
        return  (queueExecutor != null) &&
                (! queueExecutor.isTerminated()) &&
                (! queueExecutor.isShutdown());
    }
}
