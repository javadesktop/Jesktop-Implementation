
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.apps.explorer;



import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;


/**
 * Class ItemPreview
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a> Dec 2000.
 * @version V1.0
 */
public class ItemPreview extends JPanel {

    ActualPreview actualPreview = new ActualPreview();

    /**
     * Constructor ItemPreview
     *
     *
     */
    protected ItemPreview() {

        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());
        this.add(new JLabel("Stats here"), BorderLayout.NORTH);
        this.add(actualPreview, BorderLayout.SOUTH);
    }

    /**
     * Method getPreferredSize
     *
     *
     * @return
     *
     */
    public Dimension getPreferredSize() {

        int ht = (int) super.getPreferredSize().getHeight();

        return new Dimension(120, ht);
    }

    /**
     * Method getPreviewPanel
     *
     *
     * @return
     *
     */
    public JComponent getPreviewPanel() {
        return actualPreview;
    }

    /**
     * Class ActualPreview
     *
     *
     * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a> Dec 2000.
     * @version V1.0
     */
    private class ActualPreview extends JScrollPane {

        /**
         * Constructor ActualPreview
         *
         *
         */
        public ActualPreview() {
            super(new JLabel("?"));
        }

        /**
         * Method paint
         *
         *
         * @param g
         *
         */
        public void paint(Graphics g) {
            super.paint(g);
        }

        /**
         * Method getMinimumSize
         *
         *
         * @return
         *
         */
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        /**
         * Method getPreferredSize
         *
         *
         * @return
         *
         */
        public Dimension getPreferredSize() {
            return new Dimension(120, 120);
        }
    }
}
