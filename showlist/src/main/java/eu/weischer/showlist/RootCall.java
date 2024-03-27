package eu.weischer.showlist;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.weischer.root.application.App;

public class RootCall<A, R> implements Callable<R>, Supplier<R> {
    private A argument = null;
    private Function<A, R> function = null;

    public RootCall (Function<A, R> function, A argument) {
        this.function = function;
        this.argument = argument;
    }

    @Override
    public R call() throws Exception {
        return function.apply(argument);
    }
    @Override
    public R get() {
        return function.apply(argument);
    }

    public Future<R> getFuture() {
        return App.getThreadPoolExecutor().submit(this);
    }
    public Future<R> getFuture(ExecutorService executorService) {
        return executorService.submit(this);
    }
    public CompletableFuture<R> getCompletableFuture() {
        return CompletableFuture.supplyAsync(this);
    }
    public CompletableFuture<R> getCompletableFuture(ExecutorService executorService) {
        return CompletableFuture.supplyAsync(this, executorService);
    }
}
