package eu.weischer.showlist;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Work {
    private static final Work theWork = new Work();
    private static boolean initialized = false;
    public static synchronized void initialize() {
        if (! initialized) {
            initialized = true;
            theWork.messageList.addMessage("Work initialized");
        }
    }
    public static void F1() {
        try {
            theWork.writeService = Executors.newSingleThreadExecutor(Executors.privilegedThreadFactory());
            theWork.writeService.submit(theWork.generateMessage);
        } catch (Exception ex) {}
    }
    public static void F2() {
        if (theWork.writeService != null) {
            try {
                theWork.writeService.shutdownNow();
            } catch (Exception ex) {}
            theWork.writeService=null;
        }
    }
    public static void F3() {
    }
    public static void F4() {
    }
    public static void F5() {
    }
    public static void F6() {
        theWork.messageList.addMessage("Single message");
    }
    private ExecutorService writeService = null;
    private MessageList messageList = MessageList.getMessageList();
    private Runnable generateMessage = () -> {
        int messageNumber = 0;
        try {
            while (true) {
                messageNumber ++;
                messageList.addMessage("Message number " + messageNumber);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {}
    };
}
