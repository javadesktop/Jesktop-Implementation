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

import org.jesktop.AppInstaller;
import org.jesktop.*;
import org.jesktop.JesktopPackagingException;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.JesktopLaunchException;
import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;
import org.xml.sax.SAXException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

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
 * @author Paul Hammant
 * @version $Revision: 1.6 $
 */
public class AppInstallerImpl extends AppBase implements AppInstaller {

    private LaunchableTargetFactory launchableTargetFactory;
    private PropertyChangeSupport propertyChangeSupport;
    private ImageRepository imageRepository;
    private DesktopKernelImpl desktopKernelImpl;


    protected AppInstallerImpl(final PropertyChangeSupport propertyChangeSupport,
                               final DesktopKernelImpl desktopKernelImpl,
                               final LaunchableTargetFactory launchableTargetHolder,
                               final ImageRepository imageRepository,
                               final File baseDir) {
        super(baseDir);
        launchableTargetFactory = launchableTargetHolder;
        this.propertyChangeSupport = propertyChangeSupport;
        this.imageRepository = imageRepository;
        this.desktopKernelImpl = desktopKernelImpl;
    }

    private void notifyLaunchableTargetListeners() {

        propertyChangeSupport.firePropertyChange(DesktopKernel.LAUNCHABLE_TARGET_CHANGE, null,
                                                 launchableTargetFactory
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
     *
     */
    private void applicationInstall(final InputStream[] inputStreams, boolean confirm) {

        //System.err.println("install apps from " +inputStreams.length + " jars" );
        String appFilePrefix = "JesktopApp" + launchableTargetFactory.getNewAppSuffix();
        JarSuffixHolder jarSuffix = new JarSuffixHolder();
        Vector jarNames = new Vector();
        for (int f = 0; f < inputStreams.length; f++) {
            jarNames.add(internJar(false, inputStreams[f], appFilePrefix, jarSuffix));
        }

        try {
            try {
                LaunchableTarget[] pendInsts = new org.jesktop.launchable.LaunchableTarget[0];
                pendInsts = this.installApps(appFilePrefix, jarNames, jarSuffix);
                if (confirm) {
                  desktopKernelImpl.confirmAppInstallation(pendInsts);
                } else {
                    for (int i = 0; i < pendInsts.length; i++) {
                        launchableTargetFactory.confirmLaunchableTarget(pendInsts[i]);
                    }
                }
            } catch (JesktopPackagingException jpe) {
                desktopKernelImpl.showErrorApp(jpe);
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
                Configuration app = null;
                try {
                    app = CONF_BUILDER.build(url.openStream());
                } catch (SAXException e) {
                    throw new JesktopPackagingException(e.getMessage());
                }
                String appType = app.getAttribute("type", "normal");
                String targetName = app.getAttribute("target");
                String className = getAppElemValue(app, "class", true);
                String displayName = getAppElemValue(app, "display-name", true);
                String singleInstance = app.getAttribute("single-instance", "false");
                LaunchableTarget lt = null;

                if (appType.equals("normal")) {
                    lt = launchableTargetFactory.makeNormalLaunchableTarget(targetName, className,
                                                                           appFilePrefix,
                                                                           jarFileNames,
                                                                           displayName,
                                                                           singleInstance
                                                                               .equals("true"));
                } else if (appType.equals("decorator")) {
                    lt = launchableTargetFactory.makeDecoratorLaunchableTarget(targetName,
                                                                              className,
                                                                              appFilePrefix,
                                                                              jarFileNames,
                                                                              displayName,
                                                                              getAppElemValue(app,
                                                                                  "configpath",
                                                                                  false));
                } else if (appType.equals("configlet")) {
                    lt = launchableTargetFactory.makeConfigletLaunchableTarget(targetName,
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

            imageRepository.setImageIcon(ImageRepository.APP + targetName + "_16x16",
                                         new ImageIcon(url));
        } catch (ConfigurationException ce) {
            throw new JesktopPackagingException("Malformed xml segment for icons in jar");
        } catch (NullPointerException npe) {
            throw new JesktopPackagingException("Malformed icon segment in jar =(" + url + ")");
        }    // no small icon

        try {    //todo - method for dupe logic
            url = tempClassLoader.getResource(icons.getAttribute("icon32"));

            imageRepository.setImageIcon(ImageRepository.APP + targetName + "_32x32",
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
