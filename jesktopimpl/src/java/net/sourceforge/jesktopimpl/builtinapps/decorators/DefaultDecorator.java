/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.builtinapps.decorators;



import org.jesktop.api.Decorator;
import org.jesktop.api.DesktopKernel;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.frimble.Frimble;


import javax.swing.UIManager;


/**
 * Class DefaultDecorator does the default decoration for Jesktop.
 * It renders in Metal and uses small icons for windows.
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.1 $
 */
public class DefaultDecorator implements Decorator {

    private DesktopKernel desktopKernel;

    // Javadocs will automatically import from interface.

    /**
     * Method setDesktopKernel
     *
     *
     * @param mDesktopKernel
     *
     */
    public void setDesktopKernel(DesktopKernel desktopKernel) {
        this.desktopKernel = desktopKernel;
    }

    /**
     */

    // Javadocs will automatically import from interface.
    public void end() {}

    /**
     */

    // Javadocs will automatically import from interface.
    public void initDecoratation(Frimble frimble, LaunchableTarget launchableTarget) {

        String targetName = "default";

        if (launchableTarget != null) {

            // for uninstalled apps, for the moment launchableTarget is null.
            targetName = launchableTarget.getTargetName();
        }

        frimble.setFrameIcon(desktopKernel.getImageRepository().getAppSmallImageIcon(targetName));
        frimble.pack();
    }
}
