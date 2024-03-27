package eu.weischer.showlist;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class AsyncResult<T> {
    private T result = null;
    private Throwable throwable = null;

    public AsyncResult(T result, Throwable throwable) {
        this.result = result;
        this.throwable = throwable;
    }
    public AsyncResult(T result) {
        this.result = result;
    }
    public AsyncResult(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
    public T getResult() {
        return result;
    }
    public boolean hasError() {
        return throwable != null;
    }
    public boolean normal() {
        return throwable == null;
    }
    public boolean cancelled() {
        return  (Object) throwable instanceof CancellationException ||
                (Object) throwable instanceof InterruptedException;
    }
    public boolean timeout() {
        return  (Object) throwable instanceof TimeoutException;
    }
    public boolean failed() {
        return  (Object) throwable instanceof ExecutionException;
    }
    public Throwable getCause() {
        return  throwable == null ? null : throwable.getCause();
    }
}
