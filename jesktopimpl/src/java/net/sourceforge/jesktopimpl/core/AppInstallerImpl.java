/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.core;

import org.jesktop.api.AppInstaller;
import org.jesktop.api.ImageRepository;
import org.jesktop.api.DesktopKernel;
import org.jesktop.api.JesktopPackagingException;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.api.JesktopLaunchException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;
import org.xml.sax.SAXException;

import javax.swing.ImageIcon;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.Vector;


/**
 * Class AppInstallerImpl
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public class AppInstallerImpl extends AppBase implements AppInstaller {

    private LaunchableTargetFactory mLaunchableTargetFactory;
    private PropertyChangeSupport mPropertyChangeSupport;
    private ImageRepository mImageRepository;
    private DesktopKernelImpl mDesktopKernelImpl;


    protected AppInstallerImpl(final PropertyChangeSupport propertyChangeSupport,
                               final DesktopKernelImpl desktopKernelImpl,
                               final LaunchableTargetFactory launchableTargetHolder,
                               final ImageRepository imageRepository,
                               final File baseDir) {
        super(baseDir);
        mLaunchableTargetFactory = launchableTargetHolder;
        mPropertyChangeSupport = propertyChangeSupport;
        mImageRepository = imageRepository;
        mDesktopKernelImpl = desktopKernelImpl;
    }

    private void notifyLaunchableTargetListeners() {

        mPropertyChangeSupport.firePropertyChange(DesktopKernel.LAUNCHABLE_TARGET_CHANGE, null,
                                                 mLaunchableTargetFactory
                                                     .getAllLaunchableTargets());
    }

    /**
     * Method installApps
     *
     *
     * @param url
     *
     * @throws JesktopPackagingException
     *
     */
    public void installApps(final URL url) throws JesktopPackagingException {

        try {
            installApps(url.openStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Method installAppWithoutConfirmation
     *
     *
     * @param url
     *
     * @throws JesktopPackagingException
     *
     */
    public void installAppWithoutConfirmation(final URL url) throws JesktopPackagingException {
        try {
            applicationInstall(new InputStream[] {url.openStream()}, false);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Method installApps
     *
     *
     * @param urls
     *
     * @throws JesktopPackagingException
     *
     */
    public void installApps(final URL[] urls) throws JesktopPackagingException {

        try {
            InputStream[] streams = new InputStream[urls.length];

            for (int f = 0; f < urls.length; f++) {
                streams[f] = urls[f].openStream();
            }

            applicationInstall(streams, true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Method installApps
     *
     *
     * @param inputStream
     *
     * @throws JesktopPackagingException
     *
     */
    public void installApps(final InputStream inputStream) throws JesktopPackagingException {
        applicationInstall(new InputStream[]{ inputStream }, true);
    }
    /**
     * Method installApps
     *
     *
     * @param inputStreams
     *
     * @throws JesktopPackagingException
     *
     */
    public void installApps(final InputStream[] inputStreams) throws JesktopPackagingException {
        applicationInstall(inputStreams, true);
    }
    /**
     * Method installApps
     *
     *
     * @param inputStreams
     *
     * @throws JesktopPackagingException
     *
     */
    protected void installAppsWithoutConfirmation(final InputStream[] inputStreams) throws JesktopPackagingException {
        applicationInstall(inputStreams, false);
    }
    /**
     * Method installApps
     *
     *
     * @param inputStreams
     *
     * @throws JesktopPackagingException
     *
     */
    private void applicationInstall(final InputStream[] inputStreams, boolean confirm) throws JesktopPackagingException {

        //System.err.println("install apps from " +inputStreams.length + " jars" );
        String appFilePrefix = "JesktopApp" + mLaunchableTargetFactory.getNewAppSuffix();
        JarSuffixHolder jarSuffix = new JarSuffixHolder();
        Vector jarNames = new Vector();
        for (int f = 0; f < inputStreams.length; f++) {
            jarNames.add(internJar(false, inputStreams[f], appFilePrefix, jarSuffix));
        }

        try {
            try {
                LaunchableTarget[] pendInsts = this.installApps(appFilePrefix, jarNames, jarSuffix);
                if (confirm) {
                  mDesktopKernelImpl.confirmAppInstallation(pendInsts);
                } else {
                    for (int i = 0; i < pendInsts.length; i++) {
                        mLaunchableTargetFactory.confirmLaunchableTarget(pendInsts[i]);
                    }
                }
            } catch (JesktopPackagingException jpe) {
                mDesktopKernelImpl.showErrorApp(jpe);
            }
        } catch (JesktopLaunchException jle) {
            System.err.println("JesktopLaunchException during confirm of app install");
            jle.printStackTrace();
        }

        notifyLaunchableTargetListeners();
    }

    private LaunchableTarget[] installApps(
            final String appFilePrefix, final Vector jarFileNames, final JarSuffixHolder suffix)
                throws JesktopPackagingException {

        //ClassLoader classLoader = launchableTargetHolder.getClassLoader(appFilePrefix, jarFileNames);
        InputStream appsIs = null;
        String firstJar = null;
        try {
            firstJar = (String) jarFileNames.elementAt(0);
            URLClassLoader tempClassLoader = new URLClassLoader(new URL[]{
                new File(firstJar).toURL() });
            Configuration appJarApplicationsConf = getApplicationsDotXML(firstJar, tempClassLoader);

            return installApps(appFilePrefix, jarFileNames, suffix, appJarApplicationsConf,
                               tempClassLoader);
        } catch (IOException ioe) {
            throw new JesktopPackagingException(
                "JESKTOP-INF/applications.xml is missing from application jar : " + firstJar);
        }
    }

    private LaunchableTarget[] installApps(
            final String appFilePrefix, final Vector jarFileNames, final JarSuffixHolder suffix,
            final Configuration appsConfig, final URLClassLoader tempClassLoader) throws JesktopPackagingException {

        try {
            Configuration addionalJarsGroup = appsConfig.getChild("additional-jars");
            Configuration[] additionalJars = addionalJarsGroup.getChildren("jar");

            for (int q = 0; q < additionalJars.length; q++) {
                String whereStr = additionalJars[q].getAttribute("where").toLowerCase();
                String jarStr = additionalJars[q].getValue();

                if (whereStr.equals("remote")) {

                    // this is failing see bugs:
                    // http://developer.java.sun.com/developer/bugParade/bugs/4388202.html
                    jarFileNames.add(internJar(false, new URL(jarStr).openStream(),
                                               appFilePrefix, suffix));
                } else if (whereStr.equals("contained")) {
                    URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{
                        new File((String) jarFileNames.elementAt(0)).toURL() });
                    URL url = urlClassLoader.getResource(jarStr);

                    jarFileNames.add(internJar(false, url.openStream(), appFilePrefix, suffix));
                }
            }
        } catch (ConfigurationException ce) {
            ce.printStackTrace();

            // OK, so there were no additional jars
        } catch (MalformedURLException mfue) {
            mfue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Vector pendingInstalls = new Vector();
        Configuration[] apps = appsConfig.getChildren("appxml");

        for (int q = 0; q < apps.length; q++) {
            try {
                String appSpecificXml = apps[q].getAttribute("locn");
                URL url = tempClassLoader.getResource(appSpecificXml);
                Configuration app = CONF_BUILDER.build(url.openStream());
                String appType = app.getAttribute("type", "normal");
                String targetName = app.getAttribute("target");
                String className = getAppElemValue(app, "class", true);
                String displayName = getAppElemValue(app, "display-name", true);
                String singleInstance = app.getAttribute("single-instance", "false");
                LaunchableTarget lt = null;

                if (appType.equals("normal")) {
                    lt = mLaunchableTargetFactory.makeNormalLaunchableTarget(targetName, className,
                                                                           appFilePrefix,
                                                                           jarFileNames,
                                                                           displayName,
                                                                           singleInstance
                                                                               .equals("true"));
                } else if (appType.equals("decorator")) {
                    lt = mLaunchableTargetFactory.makeDecoratorLaunchableTarget(targetName,
                                                                              className,
                                                                              appFilePrefix,
                                                                              jarFileNames,
                                                                              displayName,
                                                                              getAppElemValue(app,
                                                                                  "configpath",
                                                                                  false));
                } else if (appType.equals("configlet")) {
                    lt = mLaunchableTargetFactory.makeConfigletLaunchableTarget(targetName,
                                                                              className,
                                                                              appFilePrefix,
                                                                              jarFileNames,
                                                                              displayName,
                                                                              getAppElemValue(app,
                                                                                  "configpath",
                                                                                  true));
                } else {
                    throw new JesktopPackagingException("Application type " + appType
                                                        + " not supported");
                }

                pendingInstalls.addElement(lt);
                createIcons(app, targetName, tempClassLoader);
            } catch (SAXException se) {
                se.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (ConfigurationException ce) {
                ce.printStackTrace();
            } finally {

                //System.out.println("App installed OK");
            }
        }

        LaunchableTarget[] pendingInsts = new LaunchableTarget[pendingInstalls.size()];

        pendingInstalls.copyInto(pendingInsts);

        return pendingInsts;
    }

    private void createIcons(final Configuration app, final String targetName, final URLClassLoader tempClassLoader)
            throws JesktopPackagingException {

        Configuration icons = app.getChild("icons");

        if (icons.getAttributeNames().length == 0) {
            return;
        }

        URL url = null;

        try {
            url = tempClassLoader.getResource(icons.getAttribute("icon16"));

            mImageRepository.setImageIcon(ImageRepository.APP + targetName + "_16x16",
                                         new ImageIcon(url));
        } catch (ConfigurationException ce) {
            throw new JesktopPackagingException("Malformed xml segment for icons in jar");
        } catch (NullPointerException npe) {
            throw new JesktopPackagingException("Malformed icon segment in jar =(" + url + ")");
        }    // no small icon

        try {    //todo - method for dupe logic
            url = tempClassLoader.getResource(icons.getAttribute("icon32"));

            mImageRepository.setImageIcon(ImageRepository.APP + targetName + "_32x32",
                                         new ImageIcon(url));
        } catch (ConfigurationException ce) {
            throw new JesktopPackagingException("Malformed xml segment for icons in jar");
        } catch (NullPointerException npe) {
            throw new JesktopPackagingException("Malformed icon segment in jar =(" + url + ")");
        }        // no large icon
    }

    private String getAppElemValue(final Configuration app, final String elem, final boolean mandatory)
            throws JesktopPackagingException {

        try {
            return app.getChild(elem).getValue();
        } catch (ConfigurationException ce) {
            if (mandatory) {
                throw new JesktopPackagingException("Manadatory element - " + elem
                                                    + " - missing from app config file");
            } else {
                return null;
            }
        }
    }
}
