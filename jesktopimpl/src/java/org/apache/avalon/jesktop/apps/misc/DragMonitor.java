/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.apps.misc;

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
