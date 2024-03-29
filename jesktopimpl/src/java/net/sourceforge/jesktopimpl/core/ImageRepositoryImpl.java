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
package net.sourceforge.jesktopimpl.core;

import org.jesktop.ImageRepository;
import org.jesktop.ObjectRepository;

import javax.swing.ImageIcon;
import java.util.HashMap;
import java.net.URLClassLoader;
import java.net.URL;


/**
 * Class ImageRepositoryImpl
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.8 $
 */
public class ImageRepositoryImpl implements ImageRepository {

    private static final String PATH = "net/sourceforge/jesktopimpl/icons/";
    private static final ImageIcon DEFAULT_APP_ICON_32 = makeImage(PATH
                                                                   + "default_app_32x32.gif");
    private static final ImageIcon DEFAULT_APP_ICON_16 = makeImage(PATH
                                                                   + "default_app_16x16.gif");
    private static final ImageIcon DEFAULT_FILE_ICON_32 = makeImage(PATH
                                                                    + "default_file_32x32.gif");
    private static final ImageIcon DEFAULT_FILE_ICON_16 = makeImage(PATH
                                                                    + "default_file_16x16.gif");
    private HashMap images = new HashMap();
    private ObjectRepository objectRepository;

    public ImageRepositoryImpl(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    private static ImageIcon makeImage(final String resource) {

        URL url =
            ((URLClassLoader) ImageRepositoryImpl.class.getClassLoader()).getResource(resource);

        return new ImageIcon(url);
    }

    private void setDefaults() {

        // these will only happen during install.
        // they can be overwritten by apps
        setDefault(MIME + "directory_16x16", PATH + "folder_16x16.gif");
        setDefault(MIME + "folder_16x16", PATH + "folder_16x16.gif");
    }

    private void setDefault(final String name, final String imgPath) {

        if (!objectRepository.containsKey(name)) {
            objectRepository.put(name, makeImage(imgPath));
        }
    }

    /**
     * Method getAppSmallImageIcon
     *
     *
     * @param targetName
     *
     * @return
     *
     */
    public ImageIcon getAppSmallImageIcon(final String targetName) {
        return getAppImageIcon(targetName + "_16x16", DEFAULT_APP_ICON_16);
    }

    /**
     * Method getAppBigImageIcon
     *
     *
     * @param targetName
     *
     * @return
     *
     */
    public ImageIcon getAppBigImageIcon(final String targetName) {
        return getAppImageIcon(targetName + "_32x32", DEFAULT_APP_ICON_32);
    }

    /**
     * Method getAppImageIcon
     *
     *
     * @param targetName
     * @param defaultIcon
     *
     * @return
     *
     */
    public ImageIcon getAppImageIcon(final String targetName, final ImageIcon defaultIcon) {
        return getImageIcon(APP + targetName, defaultIcon);
    }

    /**
     * Method getFileSmallImageIcon
     *
     *
     * @param mimeType
     *
     * @return
     *
     */
    public ImageIcon getFileSmallImageIcon(final String mimeType) {
        return getFileImageIcon(mimeType + "_16x16", DEFAULT_FILE_ICON_16);
    }

    /**
     * Method getFileBigImageIcon
     *
     *
     * @param mimeType
     *
     * @return
     *
     */
    public ImageIcon getFileBigImageIcon(final String mimeType) {
        return getFileImageIcon(mimeType + "_32x32", DEFAULT_FILE_ICON_32);
    }

    /**
     * Method getFileImageIcon
     *
     *
     * @param mimeType
     * @param defaultIcon
     *
     * @return
     *
     */
    public ImageIcon getFileImageIcon(final String mimeType, final ImageIcon defaultIcon) {
        return getImageIcon(MIME + mimeType, defaultIcon);
    }

    private ImageIcon getImageIcon(final String name, final ImageIcon defaultIcon) {

        ImageIcon ii = (ImageIcon) images.get(name);

        if (ii == null) {
            if (objectRepository.containsKey(name)) {
                ii = (ImageIcon) objectRepository.get(name);
            } else {
                ii = defaultIcon;
            }

            images.put(name, ii);
        }

        return ii;
    }

    /**
     * Method setImageIcon
     *
     *
     * @param name
     * @param imageIcon
     *
     */
    public void setImageIcon(final String name, final ImageIcon imageIcon) {
        objectRepository.put(name, imageIcon);
        images.put(name, imageIcon);
    }
}
