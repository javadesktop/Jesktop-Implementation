/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */

package net.sourceforge.jesktopimpl.services;

import org.jesktop.frimble.Frimble;
import org.jesktop.appsupport.DraggedItem;
import org.jesktop.config.PersistableConfig;
import org.jesktop.api.DesktopKernel;
import org.jesktop.api.LaunchedTarget;
import org.w3c.dom.Document;

import java.awt.Point;
import java.beans.PropertyChangeListener;


/**
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version 1.0
 */
public interface WindowManager extends PropertyChangeListener {

    /**
     * Method close
     *
     *
     */
    void close();

    /**
     * Desktop is occasionally going to ask the kernel to launch an
     * app.
     *
     * @param dk
     */
    void setKernelCallback(DesktopKernel dk);

    /**
     * Each implemntation of WindowManager may have a different concept of
     * containment for apps. Some will use JFrames, some may use JInternalFrames
     * Others may be custom displays.....
     * e.g. Imagine a JTabbedPane with each tab being an app, right click on the
     * itslef for close/minimise actions...... nahh!
     *
     * @param title
     *
     * @return
     */
    Frimble createFrimble(String title);

    /**
     * Method renderDragRepresentation
     *
     *
     * @param draggedItem
     * @param pt
     *
     */
    void renderDragRepresentation(DraggedItem draggedItem, Point pt);

    /**
     * Method addLaunchedTarget
     *
     *
     * @param launchedTarget
     *
     */
    void addLaunchedTarget(LaunchedTarget launchedTarget);

    /**
     * Method removeLaunchedTarget
     *
     *
     * @param launchedTarget
     *
     */
    void removeLaunchedTarget(LaunchedTarget launchedTarget);

    /**
     * Method updateComponentTreeUI
     *
     *
     */
    void updateComponentTreeUI();

    /**
     * Method setPersistableConfig
     *
     *
     * @param persistableConfig
     *
     */
    void setPersistableConfig(PersistableConfig persistableConfig);

    /**
     * Method setConfig
     *
     *
     * @param configPath
     * @param config
     *
     */
    void setConfig(String configPath, Document config);

    /**
     * Method initializeView
     *
     *
     */
    void initializeView();
}
