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
package net.sourceforge.jesktopimpl.apps.decorators.test;

import org.jesktop.Decorator;
import org.jesktop.ImageRepository;
import org.jesktop.config.ObjConfigurable;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.frimble.Frimble;
import org.jesktop.ImageRepository;
import org.jesktop.Decorator;

import javax.swing.UIManager;


/**
 * Class TestDecorator allows the choosing of big versus small icons for windows
 * as well as Motif (versus Metal) for LnF.  Motif being always available.
 *
 *
 * @author Paul Hammant Dec 2000.
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
