/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.apps.decorators.test;

import org.jesktop.api.Decorator;
import org.jesktop.api.ImageRepository;
import org.jesktop.config.ObjConfigurable;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.frimble.Frimble;

import javax.swing.UIManager;


/**
 * Class TestDecorator allows the choosing of big versus small icons for windows
 * as well as Motif (versus Metal) for LnF.  Motif being always available.
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a> Dec 2000.
 * @version V1.0
 */
public class TestDecorator implements Decorator, ObjConfigurable {

    //private Config config; - this does not deserialize properly.
    private Config config;
    private ImageRepository imageRepository;

    public TestDecorator(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * Method setConfig
     *
     *
     * @param obj
     *
     */
    public void setConfig(Object obj) {

        config = (Config) obj;

        if (config == null) {
            this.config = new Config();
            config.bigIcons = false;
            config.motifLnF = false;
        }

        if (config.motifLnF) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            } catch (Exception e) {}
        } else {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception e) {}
        }
    }

    /**
     * Method end
     *
     *
     */
    public void end() {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {}
    }

    /**
     * Refer Decrorator java docs.
     */
    public void decorate(Frimble frimble, LaunchableTarget launchableTarget) {

        String targetName = "default";

        if (launchableTarget != null) {

            // for uninstalled apps, for the moment launchableTarget is null.
            targetName = launchableTarget.getTargetName();
        }

        if (config.bigIcons) {
            frimble.setFrameIcon(imageRepository.getAppBigImageIcon(launchableTarget.getTargetName()));
        } else {
            frimble.setFrameIcon(imageRepository.getAppSmallImageIcon(launchableTarget.getTargetName()));
        }

        frimble.pack();
    }
}
