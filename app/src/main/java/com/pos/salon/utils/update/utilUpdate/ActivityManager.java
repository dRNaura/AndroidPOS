
package com.pos.salon.utils.update.utilUpdate;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import java.util.LinkedList;
import java.util.List;


public final class ActivityManager implements Application.ActivityLifecycleCallbacks {
    private Context applicationContext;
    private static ActivityManager manager = new ActivityManager();

    public static ActivityManager get() {
        return manager;
    }

    private LinkedList<Activity> stack = new LinkedList<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (!stack.contains(activity)) {
            stack.add(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (stack.contains(activity)) {
            stack.remove(activity);
        }
    }

    public Activity topActivity() {
        Activity activity = null;
        if (!stack.isEmpty()) {
            activity = stack.getLast();
        }
        return activity;
    }

    public boolean pop() {
        Activity activity = null;
        if (!stack.isEmpty()) {
            activity = stack.pop();
        }
        return activity != null;
    }

    public List<Activity> getStack() {
        return this.stack;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void registerSelf(Context context) {
        Application application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(ActivityManager.get());
        this.applicationContext = context.getApplicationContext();
    }

    public void exit() {
        Activity activity;
        while (!stack.isEmpty()) {
            activity = stack.pop();
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        //System.exit(0);
        //Process.killProcess(Process.myPid());
    }
}
