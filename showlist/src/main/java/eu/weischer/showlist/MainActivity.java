package eu.weischer.showlist;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import eu.weischer.root.activity.Layout;
import eu.weischer.root.activity.MenuItemHandler;
import eu.weischer.root.activity.RootActivity;
import eu.weischer.root.activity.RootRecyclerAdapter;
import eu.weischer.root.application.App;
import eu.weischer.root.application.Logger;
import eu.weischer.showlist.databinding.ActivityMainBinding;
import eu.weischer.showlist.databinding.ListBinding;

@Layout(bindingClass = ActivityMainBinding.class, setNavigatiobBarColor = true, menuId=R.menu.main_options)
public class MainActivity  extends RootActivity {
    private static final Logger.LogAdapter log = Logger.getLogAdapter("MainActivity");
    private class Test  implements Comparable {
        private int member;
        public Test(int member) {
            this.member = member;
        }
        public int getMember() {
            return member;
        }
        @Override
        public int compareTo(Object compareObject) {
            return (this.member <= ((Test)compareObject).getMember()) ? -1 : 1;        }
    }
    private TreeSet<Test> treeSet = new TreeSet<>();
    private MessageList messageList = MessageList.getMessageList();
    private ActivityMainBinding binder;
    private RecyclerView recyclerView = null;
    private boolean scrollDown = false;
    private RootRecyclerAdapter<ListBinding, MessageList.Message> adapter;
    private MessageList.MessageUpdate update = () -> {
        ArrayList<MessageList.Message> oldList = adapter.getList();
        ArrayList<MessageList.Message> newList = messageList.getList();
        long firstOldNumber = oldList.isEmpty() ? 0 : oldList.get(0).getNumber();
        long firstNewNumber = newList.isEmpty() ? 0 : newList.get(0).getNumber();
        long firstDifference = firstNewNumber - firstOldNumber;
        if (firstDifference > 0)
            adapter.notifyItemRangeRemoved(0, (int) firstDifference);
        long lastOldNumber = oldList.isEmpty() ? -1 : oldList.get(oldList.size()-1).getNumber();
        long lastNewNumber = newList.isEmpty() ? -1 : newList.get(newList.size()-1).getNumber();
        long lastDifference = lastNewNumber - lastOldNumber;
        adapter.setList(newList);
        if (lastDifference > 0)
            adapter.notifyItemRangeInserted((int) (oldList.size() - firstDifference), (int) lastDifference);
        if (scrollDown)
            recyclerView.scrollToPosition(newList.size()-1);

    };
    private RootTimer rootTimer = new RootTimer();

    private Consumer<RootTimer.TimerResponse> timerResponse = (response) -> {
        String message = "Id=" + response.getTag() + " " + response.getState().toString();
        messageList.addMessage(message);
        log.v(message);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.i("onCreate");
        binder = getBinder();
        binder.setTitle("ShowList");

        adapter = new RootRecyclerAdapter<>(this, messageList.getList(), R.layout.list, null);

        recyclerView = binder.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycler_view_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollDown = false;
                return false;
            }
        });
    }

    @MenuItemHandler( id = R.id.down )
    public void menuItemDown() {
        scrollDown = true;
        update.update();
    }
    @MenuItemHandler( id = R.id.clear )
    public void menuItemClear() {
        messageList.clear();
        scrollDown = false;
        adapter.updateList(messageList.getList());
    }
    @MenuItemHandler( id = R.id.time )
    public void menuItemTime() {
        messageList.toggleTime();
        adapter.updateList(messageList.getList());
    }

    @Override
    protected void startWork() {
        Work.initialize();
        log.i("startWork");
        messageList.setUpdate(update);
    };
    @Override
    protected void stopWork() {
        log.i("stopWork");
        messageList.setUpdate(null);
    };
    public void action1(View v) {
        Work.F1();
    }
    public void action2(View v) {
        Work.F2();
    }
    public void action3(View v) {
        RootTimer.TimerQuery query = new RootTimer.TimerQuery(1000, App.getMainExecutorService(), timerResponse)
                .setTag(123);
        rootTimer.queueTimer(query);
//        Work.F3();
    }
    public void action4(View v) {
        Test test0 = new Test(0);
        Test test1 = new Test(1);
        Test test2 = new Test(2);
        Test test3 = new Test(3);
        Test test4 = new Test(4);
        Test test5 = new Test(2);
        treeSet.add(test3);
        treeSet.add(test1);
        treeSet.add(test0);
        treeSet.add(test2);
        treeSet.add(test4);
        treeSet.add(test5);
        for (Test test : treeSet) {
            messageList.addMessage("Member " + test.getMember());
        }
//        Work.F4();
    }
    public void action5(View v) {
//        Work.F5();
    }
    public void action6(View v) {
        Work.F6();
    }
}
