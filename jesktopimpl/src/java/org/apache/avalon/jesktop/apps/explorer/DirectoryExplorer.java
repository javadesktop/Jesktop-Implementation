
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.apps.explorer;



import org.jesktop.api.DesktopKernelAware;
import org.jesktop.api.DesktopKernel;
import org.jesktop.frimble.FrimbleAware;
import org.jesktop.frimble.Frimble;
import org.jesktop.appsupport.DropAware;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * Class DirectoryExplorer
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a> Dec 2000.
 * @version V1.0
 */
public class DirectoryExplorer extends JPanel
        implements DesktopKernelAware, FrimbleAware, DropAware {

    private JSplitPane mSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private DirectoryPanel mDirectoryPanel = new DirectoryPanel();
    private DirectoryTree mDirectoryTree = new DirectoryTree();
    private JToolBar mToolBar = new JToolBar(JToolBar.HORIZONTAL);
    private JButton mParentBtn = new JButton("Up Dir");

    /**
     * Constructor DirectoryExplorer
     *
     *
     */
    public DirectoryExplorer() {

        this.setLayout(new BorderLayout());
        this.add(mToolBar, BorderLayout.NORTH);
        this.add(mSplitPane, BorderLayout.CENTER);
        mSplitPane.add(mDirectoryPanel, JSplitPane.RIGHT);
        mSplitPane.add(mDirectoryTree, JSplitPane.LEFT);
        mDirectoryTree.setDirectoryPanel(mDirectoryPanel);
        mToolBar.add(mParentBtn);
        mToolBar.add(new JLabel(""));
        mParentBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                if (ae.getSource() == mParentBtn) {
                    mDirectoryPanel.setParentDirectory();
                }
            }
        });
    }

    // Javadocs will automatically import from interface.

    /**
     * Method setDesktopKernel
     *
     *
     * @param desktopKernel
     *
     */
    public void setDesktopKernel(DesktopKernel desktopKernel) {
        mDirectoryPanel.setDesktopKernel(desktopKernel);
    }

    // Javadocs will automatically import from interface.

    /**
     * Method setFrimble
     *
     *
     * @param frimble
     *
     */
    public void setFrimble(Frimble frimble) {
        mDirectoryPanel.setFrimble(frimble);
    }

    // Javadocs will automatically import from interface.

    /**
     * Method doYouRecognizeDraggedItem
     *
     *
     * @param pt
     * @param cl
     *
     * @return
     *
     */
    public boolean doYouRecognizeDraggedItem(final Point pt, Class cl) {
        return mDirectoryPanel.doYouRecognizeDraggedItem(pt, cl);
    }

    /**
     * Method droppedOnYou
     *
     *
     * @param obj
     *
     */
    public void droppedOnYou(Object obj) {

        // nothing for time being.
    }

    /**
     * Method anotherHasRecognizedDraggedItem
     *
     *
     * @param cl
     *
     */
    public void anotherHasRecognizedDraggedItem(Class cl) {
        mDirectoryPanel.anotherHasRecognizedDraggedItem(cl);
    }

    /**
     * Method draggingStopped
     *
     *
     */
    public void draggingStopped() {
        mDirectoryPanel.draggingStopped();
    }
}
