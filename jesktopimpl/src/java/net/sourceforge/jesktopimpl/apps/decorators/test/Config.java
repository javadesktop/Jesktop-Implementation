/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.apps.decorators.test;

import java.io.Serializable;

/**
 * Class Config (not used until we can overcome a serialization issue with Avalon
 * and class loaders.
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a> Dec 2000.
 * @version V1.0
 */
public class Config implements Serializable {
    public boolean bigIcons;
    public boolean motifLnF;
}
