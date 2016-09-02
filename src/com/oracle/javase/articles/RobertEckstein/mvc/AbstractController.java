/*
 *  https://web.archive.org/web/20100801021330/http://www.oracle.com/technetwork/articles/javase/index-142890.html
 *
 * see also: https://github.com/101companies/101repo/blob/master/technologies/Java_platform/samples/sunMvcSample/com/sun/example/mvc/view/AbstractViewPanel.java
 */
package com.oracle.javase.articles.RobertEckstein.mvc;

import static com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory.propertyName;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.netbeans.modules.vcscore.util.WeakList;

/**
 *  Abstract controller that holds reference to multiple views and models.
 *  The {@link AbstractController} is the main public interface and object for a
 *  collection of models.  Views add themselves as event listeners.
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
 *   <li>  Since we hold only weak-refs to views, <strong>caller must hold a strong-ref,
 *          otherwise the view could go disappear...!</strong>
 * </ul>
 *
 * @author Robert Eckstein
 * @author brian witt
 * @param <M> Superclass of models to manage.
 * @param <V> Superclass of views to alert.
 * @see <a href="https://community.oracle.com/blogs/enicholas/2006/05/04/understanding-weak-references">weak object references</a>
 */
public abstract class AbstractController<M extends AbstractModel, V extends ViewEventSink>
        implements PropertyChangeListener
{

    private final WeakList<ViewEventSink>    registeredViews;
    private final ArrayList<AbstractModel>    registeredModels;

    /***
     *  Create controller with empty view and model lists.
     *  The controller is the central object that holds &quot;strong&quot;
     *  references to models,
     *  and &quot;weak&quot; references (Java 1.2) to the views.
     */
    public AbstractController()
    {
        registeredViews = new WeakList<ViewEventSink>();
        registeredModels = new ArrayList<AbstractModel>();
    }


    /***
     *  Add one more model to the list of managed models.
     *  The controller will add itself as the only property-change listener
     *  (and will re-broadcast changes out to all views).
     *
     * @param model Model to add
     */
    public void addModel(AbstractModel model)
    {
        registeredModels.add(model);
        model.addPropertyChangeListener(this);
    }

    /***
     *  Remove a prior-enrolled model from being managed by this controller.
     *  First it severs the property-change listener hook into the model.
     * @param model to remove, added already.
     */
    public void removeModel(AbstractModel model)
    {
        model.removePropertyChangeListener(this);
        registeredModels.remove(model);
    }

    /***
     *  Add a view (event sink) to be notified about property changes.
     *  Since we hold only weak-refs to views, <strong>caller must hold a strong-ref,
     *  otherwise the view could go disappear...!</strong>
     *
     * @param view view that will {@code modelPropertyChange()} callbacks.
     */
    public void addView(ViewEventSink view)
    {
        registeredViews.add(view);
    }

    /***
     *  Disconnect event-sink from property-change events from the models managed by this controller.
     *  This is a "courtesy call" since we hold weak references to views.
     *  NB, if controller-object is only "reference" to a view-object, then the view-object
     *  might already have been GC'ed.
     *
     * @param view to remove.
     */
    public void removeView(ViewEventSink view)
    {
        registeredViews.remove(view);
    }


    /***
     * {@inheritDoc}
     *
     * <p> Iterates thru all registers and not-GC'ed views with a
     *  {link #modelPropertyChange()} callback.
     *  Views are held with weak-ref , so if larger application drops all
     *  references to the view, then it is gone.
     *
     * @param evt containing property name, and old and new values.
     */
    public void propertyChange(PropertyChangeEvent evt) {

        //  Use this to observe property changes from registered models
        //  and propagate them on to all the views.
        //
        //  The model calls here when a property changes.  This method
        //  will broadcast out to interested views (event sinks).

        for (ViewEventSink view: registeredViews) {
            view.modelPropertyChange(evt);
        }
    }


    /**
     * This is a convenience method that subclasses can call upon
     * to fire property changes back to the models. This method
     * uses reflection to inspect each of the model classes
     * to determine whether it is the owner of the property
     * in question. If it isn't, a NoSuchMethodException is thrown (internally),
     * which the method ignores.
     *
     * <p> E.g. if {@link propertyName} is "TEXT_WEIGHT", then method tries to
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
