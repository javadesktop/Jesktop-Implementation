/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.windowmanagers.windoze;

import org.jesktop.api.LaunchedTarget;
import org.jesktop.api.DesktopKernel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Dimension;

/**
     * Class LaunchedButton
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1.1.1 $
     */
    public class LaunchedButton extends JButton {

        protected final LaunchedTarget launchedTarget;
        protected DesktopKernel mDesktopKernel;

        protected LaunchedButton(final LaunchedTarget launchedTarget, final DesktopKernel desktopKernel) {

            super();
            
            mDesktopKernel = desktopKernel;

            this.setIcon(mDesktopKernel.getImageRepository()
                .getAppSmallImageIcon(launchedTarget.getTargetName()));
            this.setText(launchedTarget.getTargetName());

            this.launchedTarget = launchedTarget;

            this.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    mDesktopKernel.launchedTargetSelected(getLaunchedTarget());
                }
            });
            this.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    maybeShowPopup(e);
                }

                public void mouseReleased(MouseEvent e) {
                    maybeShowPopup(e);
                }

                protected void maybeShowPopup(MouseEvent e) {

                    if (e.isPopupTrigger()) {
                        LaunchTargetMenu ltm = new LaunchTargetMenu(mDesktopKernel, getLaunchedTarget());

                        ltm.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }

        /**
         * Method getMinimumSize
         *
         *
         * @return
         *
         */
        public Dimension getMinimumSize() {
            return new Dimension(5, 10);
        }

        private LaunchedTarget getLaunchedTarget() {
            return launchedTarget;
        }

        protected boolean isLaunchedTarget(final LaunchedTarget launchedTarget) {
            return (this.launchedTarget == launchedTarget);
        }
    }
