/* ====================================================================
 * Copyright 2000 - 2004, The Jesktop project committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Portions of this code are copyright Apache Software Foundation, and licensed
 * under the Apache Software License 1.1
 */
package net.sourceforge.jesktopimpl.core;

import org.jesktop.mime.MimeInfo;
import org.jesktop.mime.MimeAlreadyRegisteredException;
import org.jesktop.mime.MimeNotRegisteredException;
import org.jesktop.launchable.LaunchableTarget;
import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;

import javax.swing.ImageIcon;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * This class is the default implementation of MimeInfo
 *
 * @author  Laurent Cornelis <nelis2@yahoo.com>
 * @version 1.0
 */
public class MimeInfoImpl implements MimeInfo, java.io.Serializable {

    private LaunchableTargetFactory launchableTargetFactory;
    private MimeManagerImpl manager;
    private String mime;
    private String description;
    private HashSet registeredExtensions;
    private ArrayList actions;

    /**
     * Create a MimeInfoImpl with the mime String, the description and a reference on the launchableTargetHolder (Only used by MimeManagerImpl)
     * @see MimeManagerImpl
     */
    public MimeInfoImpl(final MimeManagerImpl manager, final String mime, final String description,
                        final LaunchableTargetFactory launchableTargetFactory) {

        this.launchableTargetFactory = launchableTargetFactory;
        this.manager = manager;
        registeredExtensions = new HashSet(2);
        actions = new ArrayList();

        setMime(mime);
        setDescription(description);
    }

    // Javadocs will automatically import from interface.


    /**
     * Set the mime String
     */
    public synchronized void setMime(final String mime) {
        this.mime = mime;
    }

    /**
     * Method getMime
     *
     *
     * @return
     *
     */
    public synchronized String getMime() {
        return mime;
    }

    /**
     * Set the description
     */
    public synchronized void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Method getDescription
     *
     *
     * @return
     *
     */
    public synchronized String getDescription() {
        return description;
    }

    /**
     * Set the Icon (Not yet implemented)
     */
    public synchronized void setIcon(final String icon) {}

    /**
     * Not yet implemented
     */
    public synchronized ImageIcon getIcon() {
        return null;
    }

    /**
     * Set an Action (LaunchableTarget name) at a specified index (0 is primary action)
     */
    synchronized void setAction(final int index, final String action) {

        if (actions.contains(action)) {
            actions.remove(action);
        }

        actions.add(index, action);
    }

    /**
     * Remove an action (LaunchableTarget name)
     */
    synchronized void removeAction(final String action) {

        if (!actions.contains(action)) {
            return;
        }

        actions.remove(action);
    }

    /**
     * Method getActions
     *
     *
     * @return
     *
     */
    public synchronized LaunchableTarget[] getActions() {

        LaunchableTarget[] targets = new LaunchableTarget[actions.size()];
        Iterator it = actions.iterator();

        for (int i = 0; it.hasNext(); i++) {
            targets[i] = launchableTargetFactory.getLaunchableTarget((String) it.next());
        }

        return targets;
    }

    /**
     * Register a new extension for this Mime
     */
    public synchronized void registerExtension(final String extension)
            throws MimeAlreadyRegisteredException {

        if (manager.isExtensionRegistered(extension)) {
            throw new MimeAlreadyRegisteredException(extension);
        }

        registeredExtensions.add(extension);
    }

    /**
     * Unregister an extention for this Mime
     */
    public synchronized void unregisterExtension(final String extension)
            throws MimeNotRegisteredException {

        if (!manager.isExtensionRegistered(extension)) {
            throw new MimeNotRegisteredException(extension);
        }

        registeredExtensions.remove(extension);
    }

    /**
     * Check if an extension is registered for this Mime
     */
    public synchronized boolean isRegistered(final String extension) {
        return registeredExtensions.contains(extension);
    }

    /**
     * Method getExtensions
     *
     *
     * @return
     *
     */
    public synchronized String[] getExtensions() {
        return (String[]) registeredExtensions.toArray(new String[registeredExtensions.size()]);
    }

    /**
     * Method getExtensionsAsString
     *
     *
     * @return
     *
     */
    public synchronized String getExtensionsAsString() {

        Iterator it = registeredExtensions.iterator();
        StringBuffer buf = new StringBuffer();

        while (it.hasNext()) {
            buf.append(it.next());

            if (it.hasNext()) {
                buf.append(", ");
            }
        }

        return buf.toString();
    }
}
