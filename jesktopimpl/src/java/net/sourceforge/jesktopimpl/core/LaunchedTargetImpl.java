/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.core;

import org.jesktop.api.LaunchedTarget;


/**
 * Class LaunchedTarget
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.1 $
 */
public abstract class LaunchedTargetImpl implements LaunchedTarget {

    private String targetName;
    private String displayName;

    /**
     * Constructor LaunchedTarget
     *
     *
     *
     * @param targetName
     * @param dislayName
     *
     */
    public LaunchedTargetImpl(final String targetName, final String dislayName) {
        this.targetName = targetName;
        this.displayName = displayName;
    }

    /**
     * Method getName
     *
     *
     * @return
     *
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Method getDisplaytName
     *
     *
     * @return
     *
     */
    public String getDisplaytName() {
        return displayName;
    }
}
