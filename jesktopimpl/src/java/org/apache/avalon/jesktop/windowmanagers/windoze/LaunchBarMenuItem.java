/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.windowmanagers.windoze;

import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.api.JesktopLaunchException;
import org.jesktop.api.DesktopKernel;

import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.Point;

/**
     * Class LaunchBarMenuItem
     *
     *
     * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
     * @version $Revision: 1.1.1.1 $
     */
    public class LaunchBarMenuItem extends JMenuItem implements ActionListener {

        protected LaunchableTarget mLaunchableTarget;
        protected DesktopKernel mDesktopKernel;
        protected WindozeWindowManager mWindozeWindowManager;

        /**
         * Constructor LaunchBarMenuItem
         *
         *
         * @param launchableTarget
         * @param im
         *
         */
        protected LaunchBarMenuItem(final DesktopKernel desktopKernel, final LaunchableTarget launchableTarget, 
                                    final WindozeWindowManager windozeWindowManager,  final ImageIcon im) {

            super(launchableTarget.getDisplayName(), im);

            mDesktopKernel = desktopKernel;
            mLaunchableTarget = launchableTarget;
            mWindozeWindowManager = windozeWindowManager;

            addActionListener(this);

/*this.addMenuDragMouseListener(new MenuDragMouseListener() {

                 public void menuDragMouseEntered( MenuDragMouseEvent event ) {
                    System.out.println( "mdm entered" );
                    mRenderLaunchableTargetDrag = false;
                 }

                 public void menuDragMouseExited( MenuDragMouseEvent event ) {
                    System.out.println( "mdm exit" );
                    mRenderLaunchableTargetDrag = true;
                 }

                 public void menuDragMouseDragged( MenuDragMouseEvent event ) {
                     System.out.println( "mdm dragged" );
                 }

                 public void menuDragMouseReleased( MenuDragMouseEvent event ) {
                     System.out.println( "mdm released" );
                 }
             });*/
            this.addMouseListener(new MouseAdapter() {

                public void mouseReleased(MouseEvent event) {

                    Point pt = event.getPoint();

                    SwingUtilities.convertPointToScreen(pt, LaunchBarMenuItem.this);
                    mWindozeWindowManager.renderLaunchableTargetDragRepresentation(null, pt, true);
                }
            });
            this.addMouseMotionListener(new MouseMotionListener() {

                public void mouseDragged(MouseEvent event) {

                    Point pt = event.getPoint();

                    SwingUtilities.convertPointToScreen(pt, LaunchBarMenuItem.this);
                    mWindozeWindowManager.renderLaunchableTargetDragRepresentation(launchableTarget, pt, false);
                }

                public void mouseMoved(MouseEvent event) {}
            });
        }
    
    
        /**
         * Method getLaunchableTarget
         *
         *
         * @return
         *
         */
        protected LaunchableTarget getLaunchableTarget() {
            return mLaunchableTarget;
        }

        /**
         * Method actionPerformed
         *
         *
         * @param e
         *
         */
        public void actionPerformed(final ActionEvent e) {

            try {
                mDesktopKernel.getAppLauncher().launchApp(mLaunchableTarget);
            } catch (JesktopLaunchException jle) {
                jle.printStackTrace();
            }
        }
    }
