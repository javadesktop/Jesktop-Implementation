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
package net.sourceforge.jesktopimpl.apps.explorer;



import javax.swing.table.AbstractTableModel;

import java.io.File;

import java.util.Vector;
import java.util.Date;


/**
     * Class DirectoryTableModel
     *
     *
     * @author Paul Hammant
     * @version $Revision: 1.3 $
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
