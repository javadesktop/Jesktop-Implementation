/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.services;

import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.launchable.DecoratorLaunchableTarget;
import org.jesktop.launchable.ConfigletLaunchableTarget;
import org.jesktop.launchable.NormalLaunchableTarget;

import javax.swing.JComponent;
import java.util.Vector;

public interface LaunchableTargetFactory  {

    String KEY = "jesktop-instapps";
    String SHUTDOWN_APP = "~Shutdown";

    LaunchableTarget[] getAllLaunchableTargets();

    DecoratorLaunchableTarget makeDecoratorLaunchableTarget(
            final String targetName,
            final String className,
            final String displayName,
            final String configPath);

    DecoratorLaunchableTarget makeDecoratorLaunchableTarget(
            final String targetName, final String className,
            final String appFilePrefix,
            final Vector jarFileNames,
            final String displayName,
            final String configPath);

    boolean isRegistered(final String targetName);

    ConfigletLaunchableTarget[] getConfigletLaunchableTargets();

    DecoratorLaunchableTarget[] getDecoratorLaunchableTargets();

    NormalLaunchableTarget[] getNormalLaunchableTargets();

    void removeLaunchableTarget(final String targetName);

    void confirmLaunchableTarget(final LaunchableTarget launchableTarget);

    ClassLoader getClassLoader(final LaunchableTarget launchableTarget);

    String getNewAppSuffix();

    LaunchableTarget getLaunchableTarget(final String targetName);

    LaunchableTarget getLaunchableTarget(final JComponent launched);

    void makeNormalLaunchableTarget(final String targetName,
                             final String className,
                             final String displayName,
                             final boolean singleInstance);

    LaunchableTarget makeNormalLaunchableTarget(final String targetName,
                             final String className,
                             final String appFilePrefix,
                             final Vector jarFileNames,
                             final String displayName,
                             final boolean singleInstance);

    LaunchableTarget makeConfigletLaunchableTarget(final String targetName,
                             final String className,
                             final String appFilePrefix,
                             final Vector jarFileNames,
                             final String displayName,
                             final String configPath);

}
