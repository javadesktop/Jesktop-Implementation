/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.builtinapps.mime;

import org.jesktop.frimble.FrimbleAware;
import org.jesktop.frimble.Frimble;
import org.jesktop.api.DesktopKernelAware;
import org.jesktop.api.DesktopKernel;
import org.jesktop.mime.MimeManager;
import org.jesktop.mime.MimeInfo;
import org.jesktop.mime.MimeException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Container;

/**
 *
 * Application to manage Mime types in the system (Not working yet, only for demonstration purposes)
 *
 * @author  Laurent Cornelis <nelis2@yahoo.com>
 * @version 1.0
 */
public class MimeConfigurator extends JPanel
        implements FrimbleAware, ActionListener, DesktopKernelAware {

    private Frimble frimble;
    protected DesktopKernel desktopKernel;
    protected MimeManager mimeManager;
    private JScrollPane scrollPane;
    private JTable mimeList;
    private MimeTableModel model;
    private JButton addMime;
    private JButton editMime;
    private JButton removeMime;

    /**
     * Constructor MimeConfigurator
     *
     *
     */
    public MimeConfigurator() {}

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
        mimeManager = desktopKernel.getMimeManager();

        try {
            jbInit();
            JOptionPane.showMessageDialog(
                this, "This application is not working yet, it's just for demonstration purpose",
                "Application not working yet", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method actionPerformed
     *
     *
     * @param ev
     *
     */
    public void actionPerformed(ActionEvent ev) {

        Object source = ev.getSource();

        if (source == addMime) {
            MimeEditor ed = new MimeEditor();
        } else if (source == editMime) {
            int row = mimeList.getSelectedRow();

            if (row == -1) {
                return;
            }

            MimeInfo minfo = mimeManager.getMimeInfo(row);
            MimeEditor ed = new MimeEditor(minfo);
        } else if (source == removeMime) {
            int row = mimeList.getSelectedRow();

            if (row == -1) {
                return;
            }

            MimeInfo minfo = mimeManager.getMimeInfo(row);
            int confirm =
                JOptionPane
                    .showConfirmDialog(this, "Are you sure you want to remove mime "
                                       + minfo.getMime()
                                       + " ?", "Confirmation", JOptionPane
                                           .OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.OK_OPTION) {
                try {
                    mimeManager.unregisterMime(minfo.getMime());
                    model.fireTableDataChanged();
                } catch (Exception e) {}
            }
        }
    }

    private void jbInit() throws Exception {

        this.setLayout(new GridBagLayout());

        addMime = new JButton("Add");
        editMime = new JButton("Edit");
        removeMime = new JButton("Remove");

        addMime.addActionListener(this);
        editMime.addActionListener(this);
        removeMime.addActionListener(this);

        model = new MimeTableModel();
        mimeList = new JTable(model);

        mimeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scrollPane = new JScrollPane(mimeList);

        this.add(scrollPane,
                 new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(addMime,
                 new GridBagConstraints(0, 1, 1, 1, 1.0, 0, GridBagConstraints.CENTER,
                                        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(editMime,
                 new GridBagConstraints(1, 1, 1, 1, 1.0, 0, GridBagConstraints.CENTER,
                                        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(removeMime,
                 new GridBagConstraints(2, 1, 1, 1, 1.0, 0, GridBagConstraints.CENTER,
                                        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    /**
     * Class MimeTableModel
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1.1.1 $
     */
    class MimeTableModel extends AbstractTableModel {

        private final String[] colNames = { "Extensions", "Description", "Mime" };
        private final int EXTENSION = 0;
        private final int DESCRIPTION = 1;
        private final int MIME = 2;

        /**
         * Method getColumnName
         *
         *
         * @param col
         *
         * @return
         *
         */
        public String getColumnName(int col) {
            return colNames[col];
        }

        /**
         * Method getRowCount
         *
         *
         * @return
         *
         */
        public int getRowCount() {
            return mimeManager.countRegisteredMimes();
        }

        /**
         * Method getColumnCount
         *
         *
         * @return
         *
         */
        public int getColumnCount() {
            return colNames.length;
        }

        /**
         * Method getValueAt
         *
         *
         * @param row
         * @param col
         *
         * @return
         *
         */
        public Object getValueAt(int row, int col) {

            MimeInfo minfo = mimeManager.getMimeInfo(row);

            switch (col) {

            case EXTENSION :
                return minfo.getExtensionsAsString();

            case DESCRIPTION :
                return minfo.getDescription();

            case MIME :
                return minfo.getMime();
            }

            return null;
        }
    }

    /**
     * Class MimeEditor
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1.1.1 $
     */
    class MimeEditor extends JDialog implements ActionListener {

        private MimeInfo minfo;
        private boolean editMode;
        private JTextField mime;
        private JTextField description;
        private JTextField extensions;
        private JButton ok;
        private JButton cancel;

        /**
         * Constructor MimeEditor
         *
         *
         * @param minfo
         *
         */
        public MimeEditor(MimeInfo minfo) {

            super(new JFrame(), true);

            this.minfo = minfo;
            this.editMode = true;

            this.setTitle("Edit Mime (" + minfo.getMime() + ")");
            buildGUI();
        }

        /**
         * Constructor MimeEditor
         *
         *
         */
        public MimeEditor() {

            super(new JFrame(), "Add Mime", true);

            this.editMode = false;

            System.out.println("MimeEditor()");
            buildGUI();
        }

        private void buildGUI() {

            System.out.println("builGUI()");
            this.setResizable(false);
            this.setSize(200, 150);

            Container cp = this.getContentPane();

            cp.setLayout(new BorderLayout());

            if (editMode) {
                mime = new JTextField(minfo.getMime());
                description = new JTextField(minfo.getDescription());
                extensions = new JTextField(minfo.getExtensionsAsString());
            } else {
                mime = new JTextField();
                description = new JTextField();
                extensions = new JTextField();
            }

            JPanel textPanel = new JPanel(new GridLayout(3, 2));

            textPanel.add(new JLabel("Mime"));
            textPanel.add(mime);
            textPanel.add(new JLabel("Description"));
            textPanel.add(description);
            textPanel.add(new JLabel("Extensions"));
            textPanel.add(extensions);

            JPanel buttonPanel = new JPanel();

            ok = new JButton("Ok");
            cancel = new JButton("Cancel");

            ok.addActionListener(this);
            cancel.addActionListener(this);
            buttonPanel.add(ok);
            buttonPanel.add(cancel);
            cp.add(textPanel, BorderLayout.CENTER);
            cp.add(buttonPanel, BorderLayout.SOUTH);
            this.show();
        }

        /**
         * Method actionPerformed
         *
         *
         * @param ev
         *
         */
        public void actionPerformed(ActionEvent ev) {

            Object source = ev.getSource();

            if (source == ok) {
                try {
                    if (editMode) {
                        mimeManager.updateMimeInfo(minfo, mime.getText(), description.getText());
                        mimeManager.unregisterExtensionsForMime(minfo);
                        mimeManager.registerExtensionsForMime(extensions.getText(), minfo);
                    } else {
                        minfo = mimeManager.createMimeInfo(mime.getText(), description.getText());

                        mimeManager.registerMime(minfo);
                        mimeManager.registerExtensionsForMime(extensions.getText(), minfo);
                    }

                    model.fireTableDataChanged();
                    dispose();
                } catch (MimeException e) {
                    JOptionPane.showMessageDialog(this, e.toString(), "Error",
                                                  JOptionPane.ERROR_MESSAGE);
                }
            } else if (source == cancel) {
                dispose();
            }
        }
    }
}
