/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.core;



import java.beans.PropertyVetoException;


/**
 * Class Shutdown
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.2 $
 */
public interface ShutdownConfirmer {

    /**
     * Method shutdownJesktopOnly
     *
     *
     * @param force
     *
     * @throws PropertyVetoException
     *
     */
    void shutdownJesktopOnly(boolean force) throws PropertyVetoException;

    /**
     * Method shutdownSystem
     *
     *
     * @param force
     *
     * @throws PropertyVetoException
     *
     */
    void shutdownSystem(boolean force) throws PropertyVetoException;
}
