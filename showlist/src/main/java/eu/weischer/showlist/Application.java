package eu.weischer.showlist;

import android.os.SystemClock;

import eu.weischer.root.application.App;

public class Application extends eu.weischer.root.application.RootApplication {
    @Override
    public Runnable getInitializationRunnable() {
        return () -> {
            try {
                log.i("start sleep");
                SystemClock.sleep(30000);
                log.i("end sleep");
            } catch (Exception ex) {
                log.e(ex,"Error during initialization");
            }
        };
    }
}

