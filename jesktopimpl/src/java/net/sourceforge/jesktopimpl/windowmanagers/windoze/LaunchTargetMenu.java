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
import org.jesktop.DesktopKernel;
import org.jesktop.LaunchedTarget;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
     * Class LaunchTargetMenu
     *
     *
     * @author Paul Hammant
     * @version $Revision: 1.5 $
     */
    public class LaunchTargetMenu extends JPopupMenu implements ActionListener {

        protected DesktopKernel desktopKernel;
        protected LaunchedTarget launchedTarget;

        /**
         * Constructor LaunchTargetMenu
         *
         *
         * @param launchedTarget
         *
         */
        protected LaunchTargetMenu(final DesktopKernel desktopKernel, final LaunchedTarget launchedTarget) {

            this.desktopKernel = desktopKernel;
            this.launchedTarget = launchedTarget;

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
                desktopKernel.launchedTargetCloseRequested(launchedTarget);
            }
        }
    }
