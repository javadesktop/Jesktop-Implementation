/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.builtinapps.installer;

import javax.swing.JTabbedPane;


/**
 * Class AddRemoveApps
 * NOTE - to be replaced in the future with a more complete tool
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.1 $
 */
public class AddRemoveApps extends JTabbedPane {

    /**
     * Constructor AddRemoveApps
     *
     *
     */
    public AddRemoveApps() {

        super(JTabbedPane.TOP);

        this.addTab("Manage Installed Apps", new ManageInstalled());
        this.addTab("Add new Apps", new SimpleInstaller());

        //this.addTab("Add JDK Demos", new JDKDemoInstaller());
    }
}
