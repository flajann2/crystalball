/*
 * Controller.java
 *
 * Created on September 18, 2002, 4:25 PM
 */

package com.lrc.util;

/** Controller class pattern. An object that serves as the controller implements this interface.
 *
 * @author  fred
 */
public interface Controller
{
    /** The initialization function creates all resources and objects that are to be
     * controlled. The context may be null if not used. All threads, etc. are launched here,
     * as well as any user interfaces and the like.
     */
    void initializeController(Object context);

    /** Completely shuts down controller. Releases all resources associated.
     */
    void disposeController();

    /** Saves state of controller to some context. May be called at any time to persist state.
     */
    void persistController(Object context);

    /** Restores the state of controller from the context.
     */
    void restoreController(Object context);
}
