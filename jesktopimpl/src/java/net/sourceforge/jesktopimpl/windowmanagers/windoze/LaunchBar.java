/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
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
 * @version $Revision: 1.4 $
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
