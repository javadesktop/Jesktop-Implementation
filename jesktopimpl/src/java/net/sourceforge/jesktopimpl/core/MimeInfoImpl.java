/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
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

    private LaunchableTargetFactory mLaunchableTargetFactory;
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

        this.mLaunchableTargetFactory = launchableTargetFactory;
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
            targets[i] = mLaunchableTargetFactory.getLaunchableTarget((String) it.next());
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
