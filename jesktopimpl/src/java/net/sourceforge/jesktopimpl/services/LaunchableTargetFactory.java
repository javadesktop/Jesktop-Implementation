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
