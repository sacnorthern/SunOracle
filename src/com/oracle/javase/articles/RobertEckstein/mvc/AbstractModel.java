/*
 *  https://web.archive.org/web/20100801021330/http://www.oracle.com/technetwork/articles/javase/index-142890.html
 *
 * see also: https://github.com/101companies/101repo/blob/master/technologies/Java_platform/samples/sunMvcSample/com/sun/example/mvc/view/AbstractViewPanel.java
 */
package com.oracle.javase.articles.RobertEckstein.mvc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Robert Eckstein
 */
public abstract class AbstractModel
{

    protected PropertyChangeSupport propertyChangeSupport;

    public AbstractModel()
    {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

}

