/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */

package net.sourceforge.jesktopimpl.core;

import java.util.HashMap;
import java.io.Serializable;

public class LaunchableTargetHolder implements Serializable {
    public int appSuffix = 1;
    public HashMap targets = new HashMap();
}
