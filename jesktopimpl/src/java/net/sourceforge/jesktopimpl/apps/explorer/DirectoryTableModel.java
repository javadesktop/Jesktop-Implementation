
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.apps.explorer;



import javax.swing.table.AbstractTableModel;

import java.io.File;

import java.util.Vector;
import java.util.Date;


/**
     * Class DirectoryTableModel
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1 $
     */
public class DirectoryTableModel extends AbstractTableModel {

    private File mDirectory;
    private Vector mContents = new Vector();
    private static final String[] COLHDGS = { "Name", "Size", "Date changed" };
    private static final Class[] COLTYPES = { File.class, Long.class, Date.class };

    protected DirectoryTableModel(File directory) {

        this.mDirectory = directory;

        String[] conts = directory.list();

        for (int f = 0; f < conts.length; f++) {
            File f2 = new File(directory, conts[f]);

            mContents.addElement(f2);
        }
    }

    protected File getDirectory() {
        return mDirectory;
    }

    protected File getFileAtRow(int row) {
        return (File) mContents.elementAt(row);
    }

    /**
     * Method getRowCount
     *
     *
     * @return
     *
     */
    public int getRowCount() {
        return mContents.size();
    }

    /**
     * Method getColumnCount
     *
     *
     * @return
     *
     */
    public int getColumnCount() {
        return COLHDGS.length;
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
        return COLHDGS[columnIndex];
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
        return COLTYPES[columnIndex];
    }

    /**
     * Method isDir
     *
     *
     * @param rowIndex
     *
     * @return
     *
     */
    public boolean isDir(int rowIndex) {

        File file = (File) mContents.elementAt(rowIndex);

        return file.isDirectory();
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

        File file = (File) mContents.elementAt(rowIndex);

        if (columnIndex == 0) {
            return file;
        } else if (columnIndex == 1) {
            return new Long(file.length());
        } else {
            return new Date(file.lastModified());
        }
    }
}
