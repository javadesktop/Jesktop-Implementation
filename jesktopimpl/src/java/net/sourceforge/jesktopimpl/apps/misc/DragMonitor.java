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
package net.sourceforge.jesktopimpl.apps.misc;

import org.jesktop.appsupport.DropAware;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.Point;


/**
 * This app watches the dragging that is going on.
 *
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class DragMonitor extends JPanel implements DropAware {

    JLabel clName, xPos, yPos;

    /**
     * Constructor DragMonitor
     *
     *
     */
    public DragMonitor() {

        this.setLayout(new GridLayout(3, 2));

        clName = new JLabel("<no drag>");
        xPos = new JLabel("-");
        yPos = new JLabel("-");

        this.add(new JLabel("Class being dragged"));
        this.add(clName);
        this.add(new JLabel("X Posn"));
        this.add(xPos);
        this.add(new JLabel("Y Posn"));
        this.add(yPos);
    }

    /**
     * Method doYouRecognizeDraggedItem
     *
     *
     * @param pt
     * @param cl
     *
     * @return
     *
     */
    public boolean doYouRecognizeDraggedItem(final Point pt, Class cl) {

        clName.setText(cl.getName());
        xPos.setText("" + pt.x);
        yPos.setText("" + pt.y);

        return false;
    }

    /**
     * Method droppedOnYou
     *
     *
     * @param obj
     *
     */
    public void droppedOnYou(Object obj) {}

    /**
     * Method anotherHasRecognizedDraggedItem
     *
     *
     * @param cl
     *
     */
    public void anotherHasRecognizedDraggedItem(Class cl) {

        clName.setText("?");
        xPos.setText("?");
        yPos.setText("?");
    }

    /**
     * Method draggingStopped
     *
     *
     */
    public void draggingStopped() {

        clName.setText("<no drag>");
        xPos.setText("-");
        yPos.setText("-");
    }
}
