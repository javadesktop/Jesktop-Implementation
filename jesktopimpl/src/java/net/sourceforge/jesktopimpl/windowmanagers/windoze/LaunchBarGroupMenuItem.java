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

import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.ImageRepository;
import org.jesktop.DesktopKernel;
import org.jesktop.AppLauncher;
import org.jesktop.AppLauncher;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.BoxLayout;
import java.util.HashSet;
import java.awt.Container;

/**
     * Class LaunchBarGroupMenuItem
     *
     *
     * @author Paul Hammant
     * @version $Revision: 1.5 $
     */
    public class LaunchBarGroupMenuItem extends JMenu {

    protected LaunchableTarget[] mLaunchableTargets;
    protected ImageRepository mImageRepository;
    protected DesktopKernel mDesktopKernel;
    protected WindozeWindowManager mWindozeWindowManager;

        /**
         * Constructor LaunchBarGroupMenuItem
         *
         *
         * @param path
         * @param phrase
         * @param deep
         *
         */
        protected LaunchBarGroupMenuItem(final DesktopKernel desktopKernel, final LaunchableTarget[] launchableTargets,
                                         final WindozeWindowManager windozeWindowManager, final ImageRepository imageRepository,
                                         final AppLauncher appLauncher,
                                         final String path, final String phrase, final int deep) {

            super(phrase);

            mLaunchableTargets = launchableTargets;
            mImageRepository = imageRepository;
            mDesktopKernel = desktopKernel;
            mWindozeWindowManager = windozeWindowManager;

            HashSet hs = new HashSet();

            for (int x = 0; x < mLaunchableTargets.length; x++) {
                if (mLaunchableTargets[x].getTargetName().startsWith(path)) {
                    String[] parts = mLaunchableTargets[x].getTargetNameParts();

                    if (deep < parts.length - 1) {

                        // still a menu
                        if (!hs.contains(parts[deep])) {
                            LaunchBarGroupMenuItem lbgi =
                                new LaunchBarGroupMenuItem(mDesktopKernel, mLaunchableTargets,
                                        mWindozeWindowManager, mImageRepository, appLauncher, path + "/" + parts[deep], parts[deep],
                                                           deep + 1);

                            this.add(lbgi);
                            lbgi.setMenuLocation(10, 10);
                            hs.add(parts[deep].intern());
                        }
                    }

                    if (deep == parts.length - 1) {

                        // leaf
                        if (!hs.contains(parts[deep])) {
                            this.add(new LaunchBarMenuItem(mDesktopKernel, mLaunchableTargets[x],
                                    mWindozeWindowManager, appLauncher,
                                mImageRepository.getAppSmallImageIcon(mLaunchableTargets[x].getTargetName())));
                        }
                    }
                }
            }

            JPopupMenu pm = getPopupMenu();

            pm.setLayout(new BoxLayout(pm, BoxLayout.Y_AXIS));
            setMinimumSize(getPreferredSize());
        }

        /**
         * Method setPopupMenuVisible
         *
         *
         * @param b
         *
         */
        public void setPopupMenuVisible(final boolean b) {

            boolean isVisible = isPopupMenuVisible();

            if (b != isVisible) {
                if ((b == true) && isShowing()) {

                    // Set location of popupMenu (pulldown or pullright)
                    // Perhaps this should be dictated by L&F
                    int x = 0;
                    int y = 0;
                    Container parent = getParent();
                    JPopupMenu pm = getPopupMenu();

                    pm.pack();

                    if (parent instanceof JPopupMenu) {
                        x = (int) (getWidth() * 0.95);
                        y = (int) getHeight();

                        if (pm.getHeight() > y) {
                            y = pm.getHeight();
                        }
                    } else {
                        x = getWidth();
                        y = 0;
                    }

                    // TODO bug here affecting consitent layout
                    //System.err.println("y=" + y);
                    //System.err.println("y1=" + getPreferredSize().getHeight());
                    //System.err.println("y2=" + getMinimumSize().getHeight());
                    //System.err.println("ya=" + pm.getSize().height);
                    //System.err.println("yb=" + pm.getBounds().height);
                    //System.err.println("yc=" + pm.getHeight());
                    pm.show(this, x, -y);

                    // y height of panel is different before show() to after.
                    //System.err.println("yy=" + getWidth());
                } else {
                    getPopupMenu().setVisible(false);
                }
            }
        }
    }

