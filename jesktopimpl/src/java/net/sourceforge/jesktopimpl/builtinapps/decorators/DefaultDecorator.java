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
 * @version $Revision: 1.4 $
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
