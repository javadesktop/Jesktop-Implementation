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

import org.jesktop.frimble.Frimble;
import org.jesktop.frimble.FrimbleAware;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;


/**
 * Class ShutdownConfimer is launched by the Kernel when a suhutdown is requested
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version %I%, %G%
 */
public class ShutdownConfirmer extends JPanel implements FrimbleAware {

    private Frimble frimble;
    private final String exitAll = "Shutdown Desktop & System";
    private final String exitJesktop = "Shutdown Desktop Only";
    private final JComboBox choices = new JComboBox(new String[]{ exitAll, exitJesktop });
    private final JButton okBtn = new JButton("OK");
    private final JButton cancelBtn = new JButton("Cancel");
    private final JLabel shutdownWarning = new JLabel(
        "<html><b>Do you want to shut the system down as well as Jesktop?</b><br>Closing the system too may shut down other services such as Mail and Web Servers.</html>");
    private net.sourceforge.jesktopimpl.core.ShutdownConfirmer shutdownConfirmer;

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
                        shutdownConfirmer.shutdownSystem(false);
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
    public void setShutdownCallback(net.sourceforge.jesktopimpl.core.ShutdownConfirmer shutdownConfirmer) {
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
}
