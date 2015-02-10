/*
 *  Copyright 2013 Alexey Andreev.
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

import org.teavm.dom.browser.Window;
import org.teavm.javascript.spi.Async;
import org.teavm.javascript.spi.GeneratedBy;

import org.teavm.javascript.spi.Rename;
import org.teavm.javascript.spi.Superclass;
import org.teavm.jso.JS;
import org.teavm.platform.Platform;


/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
@Superclass("")
public class TObject {
    
    private TThread owner;
    private TObject monitorLock;
    private int monitorCount=0;
    /*private JSArray<NotifyListener> notifyListeners;*/
    private static final Window window = (Window)JS.getGlobal();
    
    /*
    @JSFunctor
    private static interface NotifyListener extends JSObject{
        void handleNotify();
    }*/
    @Async
    static void monitorEnter(TObject o){
        if ( o.monitorLock == null ){
            o.monitorLock = new TObject();
        }
        System.out.println("Thread "+Thread.currentThread()+" waiting  on lock for "+o);
        System.out.println("Lock owned by "+o.owner);
        if ( o.owner != null ){
            System.out.println(o.owner.getName());
        }
        while (o.owner != null && o.owner != TThread.currentThread() ){
            try {
                System.out.println(Thread.currentThread()+" waiting on lock");
                o.monitorLock.wait();
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted", ex);
            }
        }
        if ( o.owner != null && o.owner != TThread.currentThread()){
            System.out.println("Thread "+TThread.currentThread()+" has been granted a lock which is owned by "+o.owner);
            throw new RuntimeException("Thread "+TThread.currentThread()+" has been granted a lock which is owned by "+o.owner);
        }
        System.out.println("Thread "+Thread.currentThread()+" obtaining lock for "+o);
        System.out.println("The old owner was "+o.owner);
        o.owner = TThread.currentThread();
        o.monitorCount++;
        
    }
    
    static void monitorExit(TObject o){
        
        o.monitorCount--;
        if ( o.monitorCount == 0 && o.monitorLock != null){
            o.owner = null;
            o.monitorLock.notifyAll();
        }
    }
    
    static boolean holdsLock(TObject o){
        return o.owner == TThread.currentThread();
    }
    
    @Rename("fakeInit")
    public TObject() {
    }

    @Rename("<init>")
    private void init() {
        Platform.getPlatformObject(this).setId(Platform.nextObjectId());
    }

    @Rename("getClass")
    public final TClass<?> getClass0() {
        return TClass.getClass(Platform.getPlatformObject(this).getPlatformClass());
    }

    @Override
    public int hashCode() {
        return identity();
    }

    @Rename("equals")
    public boolean equals0(TObject other) {
        return this == other;
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + TInteger.toHexString(identity());
    }

    int identity() {
        return Platform.getPlatformObject(this).getId();
    }

    @Override
    protected Object clone() throws TCloneNotSupportedException {
        if (!(this instanceof TCloneable) && Platform.getPlatformObject(this)
                .getPlatformClass().getMetadata().getArrayItem() == null) {
            throw new TCloneNotSupportedException();
        }
        Object result = Platform.clone(this);
        Platform.getPlatformObject(result).setId(Platform.nextObjectId());
        return result;
    }

    @Rename("notify")
    @GeneratedBy(ObjectNativeGenerator.class)
    public final native void notify0();/*{
        if (notifyListeners != null && notifyListeners.getLength() > 0){
            notifyListeners.shift().handleNotify();
        }
    }*/

    
    @Rename("notifyAll")
    @GeneratedBy(ObjectNativeGenerator.class)
    public final native void notifyAll0();/*{
        if (notifyListeners != null){
            JSArray<NotifyListener> listeners = window.newArray();
            while (notifyListeners.getLength() > 0 ){
                listeners.push(notifyListeners.shift());
            }
            while ( listeners.getLength() > 0 ){
                listeners.shift().handleNotify();
            }
        }
        
    }*/
    
    @Async
    @Rename("wait")
    public final  void wait0(long timeout) throws TInterruptedException{
        try {
            wait(timeout, 0);
        } catch ( InterruptedException ex){
            throw new TInterruptedException();
        }
    }
    
    @Async
    @Rename("wait")
    @GeneratedBy(ObjectNativeGenerator.class)
    public native final void wait0(long timeout, int nanos) throws TInterruptedException;

    /*
    @Rename("wait")
    public final void wait0(long timeout, int nanos, final AsyncCallback<Void> callback){
        if ( notifyListeners == null ){
            notifyListeners = window.newArray(); 
        }
        final TThread currentThread = TThread.currentThread();
        notifyListeners.push(new NotifyListener(){

            @Override
            public void handleNotify() {
                TThread.setCurrentThread(currentThread);
                try {
                    callback.complete(null);
                } finally {
                    TThread.setCurrentThread(TThread.getMainThread());
                }

            }

        });
    }
    */
    @Async
    @Rename("wait")
    public final void wait0() throws TInterruptedException {
        try {
            wait(0l);
        } catch ( InterruptedException ex){
            throw new TInterruptedException();
        }
    }

    @Override
    protected void finalize() throws TThrowable {
    }

    public static TObject wrap(Object obj) {
        return (TObject)obj;
    }
}
