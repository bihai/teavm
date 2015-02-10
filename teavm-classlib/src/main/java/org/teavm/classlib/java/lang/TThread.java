/*
 *  Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;

import org.teavm.dependency.PluggableDependency;
import org.teavm.dom.browser.TimerHandler;
import org.teavm.dom.browser.Window;
import org.teavm.javascript.spi.Async;
import org.teavm.javascript.spi.GeneratedBy;
import org.teavm.jso.JS;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.platform.async.AsyncCallback;


/**
 *
 * @author Alexey Andreev
 */
public class TThread extends TObject implements TRunnable {

    private static Window window = (Window)JS.getGlobal();
    private static TThread mainThread = new TThread(TString.wrap("main"));
    private static TThread currentThread = mainThread;
    private static long nextId = 1;
    private static int activeCount = 1;
    private long id;

    private TString name;
    private TRunnable target;
    
    /*
    private static interface RootInvoker extends JSObject {
        @JSMethod("$rt_rootInvocationAdapter")
        public LaunchFunctor rootInvocationAdapter(JSObject method);
    }
    
    @JSFunctor
    private static interface LaunchFunctor extends JSObject {
        public void launch();
    }
    
    */
    public TThread() {
        this(null, null);
    }

    public TThread(TString name) {
        this(null, name);
    }

    public TThread(TRunnable target) {
        this(target, null );
    }

    public TThread(TRunnable target, TString name ) {
        this.name = name;
        this.target = target;
        id=nextId++;
    }
    @PluggableDependency(ThreadNativeGenerator.class)
    @GeneratedBy(ThreadNativeGenerator.class)
    public native void start();/*{
        final TThread self = this;
        window.setTimeout(new TimerHandler() {
            @Override public void onTimer() {
                RootInvoker rootInvocationAdapter = (RootInvoker)window;
                rootInvocationAdapter.rootInvocationAdapter(new LaunchFunctor(){

                    @Override
                    public void launch() {
                        TThread.launch(self);
                    }
                    
                }).launch();
            }
        }, 0);
    }*/

    private static void launch(TThread thread) {
        //try {
            activeCount++;
            setCurrentThread(thread);
            thread.run();
        //} finally {
            activeCount--;
            setCurrentThread(mainThread);
        //}
        
        
    }
    
    static void setCurrentThread(TThread thread){
        currentThread = thread;
    }
    static TThread getMainThread(){
        return mainThread;
    }

    @Override
    public void run() {
        if (target != null) {
            target.run();
        }
    }

    public static TThread currentThread() {
        return currentThread;
    }

    public TString getName() {
        return name;
    }

    @Async
    public static native void yield();

    private static void yield(final AsyncCallback<Void> callback) {
        final TThread current = currentThread();
        window.setTimeout(new TimerHandler() {
            @Override public void onTimer() {
                setCurrentThread(current);
                callback.complete(null);
                setCurrentThread(mainThread);
            }
        }, 0);
    }

    public void interrupt() {
    }

    public static boolean interrupted() {
        return false;
    }

    public boolean isInterrupted() {
        return false;
    }

    public static int activeCount() {
        return activeCount;
    }

    public long getId() {
        return id;
    }

    public static boolean holdsLock(TObject obj) {
        return TObject.holdsLock(obj);
    }


    @Async
    public static native void sleep(long millis) throws TInterruptedException;

    private static void sleep(long millis, final AsyncCallback<Void> callback) {
        final TThread current = currentThread();
        
        window.setTimeout(new TimerHandler() {
            @Override public void onTimer() {
                setCurrentThread(current);
                callback.complete(null);
                setCurrentThread(mainThread);
            }
        }, millis);
    }

}
