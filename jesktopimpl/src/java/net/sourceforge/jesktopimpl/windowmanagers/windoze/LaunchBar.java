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
import org.jesktop.AppLauncher;
import org.jesktop.ImageRepository;
import org.jesktop.DesktopKernel;
import org.jesktop.*;

import javax.swing.JPopupMenu;
import java.util.HashSet;

/**
 * Class LaunchBar
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.5 $
 */
public class LaunchBar extends JPopupMenu {

    protected LaunchableTarget[] launchableTargets;
    protected ImageRepository imageRepository;
    protected DesktopKernel desktopKernel;
    protected WindozeWindowManager windozeWindowManager;
    private AppLauncher appLauncher;

    /**
     * Constructor LaunchBar
     */
    protected LaunchBar(final DesktopKernel desktopKernel, final LaunchableTarget[] launchableTargets,
                        final ImageRepository imageRepository, final WindozeWindowManager windozeWindowManager,
                        final AppLauncher appLauncher) {
        this.launchableTargets = launchableTargets;
        this.imageRepository = imageRepository;
        this.desktopKernel = desktopKernel;
        this.windozeWindowManager = windozeWindowManager;
        this.appLauncher = appLauncher;
        setupStartButton();
    }

    private void setupStartButton() {

        int deep = 0;

        // makes an assumption that there are only menus at the root level
        HashSet hs = new HashSet();

        for (int x = 0; x < launchableTargets.length; x++) {
            String[] parts = launchableTargets[x].getTargetNameParts();

            if (deep < parts.length - 1) {

                // still a menu
                if (!hs.contains(parts[deep])) {
                    this.add(new LaunchBarGroupMenuItem(desktopKernel, launchableTargets, windozeWindowManager,
                            imageRepository, appLauncher, parts[deep], parts[deep], deep + 1));
                    hs.add(parts[deep].intern());
                }
            }

            if (deep == parts.length - 1) {

                // leaf
                if (!hs.contains(parts[deep])) {
                    this.add(new LaunchBarMenuItem(desktopKernel, launchableTargets[x],
                            windozeWindowManager, appLauncher,
                            imageRepository
                            .getAppSmallImageIcon(launchableTargets[x].getTargetName())));
                }
            }
        }
    }
}
