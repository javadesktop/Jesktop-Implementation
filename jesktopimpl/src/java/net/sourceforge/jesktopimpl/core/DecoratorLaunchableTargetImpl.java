/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.core;

import org.jesktop.launchable.DecoratorLaunchableTarget;

import java.util.Vector;


/**
 * Class DecoratorLaunchableTargetImpl
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.1 $
 */
public class DecoratorLaunchableTargetImpl extends ConfigurableLaunchableTargetImpl
        implements DecoratorLaunchableTarget {

    protected DecoratorLaunchableTargetImpl(final String targetName, final String className,
                                            final String displayName, final String configPath) {
        super(targetName, className, displayName, configPath);
    }

    protected DecoratorLaunchableTargetImpl(final String targetName, final String className,
                                            final String appFilePrefix, final Vector jarFileNames,
                                            final String displayName, final String configPath) {
        super(targetName, className, appFilePrefix, jarFileNames, displayName, configPath);
    }
}
