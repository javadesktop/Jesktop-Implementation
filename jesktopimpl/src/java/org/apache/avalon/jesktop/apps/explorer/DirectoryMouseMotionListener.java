
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.apps.explorer;



import org.jesktop.api.DesktopKernel;

import javax.swing.SwingUtilities;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;


/**
     * Class DirectoryMouseMotionListener
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1.1.1 $
     */
public class DirectoryMouseMotionListener extends MouseMotionAdapter {

    private DesktopKernel mDesktopKernel;
    private DirectoryPanel mDirectoryPanel;

    protected DirectoryMouseMotionListener(DesktopKernel desktopKernel,
                                           DirectoryPanel directoryPanel) {
        mDesktopKernel = desktopKernel;
        mDirectoryPanel = directoryPanel;
    }

    /**
     * Method mouseDragged
     *
     *
     * @param e
     *
     */
    public void mouseDragged(MouseEvent e) {

        Point pt = new Point(e.getPoint());

        SwingUtilities.convertPointToScreen(pt, mDirectoryPanel.getContentsTable());
        mDesktopKernel.itemBeingDragged(mDirectoryPanel.getCurrentlyDraggedItem(), pt);
    }
}
