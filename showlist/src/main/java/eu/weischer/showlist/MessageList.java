package eu.weischer.showlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import eu.weischer.root.activity.RootRecyclerAdapter;
import eu.weischer.root.application.App;
import eu.weischer.root.application.RootApplication;
import eu.weischer.showlist.databinding.ListBinding;

public class MessageList {
    public static final class Message {
        private long time;
        private String text;
        private MessageList list;
        private long number;
        public Message(String text, MessageList list, long number) {
            this.text = text;
            this.list = list;
            this.number = number;
            time = System.currentTimeMillis();
        }
        public long getNumber() {
            return number;
        }
        public String getTime() {
            SimpleDateFormat simpleDateFormat = list.getSimpleDateFormat();
            return simpleDateFormat==null ? "" : simpleDateFormat.format(new Date(time));
        }
        public String getText() {
            return text;
        }
        public String getLine() {
            String timeText = list.getTimestamp() ? getTime() + " " : "";
            return timeText + text;
        }
    }
    private static final MessageList theMessageList = new MessageList();
    public static MessageList getMessageList() {
        return theMessageList;
    }
    public interface MessageUpdate {
        void update();
    }
    private static final int maxListSize = 5000;
    private String timePattern ="mm:ss.SSS";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timePattern);
    private LinkedList<Message> list = new LinkedList<>();
    private MessageUpdate messageUpdate = null;
    private long messageNumber = 0;
    private boolean timestamp = true;

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
        simpleDateFormat = timePattern.isEmpty() ? null : new SimpleDateFormat(timePattern);
    }
    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }
    public synchronized void addMessage(String text) {
        list.add(new Message(text, this, messageNumber));
        messageNumber ++;
        while (list.size() > maxListSize)
            list.removeFirst();
        update();
    }
    public synchronized ArrayList<Message> getList() {
        return new ArrayList<Message>(list);
    }
    public synchronized void setUpdate(MessageUpdate messageUpdate) {
        this.messageUpdate = messageUpdate;
        update();
    }
    public synchronized void clear() {
        list.clear();
        messageNumber = 0;
    }
    public synchronized boolean getTimestamp() {
        return timestamp;
    }
    public synchronized void toggleTime() {
        timestamp = ! timestamp;
    }
    private void update() {
        if (messageUpdate != null)
            App.postOnUiThread(() -> {
                messageUpdate.update();
            });
    }
}
