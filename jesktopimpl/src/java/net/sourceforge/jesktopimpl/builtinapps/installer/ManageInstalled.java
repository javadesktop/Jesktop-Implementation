/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.builtinapps.installer;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Class ManageInstalled
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.3 $
 */
public class ManageInstalled extends JPanel implements FrimbleAware {

    private static final String[] COLS = { "Target Name", "Display Name", "Class Name",
                                           "Can Be Uninstalled" };
    private static final Class[] COLCLASSES = { String.class, String.class, String.class,
                                                Boolean.class };
    private Frimble frimble;
    private DesktopKernel desktopKernel;
    private final JTable appTable = new JTable();
    private AppsTableModel appsTableModel;
    private final JButton uninstBtn = new UnInstBtn();

    /**
     * Constructor ManageInstalled
     *
     *
     */
    public ManageInstalled(DesktopKernel desktopKernel) {
        this.desktopKernel = desktopKernel;

        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(appTable), BorderLayout.CENTER);

        JPanel pnl = new JPanel(new FlowLayout());

        uninstBtn.setEnabled(false);
        pnl.add(uninstBtn);
        this.add(pnl, BorderLayout.SOUTH);

        appsTableModel = new AppsTableModel(desktopKernel.getAllLaunchableTargets());

        appTable.setModel(appsTableModel);
        desktopKernel.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(DesktopKernel.LAUNCHABLE_TARGET_CHANGE)) {
                    appsTableModel = new AppsTableModel((LaunchableTarget[]) evt.getNewValue());

                    appTable.setModel(appsTableModel);
                }
            }
        });

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
    }

    /**
     * Class UnInstBtn
     *
     *
* @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
* @version $Revision: 1.3 $
     */
    private class UnInstBtn extends JButton {

        private UnInstBtn() {

            super("Uninstall");

            appTable.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {

                    int r = appTable.rowAtPoint(e.getPoint());
                    LaunchableTarget lt = appsTableModel.getLaunchableTarget(r);

                    if (e.getClickCount() == 1) {
                        UnInstBtn.this.setEnabled(lt.canBeUnInstalled());
                    }
                }
            });
            this.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {

                    int r = appTable.getSelectedRow();
                    LaunchableTarget lt = appsTableModel.getLaunchableTarget(r);

                    desktopKernel.uninstall(lt);
                    UnInstBtn.this.setEnabled(false);
                }
            });
        }
    }

    /**
     * Class AppsTableModel
     *
     *
* @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
* @version $Revision: 1.3 $
     */
    private class AppsTableModel extends AbstractTableModel {

        private LaunchableTarget[] apps;

        private AppsTableModel(LaunchableTarget[] apps) {
            this.apps = apps;
        }

        private LaunchableTarget getLaunchableTarget(int rowIndex) {
            return apps[rowIndex];
        }

        /**
         * Method getRowCount
         *
         *
         * @return
         *
         */
        public int getRowCount() {
            return apps.length;
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
                return apps[rowIndex].getTargetName();

            case 1 :
                return apps[rowIndex].getDisplayName();

            case 2 :
                return apps[rowIndex].getClassName();

            case 3 :
                return new Boolean(apps[rowIndex].canBeUnInstalled());
            }

            return null;
        }
    }
}
