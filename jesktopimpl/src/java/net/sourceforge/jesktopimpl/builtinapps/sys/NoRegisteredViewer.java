/* ====================================================================
 * Copyright 2000 - 2004, The Jesktop project committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Portions of this code are copyright Apache Software Foundation, and licensed
 * under the Apache Software License 1.1
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
