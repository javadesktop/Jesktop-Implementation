
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.apps.explorer;



import org.jesktop.api.ImageRepository;

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
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1 $
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
