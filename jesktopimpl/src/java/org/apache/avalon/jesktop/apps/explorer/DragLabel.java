
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.apps.explorer;



import javax.swing.JLabel;

import java.awt.Color;


/**
 * Class DragLabel
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1.1.1 $
 */
public class DragLabel extends JLabel {

    protected DragLabel(String text) {    // label too one day.

        super(text);

        super.setForeground(Color.black);
    }
}
