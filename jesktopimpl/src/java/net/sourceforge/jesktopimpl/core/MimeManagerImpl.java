/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.core;

import org.jesktop.mime.MimeManager;
import org.jesktop.mime.MimeInfo;
import org.jesktop.mime.MimeAlreadyRegisteredException;
import org.jesktop.mime.MimeNotRegisteredException;
import org.jesktop.api.DesktopKernelAware;
import org.jesktop.api.DesktopKernel;
import org.jesktop.launchable.LaunchableTarget;
import org.apache.avalon.cornerstone.services.store.ObjectRepository;
import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;

import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.Serializable;


/**
 *
 * This class (singleton) is a persistent representation of the Mime types registered on the system.
 * It helps also to manage these Mimes
 *
 * @author  Laurent Cornelis <nelis2@yahoo.com>
 * @version 1.0
 */
public class MimeManagerImpl implements MimeManager, Serializable {

    private static final String KEY = "jesktop-registered-mimes";
    protected static final String ALLOWED_TARGET_NAME = "System/MimeConfiguration";
    private static MimeManagerImpl instance = null;
    private static transient ObjectRepository repository;
    private static transient LaunchableTargetFactory mLaunchableTargetFactory;
    private HashMap registeredMimes;

    protected MimeManagerImpl(ObjectRepository repository) {

        registeredMimes = new HashMap();
        this.repository = repository;
    }

    /**
     * Save the manager on disk
     */
    private synchronized void save() {
        repository.put(KEY, this);
    }

    /**
     * Factory method to create a MimeInfo (it don't register it)
     */
    public synchronized MimeInfo createMimeInfo(String mime, String description)
            throws MimeAlreadyRegisteredException {

        if (isRegistered(mime)) {
            throw new MimeAlreadyRegisteredException(mime);
        }

        return new MimeInfoImpl(this, mime, description, mLaunchableTargetFactory);
    }

    /**
     * Update the mime string and description of a MimeInfo
     */
    public synchronized MimeInfo updateMimeInfo(MimeInfo minfo, String mime, String description)
            throws MimeAlreadyRegisteredException, MimeNotRegisteredException {

        if (!isRegistered(minfo.getMime())) {
            throw new MimeNotRegisteredException(mime);
        }

        if (isRegistered(mime)) {
            MimeInfo minfo2 = getMimeInfoForMime(mime);

            if (minfo != minfo2) {
                throw new MimeAlreadyRegisteredException(mime);
            }
        }

        unregisterMime(mime);

        MimeInfoImpl impl = (MimeInfoImpl) minfo;

        impl.setMime(mime);
        impl.setDescription(description);
        registerMime(minfo);
        save();

        return impl;
    }

    /**
     * Register the MimeInfo to the system
     */
    public synchronized void registerMime(MimeInfo minfo) throws MimeAlreadyRegisteredException {

        if (isRegistered(minfo.getMime())) {
            throw new MimeAlreadyRegisteredException(minfo.getMime());
        }

        registeredMimes.put(minfo.getMime().toLowerCase(), minfo);
        save();
    }

    /**
     * Unregister a MimeInfo from the system
     */
    public synchronized void unregisterMime(String mime) throws MimeNotRegisteredException {

        if (!isRegistered(mime)) {
            throw new MimeNotRegisteredException(mime);
        }

        registeredMimes.remove(mime.toLowerCase());
        save();
    }

    /**
     * Check is this Mime is registered
     */
    public synchronized boolean isRegistered(String mime) {
        return registeredMimes.containsKey(mime.toLowerCase());
    }

    /**
     * Get MimeInfo from file extension (p.e. gif)
     */
    public synchronized MimeInfo getMimeInfoForExtension(String extension) {

        if (!isExtensionRegistered(extension)) {
            return null;
        }

        Iterator mimes = registeredMimes.values().iterator();

        while (mimes.hasNext()) {
            MimeInfoImpl minfo = (MimeInfoImpl) mimes.next();

            if (minfo.isRegistered(extension)) {
                return minfo;
            }
        }

        return null;
    }

    /**
     * Get MimeInfo from a Mime String
     */
    public synchronized MimeInfo getMimeInfoForMime(String mime) {

        if (!isRegistered(mime)) {
            return null;
        }

        return (MimeInfo) registeredMimes.get(mime);
    }

    /**
     * Public method to register an action for Mime
     */
    public synchronized void registerActionForMime(int index, String action, MimeInfo minfo) {

        MimeInfoImpl impl = (MimeInfoImpl) minfo;

        impl.setAction(0, action);
        save();
    }

    /**
     * Public method to unregister an action for Mime
     */
    public synchronized void unregisterActionForMime(String action, MimeInfo minfo) {

        MimeInfoImpl impl = (MimeInfoImpl) minfo;

        impl.removeAction(action);
        save();
    }

    /**
     * Check if extension is registered in system
     */
    synchronized boolean isExtensionRegistered(String extension) {

        Iterator mimes = registeredMimes.values().iterator();

        while (mimes.hasNext()) {
            MimeInfoImpl minfo = (MimeInfoImpl) mimes.next();

            if (minfo.isRegistered(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Count the registered mimes
     */
    public synchronized int countRegisteredMimes() {
        return registeredMimes.size();
    }

    /**
     * Get MimeInfo by index
     */
    public synchronized MimeInfo getMimeInfo(int index) {

        MimeInfo[] minfos =
            (MimeInfo[]) registeredMimes.values().toArray(new MimeInfo[registeredMimes.size()]);

        return minfos[index];
    }

    /**
     * Public method to register an extension for Mime
     */
    public synchronized void registerExtensionsForMime(String extensions, MimeInfo minfo)
            throws MimeAlreadyRegisteredException {

        MimeInfoImpl impl = (MimeInfoImpl) minfo;
        StringTokenizer st = new StringTokenizer(extensions, " ,");

        while (st.hasMoreTokens()) {
            impl.registerExtension(st.nextToken());
        }

        save();
    }

    /**
     * Public method to unregister an extension for Mime
     */
    public synchronized void unregisterExtensionsForMime(MimeInfo minfo) {

        MimeInfoImpl impl = (MimeInfoImpl) minfo;
        String[] extensions = impl.getExtensions();

        try {
            for (int i = 0; i < extensions.length; i++) {
                impl.unregisterExtension(extensions[i]);
            }
        } catch (Exception e) {}

        save();
    }
}
