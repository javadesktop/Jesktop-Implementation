/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.builtinapps.decorators;



import org.jesktop.Decorator;
import org.jesktop.ImageRepository;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.frimble.Frimble;
import org.jesktop.ImageRepository;
import org.jesktop.Decorator;


import javax.swing.UIManager;


/**
 * Class DefaultDecorator does the default decoration for Jesktop.
 * It renders in Metal and uses small icons for windows.
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.3 $
 */
public class DefaultDecorator implements Decorator {

    private ImageRepository imageRepository;
    public DefaultDecorator(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     */

    // Javadocs will automatically import from interface.
    public void end() {}

    /**
     */

    // Javadocs will automatically import from interface.
    public void decorate(Frimble frimble, LaunchableTarget launchableTarget) {

        String targetName = "default";

        if (launchableTarget != null) {

            // for uninstalled apps, for the moment launchableTarget is null.
            targetName = launchableTarget.getTargetName();
        }

        frimble.setFrameIcon(imageRepository.getAppSmallImageIcon(targetName));
        frimble.pack();
    }
}
