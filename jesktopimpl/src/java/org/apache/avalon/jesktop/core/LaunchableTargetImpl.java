/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.core;

import org.jesktop.launchable.LaunchableTarget;

import java.io.Serializable;
import java.util.Vector;
import java.util.StringTokenizer;


/**
 * Class LaunchableTarget
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.1 $
 */
public abstract class LaunchableTargetImpl implements LaunchableTarget, Comparable, Serializable {

    private String targetName;
    private String className;
    private String displayName;
    private String appFilePrefix;
    private Vector jarFileNames;
    private String[] targetNameArray;
    private boolean singleInstance;

    /**
     * Constructor LaunchableTarget
     *
     *
     * @param name
     * @param className
     *
     */
    protected LaunchableTargetImpl(final String targetName, final String className, final String displayName,
                                   final boolean singleInstance) {
        this(targetName, className, null, null, displayName, singleInstance);
    }

    protected LaunchableTargetImpl(final String targetName, final String className,final  String appFilePrefix,
                                   final Vector jarFileNames, final String displayName,
                                   final boolean singleInstance) {

        this.targetName = targetName;
        this.className = className;
        this.appFilePrefix = appFilePrefix;
        this.jarFileNames = jarFileNames;
        this.displayName = displayName;
        this.singleInstance = singleInstance;

        StringTokenizer st = new StringTokenizer(targetName, "/");
        Vector parts = new Vector();

        while (st.hasMoreTokens()) {
            String part = st.nextToken();

            parts.add(part);
        }

        targetNameArray = new String[parts.size()];

        parts.copyInto(targetNameArray);
    }

    /**
     * Method getTargetName
     *
     *
     * @return
     *
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Method getDisplayName
     *
     *
     * @return
     *
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Method toString
     *
     *
     * @return
     *
     */
    public String toString() {
        return displayName;
    }

    /**
     * Method getTargetNameParts
     *
     *
     * @return
     *
     */
    public String[] getTargetNameParts() {
        return targetNameArray;
    }

    /**
     * Method getClassName
     *
     *
     * @return
     *
     */
    public String getClassName() {
        return className;
    }

    protected String getAppFilePrefix() {
        return appFilePrefix;
    }

    protected Vector getAppJarNames() {
        return jarFileNames;
    }

    /**
     * Method isSingleInstanceApp
     *
     *
     * @return
     *
     */
    public boolean isSingleInstanceApp() {
        return singleInstance;
    }

    /**
     * Method canBeUnInstalled
     *
     *
     * @return
     *
     */
    public boolean canBeUnInstalled() {

        //TODO - over simplifies for the moment.
        return (appFilePrefix != null);
    }

    /**
     * Method compareTo
     *
     *
     * @param o
     *
     * @return
     *
     */
    public int compareTo(Object o) {

        LaunchableTarget other = (LaunchableTarget) o;

        return targetName.compareTo(other.getTargetName());
    }
}
