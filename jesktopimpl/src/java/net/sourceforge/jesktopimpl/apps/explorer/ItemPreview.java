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
 * @author Paul Hammant Dec 2000.
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
     * @author Paul Hammant Dec 2000.
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
