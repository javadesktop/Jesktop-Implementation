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



import org.jesktop.ImageRepository;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import java.awt.Component;

import java.io.File;


/**
     * Class DirectoryTableCellRenderer
     *
     *
     * @author Paul Hammant
     * @version $Revision: 1.4 $
     */
public class DirectoryTableCellRenderer extends DefaultTableCellRenderer {

    private ImageRepository mImageRepository;

    protected DirectoryTableCellRenderer(ImageRepository imageRepository) {
        mImageRepository = imageRepository;
    }

    /**
     * Method getTableCellRendererComponent
     *
     *
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param column
     *
     * @return
     *
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row,
                                                   int column) {

        if (value instanceof File) {
            File file = (File) value;
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, file.getName(),
                                                                      isSelected, hasFocus, row,
                                                                      column);

            lbl.setIcon(null);
            lbl.setBorder(new EmptyBorder(3, 3, 3, 3));

            if (file.isDirectory()) {
                lbl.setIcon(mImageRepository.getFileSmallImageIcon("directory"));
            } else {
                if (file.getAbsolutePath().toLowerCase().endsWith(".jpg")) {

                    // should get icon from jar itself (if there) // TODO PH
                    lbl.setIcon(mImageRepository.getAppSmallImageIcon("?"));
                } else {

                    // should get correct image per mime type
                    lbl.setIcon(mImageRepository.getFileSmallImageIcon("text/plain"));    //TODO LAURENT
                }
            }

            return lbl;
        } else if (value instanceof Long) {
            Long lo = (Long) value;
            String val = "";
            long n = lo.longValue();

            if (n >= 1024 * 1000 * 1000) {
                val = (n / (1024 * 1000 * 1000)) + "GB";
            } else if (n >= 1024 * 1000) {
                val = (n / (1024 * 1000)) + "MB";
            } else if (n >= 1024) {
                val = (n / (1024)) + "KB";
            } else {
                val = "1KB";
            }

            return super.getTableCellRendererComponent(table, val, isSelected, hasFocus, row,
                                                       column);
        } else {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                                                       column);
        }
    }
}
