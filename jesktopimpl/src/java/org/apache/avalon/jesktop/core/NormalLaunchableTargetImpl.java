/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.core;

import org.jesktop.launchable.NormalLaunchableTarget;

import java.util.Vector;


/**
 * Class DecoratorLaunchableTargetImpl
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Jul 2001.
 * @version $Revision: 1.1 $
 */
public class NormalLaunchableTargetImpl extends LaunchableTargetImpl
        implements NormalLaunchableTarget {

    protected NormalLaunchableTargetImpl(final String targetName, final String className, final String displayName,
                                         final boolean singleInst) {
        super(targetName, className, displayName, singleInst);
    }

    protected NormalLaunchableTargetImpl(final String targetName, final String className,
                                         final String appFilePrefix, final Vector jarFileNames,
                                         final String displayName, final boolean singleInst) {
        super(targetName, className, appFilePrefix, jarFileNames, displayName, singleInst);
    }
}
