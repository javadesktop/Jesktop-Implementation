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
package net.sourceforge.jesktopimpl.builtinapps.installer;

import javax.swing.JTabbedPane;
import org.jesktop.DesktopKernel;
import org.jesktop.AppInstaller;


/**
 * Class AddRemoveApps
 * NOTE - to be replaced in the future with a more complete tool
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.4 $
 */
public class AddRemoveApps extends JTabbedPane {

    /**
     * Constructor AddRemoveApps
     *
     *
     */
    public AddRemoveApps(DesktopKernel desktopKernel, AppInstaller appInstaller) {

        super(JTabbedPane.TOP);

        this.addTab("Manage Installed Apps", new ManageInstalled(desktopKernel));
        this.addTab("Add new Apps", new SimpleInstaller(desktopKernel, appInstaller));

        //this.addTab("Add JDK Demos", new JDKDemoInstaller());
    }
}
