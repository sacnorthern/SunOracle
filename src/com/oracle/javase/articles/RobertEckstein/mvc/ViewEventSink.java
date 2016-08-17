/*
 * see : https://accu.org/index.php/journals/1524
 *
 * see also: https://github.com/101companies/101repo/blob/master/technologies/Java_platform/samples/sunMvcSample/com/sun/example/mvc/view/AbstractViewPanel.java
 */
package com.oracle.javase.articles.RobertEckstein.mvc;

import java.beans.PropertyChangeEvent;

/**
 *  Simple receiver of events , such as a View , in an MVC design pattern.
 *  Originally from "Overload Journal #88 - December 2008 + Programming Topics + Design of applications and programs ".
 *
 * @author Paul Grenyer
 */
public interface ViewEventSink
{
    /***
     *  Fire event when the model have changed.
     * @param evt Description of what changed.
     */
    public abstract void modelPropertyChange(
       final PropertyChangeEvent evt);

}
