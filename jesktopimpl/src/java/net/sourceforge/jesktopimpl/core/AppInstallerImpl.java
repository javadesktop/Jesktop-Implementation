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

import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;
import org.jesktop.AppInstaller;
import org.jesktop.DesktopKernel;
import org.jesktop.ImageRepository;
import org.jesktop.JesktopLaunchException;
import org.jesktop.JesktopPackagingException;
import org.jesktop.launchable.LaunchableTarget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;


/**
 * Class AppInstallerImpl
 *
 * @author Paul Hammant
 * @version $Revision: 1.9 $
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
                               final DocumentBuilderFactory dbf,
                               final File baseDir) {
        super(dbf, baseDir);
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
     * @param url
     * @throws JesktopPackagingException
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
     * @param url
     * @throws JesktopPackagingException
     */
    public void installAppWithoutConfirmation(final URL url) throws JesktopPackagingException {
        try {
            applicationInstall(new InputStream[]{url.openStream()}, false);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Method installApps
     *
     * @param urls
     * @throws JesktopPackagingException
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
     * @param inputStream
     * @throws JesktopPackagingException
     */
    public void installApps(final InputStream inputStream) throws JesktopPackagingException {
        applicationInstall(new InputStream[]{inputStream}, true);
    }

    /**
     * Method installApps
     *
     * @param inputStreams
     * @throws JesktopPackagingException
     */
    public void installApps(final InputStream[] inputStreams) throws JesktopPackagingException {
        applicationInstall(inputStreams, true);
    }

    /**
     * Method installApps
     *
     * @param inputStreams
     * @throws JesktopPackagingException
     */
    protected void installAppsWithoutConfirmation(final InputStream[] inputStreams) throws JesktopPackagingException {
        applicationInstall(inputStreams, false);
    }

    /**
     * Method installApps
     *
     * @param inputStreams
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

    private LaunchableTarget[] installApps(final String appFilePrefix, final Vector jarFileNames, final JarSuffixHolder suffix)
            throws JesktopPackagingException {

        //ClassLoader classLoader = launchableTargetHolder.getClassLoader(appFilePrefix, jarFileNames);
        InputStream appsIs = null;
        String firstJar = null;
        try {
            firstJar = (String) jarFileNames.elementAt(0);
            URLClassLoader tempClassLoader = new URLClassLoader(new URL[]{
                new File(firstJar).toURL()});
            Document appJarApplicationsConf = getApplicationsDotXML(firstJar, tempClassLoader);

            return installApps(appFilePrefix, jarFileNames, suffix, appJarApplicationsConf,
                    tempClassLoader);
        } catch (IOException ioe) {
            throw new JesktopPackagingException("JESKTOP-INF/applications.xml is missing from application jar : " + firstJar);
        }
    }

    private LaunchableTarget[] installApps(final String appFilePrefix, final Vector jarFileNames, final JarSuffixHolder suffix,
                                           final Document appsDocument, final URLClassLoader tempClassLoader) throws JesktopPackagingException {

        Element appsRootElem = appsDocument.getDocumentElement();
        try {
            NodeList addionalJarsGroup = appsDocument.getElementsByTagName("additional-jars");
            Element addionalJarsGroupE = (Element) addionalJarsGroup.item(0);
            if (addionalJarsGroupE != null) {

                NodeList additionalJars = addionalJarsGroupE.getElementsByTagName("jar");

                for (int q = 0; q < additionalJars.getLength(); q++) {
                    Element additionalJar = (Element) additionalJars.item(q);
                    String whereStr = additionalJar.getAttribute("where").toLowerCase();
                    String jarStr = additionalJar.getChildNodes().item(0).getNodeValue();

                    if (whereStr.equals("remote")) {

                        // this is failing see bugs:
                        // http://developer.java.sun.com/developer/bugParade/bugs/4388202.html
                        jarFileNames.add(internJar(false, new URL(jarStr).openStream(),
                                appFilePrefix, suffix));
                    } else if (whereStr.equals("contained")) {
                        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{
                            new File((String) jarFileNames.elementAt(0)).toURL()});
                        URL url = urlClassLoader.getResource(jarStr);
                        if (url == null) {
                            System.out.println("No additional Jar - '" + jarStr + "' in jar");
                        } else {
                            jarFileNames.add(internJar(false, url.openStream(), appFilePrefix, suffix));
                        }
                    }
                }
            }
        } catch (MalformedURLException mfue) {
            mfue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Vector pendingInstalls = new Vector();
        NodeList apps = appsRootElem.getElementsByTagName("appxml");

        for (int q = 0; q < apps.getLength(); q++) {
            try {
                Element appE = (Element) apps.item(q);
                String appSpecificXml = appE.getAttribute("locn");
                URL url = tempClassLoader.getResource(appSpecificXml);
                if (url == null) {
                    throw new JesktopPackagingException("Appltcation's manifest '" + appSpecificXml + "' is missing from jar");
                }
                Document app = null;
                try {
                    app = dbf.newDocumentBuilder().parse(url.openStream());
                } catch (SAXException e) {
                    throw new JesktopPackagingException(e.getMessage());
                } catch (ParserConfigurationException e) {
                    throw new JesktopPackagingException(e.getMessage());
                }
                Element rootElem = app.getDocumentElement();
                String appType = rootElem.getAttribute("type");
                if (appType == null) {
                    appType = "normal";
                }
                String targetName = rootElem.getAttribute("target");
                String className = getSingleElemValue(rootElem, "class", true);
                String displayName = getSingleElemValue(rootElem, "display-name", true);
                String singleInstance = rootElem.getAttribute("single-instance");
                if (singleInstance == null) {
                    singleInstance = "false";
                }

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
                            getSingleElemValue(rootElem,
                                    "configpath",
                                    false));
                } else if (appType.equals("configlet")) {
                    lt = launchableTargetFactory.makeConfigletLaunchableTarget(targetName,
                            className,
                            appFilePrefix,
                            jarFileNames,
                            displayName,
                            getSingleElemValue(rootElem,
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
            } finally {

                //System.out.println("App installed OK");
            }
        }

        LaunchableTarget[] pendingInsts = new LaunchableTarget[pendingInstalls.size()];

        pendingInstalls.copyInto(pendingInsts);

        return pendingInsts;
    }

    private void createIcons(final Document app, final String targetName, final URLClassLoader tempClassLoader)
            throws JesktopPackagingException {

        NodeList icons = app.getElementsByTagName("icons");

        if (icons.getLength() > 1) {
            throw new JesktopPackagingException("Only one 'icons' element ");
        }

        Element iconsE = (Element) icons.item(0);
        if (iconsE != null) {
            URL url = null;

            try {
                String attribute = iconsE.getAttribute("icon16");
                url = tempClassLoader.getResource(attribute);

                imageRepository.setImageIcon(ImageRepository.APP + targetName + "_16x16",
                        new ImageIcon(url));
            } catch (NullPointerException npe) {
                throw new JesktopPackagingException("Malformed icon segment in jar =(" + url + ")");
            }    // no small icon

            try {    //todo - method for dupe logic
                String attribute = iconsE.getAttribute("icon32");
                url = tempClassLoader.getResource(attribute);

                imageRepository.setImageIcon(ImageRepository.APP + targetName + "_32x32",
                        new ImageIcon(url));
            } catch (NullPointerException npe) {
                throw new JesktopPackagingException("Malformed icon segment in jar =(" + url + ")");
            }        // no large icon
        }
    }
}
