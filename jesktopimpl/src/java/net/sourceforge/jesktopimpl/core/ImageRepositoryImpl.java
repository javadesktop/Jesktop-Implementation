/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.core;

import org.jesktop.api.ImageRepository;
import org.apache.avalon.cornerstone.services.store.ObjectRepository;
import org.apache.avalon.cornerstone.services.store.Store;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.Block;

import javax.swing.ImageIcon;
import java.util.HashMap;
import java.net.URLClassLoader;
import java.net.URL;


/**
 * Class ImageRepositoryImpl
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public class ImageRepositoryImpl extends AbstractLogEnabled implements Block, ImageRepository, Contextualizable,
        Composable, Configurable, Initializable  {

    private static final String PATH = "org/apache/avalon/jesktop/icons/";
    private static final ImageIcon DEFAULT_APP_ICON_32 = makeImage(PATH
                                                                   + "default_app_32x32.gif");
    private static final ImageIcon DEFAULT_APP_ICON_16 = makeImage(PATH
                                                                   + "default_app_16x16.gif");
    private static final ImageIcon DEFAULT_FILE_ICON_32 = makeImage(PATH
                                                                    + "default_file_32x32.gif");
    private static final ImageIcon DEFAULT_FILE_ICON_16 = makeImage(PATH
                                                                    + "default_file_16x16.gif");
    private HashMap mImages = new HashMap();
    private ObjectRepository mObjectRepository;
    private Store mStore;
    private Configuration mRepository;

    private static ImageIcon makeImage(final String resource) {

        URL url =
            ((URLClassLoader) ImageRepositoryImpl.class.getClassLoader()).getResource(resource);

        return new ImageIcon(url);
    }

    public void contextualize(Context context)
            throws ContextException {
    }

    public void compose(ComponentManager componentManager)
            throws ComponentException {
        mStore = (Store) componentManager.lookup(Store.class.getName());
    }

    public void configure(Configuration configuration)
            throws ConfigurationException {
        mRepository = configuration.getChild("repository");

    }

    public void initialize()
            throws Exception {
        mObjectRepository = (ObjectRepository) mStore.select(mRepository);
    }

    public ImageRepositoryImpl() {
    }

    private void setDefaults() {

        // these will only happen during install.
        // they can be overwritten by apps
        setDefault(MIME + "directory_16x16", PATH + "folder_16x16.gif");
        setDefault(MIME + "folder_16x16", PATH + "folder_16x16.gif");
    }

    private void setDefault(final String name, final String imgPath) {

        if (!mObjectRepository.containsKey(name)) {
            mObjectRepository.put(name, makeImage(imgPath));
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

        ImageIcon ii = (ImageIcon) mImages.get(name);

        if (ii == null) {
            if (mObjectRepository.containsKey(name)) {
                ii = (ImageIcon) mObjectRepository.get(name);
            } else {
                ii = defaultIcon;
            }

            mImages.put(name, ii);
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
        mObjectRepository.put(name, imageIcon);
        mImages.put(name, imageIcon);
    }
}
