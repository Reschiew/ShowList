package eu.weischer.showlist;


import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import eu.weischer.root.application.App;

public class RootRunnable <A> implements Runnable {
    private Consumer<A> consumer = null;
    private A argument = null;

    public RootRunnable(Consumer<A> consumer, A argument) {
        this.consumer = consumer;
        this.argument = argument;
    }

    @Override
    public void run() {
        consumer.accept(argument);
    }
    public Future<Void> getFuture() {
        return App.getThreadPoolExecutor().submit(this, null);
    }
    public Future<Void> getFuture(ExecutorService executorService) {
        return executorService.submit(this, null);
    }
    public CompletableFuture<Void> getCompletableFuture() {
        return CompletableFuture.runAsync(this);
    }
    public CompletableFuture<Void> getCompletableFuture(ExecutorService executorService) {
        return CompletableFuture.runAsync(this, executorService);
    }
}
