/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.builtinapps.installer;



import org.jesktop.frimble.FrimbleAware;
import org.jesktop.frimble.Frimble;
import org.jesktop.api.DesktopKernelAware;
import org.jesktop.api.DesktopKernel;
import org.jesktop.launchable.LaunchableTarget;
import org.apache.avalon.jesktop.core.InstallationConfirmer;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;




/**
 * Class ConfirmInstallation
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.1.1.1 $
 */
public class ConfirmInstallation extends JPanel implements FrimbleAware, DesktopKernelAware {

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
    public ConfirmInstallation() {

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

    /**
     * Class ApplyExit
     *
     *
* @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
* @version $Revision: 1.1.1.1 $
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
* @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
* @version $Revision: 1.1.1.1 $
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
