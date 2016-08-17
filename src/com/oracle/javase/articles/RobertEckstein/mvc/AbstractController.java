/*
 *  https://web.archive.org/web/20100801021330/http://www.oracle.com/technetwork/articles/javase/index-142890.html
 *
 * see also: https://github.com/101companies/101repo/blob/master/technologies/Java_platform/samples/sunMvcSample/com/sun/example/mvc/view/AbstractViewPanel.java
 */
package com.oracle.javase.articles.RobertEckstein.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *  Abstract controller that holds reference to views and models.
 *
 * <blockquote>The primary difference between this design and the more traditional version
 *     of MVC is that the notifications of state changes in model objects are communicated
 *     to the view through the controller. Hence, the controller mediates the flow of data
 *     between model and view objects in both directions. View objects, as always, use the
 *     controller to translate user actions into property updates on the model. In addition,
 *     changes in model state are communicated to view objects through an application's
 *     controller objects.
 *   <p> Thus, when all three components are instantiated, the view and the model will both
 *     register with the controller.
 * </blockquote>
 *
 * Changes by brian witt :
 * <ul>
 *   <li>Modified for template of the Model and the View to use.
 *          Abstract view was renamed from {@code AbstractViewPanel} to {@code ViewEventSink}.
 *   </li>
 * </ul>
 *
 * @author Robert Eckstein
 * @author brian witt
 * @param <M> Superclass of models to manage.
 * @param <V> Superclass of views to alert.
 */
public abstract class AbstractController<M extends AbstractModel, V extends ViewEventSink>
        implements PropertyChangeListener
{

    private final ArrayList<ViewEventSink>    registeredViews;
    private final ArrayList<AbstractModel>    registeredModels;

    public AbstractController()
    {
        registeredViews = new ArrayList<ViewEventSink>();
        registeredModels = new ArrayList<AbstractModel>();
    }


    public void addModel(AbstractModel model)
    {
        registeredModels.add(model);
        model.addPropertyChangeListener(this);
    }

    public void removeModel(AbstractModel model)
    {
        registeredModels.remove(model);
        model.removePropertyChangeListener(this);
    }

    public void addView(ViewEventSink view)
    {
        registeredViews.add(view);
    }

    public void removeView(ViewEventSink view)
    {
        registeredViews.remove(view);
    }


    //  Use this to observe property changes from registered models
    //  and propagate them on to all the views.


    public void propertyChange(PropertyChangeEvent evt) {

        for (ViewEventSink view: registeredViews) {
            view.modelPropertyChange(evt);
        }
    }


    /**
     * This is a convenience method that subclasses can call upon
     * to fire property changes back to the models. This method
     * uses reflection to inspect each of the model classes
     * to determine whether it is the owner of the property
     * in question. If it isn't, a NoSuchMethodException is thrown,
     * which the method ignores.
     *
     * E.g. if {@link propertyName} is "TEXT_WEIGHT", then method tries to
     * invoke {@code setTEXT_WEIGHT(&lt;newValue.getClass()&gt;)} on all models.
     *
     * @param propertyName = The name of the property.
     * @param newValue = An object that represents the new value
     *      of the property. Class-type used in finding "set" method to call.
     */
    protected void setModelProperty(String propertyName, Object newValue)
    {

        for (AbstractModel model: registeredModels) {
            try {

                Method method = model.getClass().
                    getMethod("set"+propertyName, new Class[] {
                                                      newValue.getClass()
                                                  }
                             );
                method.invoke(model, newValue);

            } catch (Exception ex) {
                //  Handle exception.  There are many possibilities.
            }
        }
    }


}
