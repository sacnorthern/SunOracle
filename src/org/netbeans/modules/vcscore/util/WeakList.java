/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 *  Downloaded from http://alvinalexander.com/java/jwarehouse/netbeans-src/vcscore/src/org/netbeans/modules/vcscore/util/WeakList.java.shtml
 *  retrieved Sept 2016.
 *
 *  Changes by Brian Witt, Sept 2016.
 *  <ul>
 *    <li> Added @Override annotation.
 *    <li> Changed into parameterized class-type.
 *    <li> {@link #get()) and {@link #removeReleased()} are {@code synchronized} methods.
 *  </ul
 */

package org.netbeans.modules.vcscore.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A simple list that holds only weak references to the original objects.
 * @author  Martin Entlicher
 * @param <T> type inside the array-list.
 */
public class WeakList<T> extends AbstractList<T> {

    private ArrayList< WeakReference<T> > items;

    /** Creates new WeakList */
    public WeakList() {
        items = new ArrayList< WeakReference<T> >();
    }

    public WeakList(Collection c) {
        items = new ArrayList< WeakReference<T> >();
        addAll(0, c);
    }

    @Override
    public void add(int index, T element) {
        items.add(index, new WeakReference(element));
    }

    @Override
    public Iterator iterator() {
        return new WeakListIterator();
    }

    @Override
    public int size() {
        removeReleased();
        return items.size();
    }

    @Override
    public synchronized T get(int index) {
        return ((WeakReference<T>) items.get(index)).get();
    }

    private synchronized void removeReleased() {
        for (Iterator it = items.iterator(); it.hasNext(); ) {
            WeakReference<T> ref = (WeakReference<T>) it.next();
            if (ref.get() == null) items.remove(ref);
        }
    }

    private class WeakListIterator implements Iterator<T> {

        private int n;
        private int i;

        public WeakListIterator() {
            n = size();
            i = 0;
        }

        @Override
        public boolean hasNext() {
            return i < n;
        }

        @Override
        public T next() {
            return get(i++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
