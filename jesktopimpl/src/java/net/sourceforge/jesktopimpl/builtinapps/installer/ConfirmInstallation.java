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



import net.sourceforge.jesktopimpl.core.InstallationConfirmer;
import org.jesktop.DesktopKernel;
import org.jesktop.frimble.Frimble;
import org.jesktop.frimble.FrimbleAware;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.DesktopKernel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;




/**
 * Class ConfirmInstallation
 *
 *
 * @author  Dec 2000.
 * @version $Revision: 1.5 $
 */
public class ConfirmInstallation extends JPanel implements FrimbleAware {

    private static final String[] COLS = { "Target Name", "Display Name", "Class Name",
                                           "Is currently installed", "Install" };
    private static final Class[] COLCLASSES = { String.class, String.class, String.class,
                                                Boolean.class, Boolean.class };
    private Frimble frimble;
    private DesktopKernel desktopKernel;
    private final JTable appTable = new JTable();
    private AppsTableModel appsTableModel;
    private final JButton applyBtn = new ApplyExit();
    private InstallationConfirmer installationConfirmer;
    private LaunchableTarget[] pendingInstalled;
    private boolean[] confirmedInstalls;

    /**
     * Constructor ConfirmInstallation
     *
     *
     */
    public ConfirmInstallation(DesktopKernel desktopKernel) {
        this.desktopKernel = desktopKernel;

        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(appTable), BorderLayout.CENTER);

        JPanel pnl = new JPanel(new FlowLayout());

        pnl.add(applyBtn);
        this.add(pnl, BorderLayout.SOUTH);
    }

    /**
     * Method setConfirmationDetails
     *
     *
     * @param installationConfirmer
     * @param pendingInstalled
     *
     */
    public void setConfirmationDetails(InstallationConfirmer installationConfirmer,
                                       LaunchableTarget[] pendingInstalled) {

        this.installationConfirmer = installationConfirmer;
        this.pendingInstalled = pendingInstalled;
        confirmedInstalls = new boolean[pendingInstalled.length];

        for (int f = 0; f < confirmedInstalls.length; f++) {
            confirmedInstalls[f] = true;    // default true, feedback said users were just hitting OK.
        }

        appsTableModel = new AppsTableModel();

        appTable.setModel(appsTableModel);
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

    /**
     * Class ApplyExit
     *
     *
* @author  Dec 2000.
* @version $Revision: 1.5 $
     */
    private class ApplyExit extends JButton {

        private ApplyExit() {

            super("Apply & Exit");

            this.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {

                    for (int f = 0; f < confirmedInstalls.length; f++) {
                        if (confirmedInstalls[f]) {
                            installationConfirmer.confirmLaunchableTarget(pendingInstalled[f]);
                        }
                    }

                    desktopKernel.notifyLaunchableTargetListeners();
                    frimble.dispose();
                }
            });
        }
    }

    /**
     * Class AppsTableModel
     *
     *
* @author  Dec 2000.
* @version $Revision: 1.5 $
     */
    private class AppsTableModel extends AbstractTableModel {

        private AppsTableModel() {}

        private LaunchableTarget getLaunchableTarget(int rowIndex) {
            return pendingInstalled[rowIndex];
        }

        /**
         * Method getRowCount
         *
         *
         * @return
         *
         */
        public int getRowCount() {
            return pendingInstalled.length;
        }

        /**
         * Method isCellEditable
         *
         *
         * @param rowIndex
         * @param columnIndex
         *
         * @return
         *
         */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return (columnIndex == 4);
        }

        /**
         * Method getColumnCount
         *
         *
         * @return
         *
         */
        public int getColumnCount() {
            return COLS.length;
        }

        /**
         * Method getColumnName
         *
         *
         * @param columnIndex
         *
         * @return
         *
         */
        public String getColumnName(int columnIndex) {
            return COLS[columnIndex];
        }

        /**
         * Method getColumnClass
         *
         *
         * @param columnIndex
         *
         * @return
         *
         */
        public Class getColumnClass(int columnIndex) {
            return COLCLASSES[columnIndex];
        }

        /**
         * Method getValueAt
         *
         *
         * @param rowIndex
         * @param columnIndex
         *
         * @return
         *
         */
        public Object getValueAt(int rowIndex, int columnIndex) {

            switch (columnIndex) {

            case 0 :
                return pendingInstalled[rowIndex].getTargetName();

            case 1 :
                return pendingInstalled[rowIndex].getDisplayName();

            case 2 :
                return pendingInstalled[rowIndex].getClassName();

            case 3 :
                return new Boolean(installationConfirmer
                    .isRegistered(pendingInstalled[rowIndex].getTargetName()));

            case 4 :
                return new Boolean(confirmedInstalls[rowIndex]);
            }

            return null;
        }

        /**
         * Method setValueAt
         *
         *
         * @param aValue
         * @param rowIndex
         * @param columnIndex
         *
         */
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            confirmedInstalls[rowIndex] = ((Boolean) aValue).booleanValue();
        }
    }
}
