
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.apps.explorer;



import org.jesktop.JesktopPackagingException;
import org.jesktop.JesktopLaunchException;
import org.jesktop.DesktopKernel;
import org.jesktop.AppLauncher;
import org.jesktop.appsupport.DraggedItem;

import javax.swing.JComponent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.IOException;


/**
     * Class DirectoryMouseListener
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.3 $
     */
public class DirectoryMouseListener extends MouseAdapter {

    private DirectoryTableModel mDirectoryTableModel;
    private final DesktopKernel mDesktopKernel;
    private DirectoryPanel mDirectoryPanel;
    private AppLauncher appLauncher;

    protected DirectoryMouseListener(final DesktopKernel desktopKernel, final DirectoryPanel directoryPanel, AppLauncher appLauncher) {
        mDesktopKernel = desktopKernel;
        mDirectoryPanel = directoryPanel;
        this.appLauncher = appLauncher;
    }

    protected void setDirectoryTableModel(final DirectoryTableModel directoryTableModel) {
        mDirectoryTableModel = directoryTableModel;
    }

    /**
     * Method mouseClicked
     *
     *
     * @param e
     *
     */
    public void mouseClicked(final MouseEvent e) {

        try {
            int r = mDirectoryPanel.getContentsTable().rowAtPoint(e.getPoint());
            File file = mDirectoryTableModel.getFileAtRow(r);

            if (e.getClickCount() == 1) {
                if (!file.isDirectory()) {
                    JComponent prvComp = mDirectoryPanel.getItemPreview().getPreviewPanel();

                    mDesktopKernel.viewDocument(file.toURL(), prvComp, true);
                }
            }

            if (e.getClickCount() == 2) {
                if (file.isDirectory()) {
                    mDirectoryPanel.setDirectory(file);
                } else {
                    if (file.getCanonicalPath().toLowerCase().endsWith(".jar")) {
                        try {
                            appLauncher.launchAppWithoutInstallation(file.toURL());
                        } catch (JesktopPackagingException baxmle) {
                            System.err.println("Jar not executable, reason "
                                               + baxmle.getMessage());
                        }
                    } else {
                        mDesktopKernel.viewDocument(file.toURL());
                    }
                }
            }
        } catch (JesktopLaunchException jle) {
            jle.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();

            //} catch (MalformedURLException mue) {
            //mue.printStackTrace();
        }
    }

    /**
     * Method mousePressed
     *
     *
     * @param e
     *
     */
    public void mousePressed(final MouseEvent e) {

        int r = mDirectoryPanel.getContentsTable().rowAtPoint(e.getPoint());
        File file = mDirectoryTableModel.getFileAtRow(r);

        mDirectoryPanel.setCurrentlyDraggedItem(new DraggedItem(new DragLabel(file.getName()),
                                                                file));
    }

    /**
     * Method mouseReleased
     *
     *
     * @param e
     *
     */
    public void mouseReleased(final MouseEvent e) {
        mDesktopKernel.itemBeingDragged(null, null);
    }
}
