/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.builtinapps.sys;

import javax.swing.JLabel;
import java.awt.Color;


/**
 * Class ErrorApp
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ErrorApp extends JLabel {

    /**
     * Constructor ErrorApp
     *
     *
     */
    public ErrorApp() {

        super();

        this.setBackground(Color.white);
        this.setForeground(Color.black);

        // TODO "Open With" functionality of Windows...
    }

    public void setException(Exception e) {
        setText("<html>" + e.getMessage() + "</html>");
    }
    
    public void setMessage(String s) {
        setText("<html>" + s + "</html>");
    }    
    
}
