/* ====================================================================
 * Copyright 2000 - 2004, The Jesktop project committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Portions of this code are copyright Apache Software Foundation, and licensed
 * under the Apache Software License 1.1
 */
package net.sourceforge.jesktopimpl.windowmanagers.windoze;

import org.jesktop.LaunchedTarget;
import org.jesktop.DesktopKernel;
import org.jesktop.ImageRepository;
import org.jesktop.DesktopKernel;

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
     * @author Paul Hammant
     * @version $Revision: 1.5 $
     */
    public class LaunchedButton extends JButton {

        protected final LaunchedTarget launchedTarget;
        protected DesktopKernel mDesktopKernel;

        protected LaunchedButton(final LaunchedTarget launchedTarget, final DesktopKernel desktopKernel, ImageRepository imageRepository) {

            super();

            mDesktopKernel = desktopKernel;

            this.setIcon(imageRepository.getAppSmallImageIcon(launchedTarget.getTargetName()));
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
