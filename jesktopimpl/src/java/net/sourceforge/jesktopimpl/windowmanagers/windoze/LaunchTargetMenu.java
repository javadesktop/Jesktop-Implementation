/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.windowmanagers.windoze;

import org.jesktop.api.LaunchedTarget;
import org.jesktop.api.DesktopKernel;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
     * Class LaunchTargetMenu
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1 $
     */
    public class LaunchTargetMenu extends JPopupMenu implements ActionListener {

        protected DesktopKernel mDesktopKernel;    
        protected LaunchedTarget mLaunchedTarget;

        /**
         * Constructor LaunchTargetMenu
         *
         *
         * @param launchedTarget
         *
         */
        protected LaunchTargetMenu(final DesktopKernel desktopKernel, final LaunchedTarget launchedTarget) {

            mDesktopKernel = desktopKernel;
            mLaunchedTarget = launchedTarget;

            JMenuItem menuItem = new JMenuItem("Close");

            this.add(menuItem);
            menuItem.addActionListener(this);
        }

        /**
         * Method actionPerformed
         *
         *
         * @param e
         *
         */
        public void actionPerformed(final ActionEvent e) {

            String str = e.getActionCommand();

            if (str.equals("Close")) {
                mDesktopKernel.launchedTargetCloseRequested(mLaunchedTarget);
            }
        }
    }
