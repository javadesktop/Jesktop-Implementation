/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.builtinapps.sys;

import org.jesktop.appsupport.ContentViewer;

import java.net.URL;


/**
 * Class NoRegisteredViewer
 *
 *
 * @author
 * @version %I%, %G%
 */
public class NoRegisteredViewer extends ErrorApp implements ContentViewer {

    /**
     * Constructor NoRegisteredViewer
     *
     *
     */
    public NoRegisteredViewer() {

        super();

        setMessage("No Registered Viewer");

    }

    /**
     * Method viewContent is used for viewing content specified by the URL.  If the content
     * is not visible, then the default non-edit action for that type should
     * be done.  E.g. for MP3s it is "listen".
     *
     * @param url the content to be viewed
     * @param thumbNail the content should be viewed in thumbnail view.
     *
     */
    public void viewContent( URL url, boolean thumbNail ) {
    }
}
