/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.services;

import org.jesktop.api.DesktopKernel;
import org.jesktop.api.LaunchedTarget;
import org.jesktop.frimble.FrimbleListener;
import org.jesktop.frimble.Frimble;
import org.jesktop.launchable.LaunchableTarget;

import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;

public interface DesktopKernelService extends DesktopKernel, PropertyChangeListener {

    FrimbleListener getKernelFrimbleListener();

    LaunchedTarget makeKernelLaunchedTarget(Frimble frimble, Object instantiatedApp, LaunchableTarget launchableTarget);

    void shutdownJesktopOnly(boolean force) throws PropertyVetoException;

    void shutdownAvalon(boolean force) throws PropertyVetoException;

}
