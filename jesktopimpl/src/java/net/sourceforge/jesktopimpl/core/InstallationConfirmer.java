/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.core;

import org.jesktop.launchable.LaunchableTarget;

/**
 * Interface InstallationConfirmer
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version * $Revision: 1.1 $
 */
public interface InstallationConfirmer {

    /**
     * Method isRegistered
     *
     *
     * @param targetName
     *
     * @return
     *
     */
    boolean isRegistered(String targetName);

    /**
     * Method confirmLaunchableTarget
     *
     *
     * @param launchableTarget
     *
     */
    void confirmLaunchableTarget(LaunchableTarget launchableTarget);
}
