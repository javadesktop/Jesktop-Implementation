/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.builtinapps.sys;

import org.jesktop.frimble.FrimbleAware;
import org.jesktop.frimble.Frimble;
import org.jesktop.api.DesktopKernelAware;
import org.jesktop.api.DesktopKernel;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;


/**
 * Class ShutdownConfimer is launched by the Kernel when a suhutdown is requested
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version %I%, %G%
 */
public class ShutdownConfirmer extends JPanel implements FrimbleAware, DesktopKernelAware {

    private Frimble frimble;
    private DesktopKernel desktopKernel;
    private final String exitAll = "Shutdown Desktop & Avalon";
    private final String exitJesktop = "Shutdown Desktop Only";
    private final JComboBox choices = new JComboBox(new String[]{ exitAll, exitJesktop });
    private final JButton okBtn = new JButton("OK");
    private final JButton cancelBtn = new JButton("Cancel");
    private final JLabel shutdownWarning = new JLabel(
        "<html><b>Do you want to close Avalon as well as Jesktop?</b><br>Closing Avalon too will shut down other services such as Mail and Web Servers.</html>");
    private org.apache.avalon.jesktop.core.ShutdownConfirmer shutdownConfirmer;

    /**
     * Constructor ShutdownConfimer
     *
     *
     */
    public ShutdownConfirmer() {

        JPanel messagePanel = new JPanel(new BorderLayout());

        messagePanel.add(shutdownWarning, BorderLayout.CENTER);
        messagePanel.add(choices, BorderLayout.SOUTH);
        choices.setSelectedIndex(0);

        JPanel buttonsPanel = new JPanel(new FlowLayout());

        buttonsPanel.add(okBtn);
        buttonsPanel.add(cancelBtn);
        this.setLayout(new BorderLayout());
        this.add(messagePanel, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);
        cancelBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                frimble.dispose();
            }
        });
        okBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                try {
                    if (choices.getSelectedItem().equals(exitAll)) {
                        shutdownConfirmer.shutdownAvalon(false);
                    } else {
                        shutdownConfirmer.shutdownJesktopOnly(false);
                    }
                } catch (PropertyVetoException pve) {
                    JOptionPane.showMessageDialog(ShutdownConfirmer.this,
                                                  "Shutdown Vetod by by one the of the Apps");
                }
            }
        });
    }

    /**
     * Method setShutdownCallback is used by the Kernel to provide a hook for app
* to signal the users actual intention in respect of shutdown.
     *
     *
     * @param shutdownConfirmer the call-back
     *
     */
    public void setShutdownCallback(org.apache.avalon.jesktop.core.ShutdownConfirmer shutdownConfirmer) {
        this.shutdownConfirmer = shutdownConfirmer;
    }

    // Javadocs will automatically import from interface.

    /**
     * Method setFrimble
     *
     *
     * @param frimble
     *
     */
    public void setFrimble(Frimble frimble) {

        this.frimble = frimble;

        frimble.pack();
    }

    // Javadocs will automatically import from interface.

    /**
     * Method setDesktopKernel
     *
     *
     * @param mDesktopKernel
     *
     */
    public void setDesktopKernel(DesktopKernel desktopKernel) {
        this.desktopKernel = desktopKernel;
    }
}
