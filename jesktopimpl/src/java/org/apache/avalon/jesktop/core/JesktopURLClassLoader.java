/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.core;


import java.net.URLClassLoader;
import java.net.URL;
import java.net.URLStreamHandlerFactory;


/**
 * Class JesktopURLClassLoader
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public class JesktopURLClassLoader extends URLClassLoader {

    protected JesktopURLClassLoader(final URL[] urls) {
        super(urls, JesktopURLClassLoader.class.getClassLoader());
    }
}
