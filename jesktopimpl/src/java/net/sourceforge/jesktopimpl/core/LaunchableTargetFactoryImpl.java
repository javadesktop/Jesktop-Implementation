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
import org.jesktop.ObjectRepository;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.launchable.DecoratorLaunchableTarget;
import org.jesktop.launchable.ConfigletLaunchableTarget;
import org.jesktop.launchable.NormalLaunchableTarget;

import javax.swing.JComponent;
import java.util.Vector;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.TreeSet;
import java.util.Collections;
import java.util.SortedSet;
import java.util.Iterator;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

public class LaunchableTargetFactoryImpl
        implements LaunchableTargetFactory  {

    private ObjectRepository objectRepository;
    private HashMap classloaders;
    private LaunchableTargetHolder launchableTargetHolder;

    public LaunchableTargetFactoryImpl(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;

        boolean alreadySetup = objectRepository.containsKey(KEY);
        boolean appsDone = false;

        if (alreadySetup) {
            try {
                launchableTargetHolder = (LaunchableTargetHolder) objectRepository.get(KEY, LaunchableTargetHolder.class.getClassLoader());

                appsDone = true;
            } catch (Exception e) {

                //if (e instanceof InvalidClassException) {
                if (e instanceof RuntimeException) {

                    // different serializtion ID
                    e.printStackTrace();
                    System.err.println("********************************");
                    System.err.println("**  Problem, the last time    **");
                    System.err.println("**  you ran Jesktop           **");
                    System.err.println("**  the installed apps used   **");
                    System.err.println("**  a now incompatible class  **");
                    System.err.println("**  Default apps installed    **");
                    System.err.println("**  only, sorry!              **");
                    System.err.println("********************************");
                } else {
                    e.printStackTrace();
                    System.err.println("********************************");
                    e.printStackTrace();
                    System.err.println("*** Default Apps installed   ***");
                    System.err.println("********************************");
                }
            }
        }

        if (!appsDone) {
            launchableTargetHolder = new LaunchableTargetHolder();
            setBuiltInApps();
            save();
        }
    }

    public LaunchableTarget[] getAllLaunchableTargets() {
        Object[] objs = subset(LaunchableTargetImpl.class);
        LaunchableTarget[] lts = new LaunchableTarget[objs.length];
        System.arraycopy(objs, 0, lts, 0, objs.length);
        return lts;
    }

    public DecoratorLaunchableTarget makeDecoratorLaunchableTarget(final String targetName, final String className,
                                                             final String displayName,
                                                             final String configPath) {

        LaunchableTarget lt = new DecoratorLaunchableTargetImpl(targetName, className,
                                                                displayName, configPath);

        launchableTargetHolder.targets.put(targetName, lt);
        save();
        return (DecoratorLaunchableTarget) lt;
    }

    public DecoratorLaunchableTarget makeDecoratorLaunchableTarget(
            String targetName, String className,
            String appFilePrefix,
            Vector jarFileNames,
            String displayName,
            String configPath) {
        LaunchableTarget lt = new DecoratorLaunchableTargetImpl(targetName, className,
                                                                appFilePrefix, jarFileNames,
                                                                displayName, configPath);

        launchableTargetHolder.targets.put(targetName, lt);
        save();
        return (DecoratorLaunchableTarget) lt;
    }

    public LaunchableTarget getLaunchableTarget(String targetName) {
        return (LaunchableTarget) launchableTargetHolder.targets.get(targetName);
    }

    public boolean isRegistered(String targetName) {
        return (launchableTargetHolder.targets.get(targetName) != null);
    }

    public ConfigletLaunchableTarget[] getConfigletLaunchableTargets() {
        Object[] objs = subset(ConfigletLaunchableTargetImpl.class);
        ConfigletLaunchableTarget[] clts = new ConfigletLaunchableTarget[objs.length];

        System.arraycopy(objs, 0, clts, 0, objs.length);

        return clts;
    }

    public DecoratorLaunchableTarget[] getDecoratorLaunchableTargets() {
        Object[] objs = subset(DecoratorLaunchableTargetImpl.class);
        DecoratorLaunchableTarget[] dlts = new DecoratorLaunchableTarget[objs.length];

        System.arraycopy(objs, 0, dlts, 0, objs.length);

        return dlts;
    }

    public NormalLaunchableTarget[] getNormalLaunchableTargets() {
        LaunchableTarget[] objs = subset(NormalLaunchableTargetImpl.class);
        NormalLaunchableTarget[] nlts = new NormalLaunchableTarget[objs.length];

        System.arraycopy(objs, 0, nlts, 0, objs.length);

        //for (int f = 0 ; f < objs.length ; f++ ) {
        //    nlts[f] = (NormalLaunchableTarget) objs[f];
        //}
        return nlts;
    }

    public void removeLaunchableTarget(String targetName) {
    }

    public void confirmLaunchableTarget(LaunchableTarget launchableTarget) {
        launchableTargetHolder.targets.put(launchableTarget.getTargetName(), launchableTarget);
        save();
    }

    public ClassLoader getClassLoader(LaunchableTarget launchableTarget) {

        LaunchableTargetImpl lti = (LaunchableTargetImpl) launchableTarget;

        return getClassLoader(lti.getAppFilePrefix(), lti.getAppJarNames());
    }

    // get or make it.
    protected synchronized ClassLoader getClassLoader(final String appFilePrefix, final Vector jarNames) {

        if (appFilePrefix == null) {
            return this.getClass().getClassLoader();
        }

        if (classloaders == null) {
            classloaders = new HashMap();
        }

        ClassLoader cl = (ClassLoader) classloaders.get(appFilePrefix);

        if (cl == null) {
            try {
                URL[] urls = new URL[jarNames.size()];
                int x = 0;

                for (Enumeration e = jarNames.elements(); e.hasMoreElements(); ) {
                    String jarName = (String) e.nextElement();

                    urls[x++] = new File(jarName).toURL();
                }

                cl = new JesktopURLClassLoader(urls);
            } catch (MalformedURLException mfue) {
                mfue.printStackTrace();
            }

            classloaders.put(appFilePrefix, cl);
        }

        return cl;
    }



    public String getNewAppSuffix() {
        String tmp = "0000" + launchableTargetHolder.appSuffix++;

        return tmp.substring(tmp.length() - 5, tmp.length());
    }

    public LaunchableTarget getLaunchableTarget(JComponent launched) {
        ClassLoader cl = launched.getClass().getClassLoader();
        String className = launched.getClass().getName();
        LaunchableTarget[] lts = getAllLaunchableTargets();

        //System.out.println("launched - " + launched.getClass().getName());


        for (int f = 0; f < lts.length; f++) {
            //System.out.println(" lts" + f + " - " + lts[f].getClassName());
            ClassLoader ltsCl = getClassLoader(lts[f]);

            if (ltsCl == null) {
                ltsCl = this.getClass().getClassLoader();
            }

            if ((ltsCl == cl) && lts[f].getClassName().equals(className)) {
                //System.out.println("*Matched");
                return lts[f];
            }
        }
        //System.out.println("*UnMatched");
        return null;    // not a launchable target

    }

    public void makeNormalLaunchableTarget(String targetName,
                                           String className,
                                           String displayName,
                                           boolean singleInstance) {
        LaunchableTarget lt = new NormalLaunchableTargetImpl(targetName, className, displayName,
                                                             singleInstance);
        launchableTargetHolder.targets.put(lt.getTargetName(), lt);
        save();
    }

    public void makeKernelLaunchableTarget(String targetName,
                                           String className,
                                           String displayName,
                                           boolean singleInstance) {
        LaunchableTarget lt = new KernelLaunchableTargetImpl(targetName, className, displayName,
                                                             singleInstance);
        launchableTargetHolder.targets.put(lt.getTargetName(), lt);
        save();
    }

    public LaunchableTarget makeNormalLaunchableTarget(String targetName,
                                                       String className,
                                                       String appFilePrefix,
                                                       Vector jarFileNames,
                                                       String displayName,
                                                       boolean singleInstance) {
        LaunchableTarget lt = new NormalLaunchableTargetImpl(targetName, className,
                                                             appFilePrefix, jarFileNames,
                                                             displayName, singleInstance);
        launchableTargetHolder.targets.put(targetName, lt);
        save();
        return lt;
    }

    public LaunchableTarget makeConfigletLaunchableTarget(final String targetName, final String className,
                                                             final String displayName,
                                                             final String configPath) {

        LaunchableTarget lt = new ConfigletLaunchableTargetImpl(targetName, className,
                                                                displayName, configPath);

        launchableTargetHolder.targets.put(targetName, lt);
        save();
        return lt;
    }

    public LaunchableTarget makeConfigletLaunchableTarget(String targetName,
                                                          String className,
                                                          String appFilePrefix,
                                                          Vector jarFileNames,
                                                          String displayName,
                                                          String configPath) {
        LaunchableTarget lt = new ConfigletLaunchableTargetImpl(targetName, className,
                                                                appFilePrefix, jarFileNames,
                                                                    displayName, configPath);

        launchableTargetHolder.targets.put(targetName, lt);
        save();
        return lt;
    }

    private void save() {
        objectRepository.put(KEY, launchableTargetHolder);
    }

    private LaunchableTarget[] subset(Class cls) {

        Vector vec = new Vector();
        Iterator it = launchableTargetHolder.targets.keySet().iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            LaunchableTarget lt = (LaunchableTarget) launchableTargetHolder.targets.get(key);

            if (cls.isInstance(lt)) {
                vec.add(lt);
            }
        }

        SortedSet sortSet = Collections.unmodifiableSortedSet(new TreeSet(vec));

        it = sortSet.iterator();

        LaunchableTarget[] lts = new LaunchableTarget[sortSet.size()];
        int x = 0;

        while (it.hasNext()) {
            LaunchableTarget lt = (LaunchableTarget) it.next();

            lts[x++] = lt;
        }

        return lts;
    }

    /**
     * Method setBuiltInApps
     *
     *
     *
     */
    public void setBuiltInApps() {

        makeKernelLaunchableTarget(
            "System/SimpleInstaller", "net.sourceforge.jesktopimpl.builtinapps.installer.SimpleInstaller",
            "Simple Installer Tool", true);
        makeKernelLaunchableTarget(
            "System/ManageInstalled", "net.sourceforge.jesktopimpl.builtinapps.installer.ManageInstalled",
            "Installed App Management Tool", true);
        makeKernelLaunchableTarget("System/ControlPanel",
                                                          "net.sourceforge.jesktopimpl.builtinapps.config.ControlPanel",
                                                          "Control Panel", true);
        makeConfigletLaunchableTarget(
            "System/DecoratorChooser", "net.sourceforge.jesktopimpl.builtinapps.installer.DecoratorChooser",
            "Chooser for Decorators", "decorator/currentDecorator");
        makeKernelLaunchableTarget(
            MimeManagerImpl.ALLOWED_TARGET_NAME, "net.sourceforge.jesktopimpl.builtinapps.mime.MimeConfigurator",
            "Mimes And Extensions Management", true);
        makeKernelLaunchableTarget(
            SHUTDOWN_APP, "net.sourceforge.jesktopimpl.builtinapps.sys.ShutdownConfirmer", "Shutdown", true);
    }

}
