/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.windowmanagers.windoze;

import org.jesktop.api.AppLauncher;
import org.jesktop.api.ImageRepository;
import org.jesktop.frimble.Frimble;
import org.jesktop.frimble.JFrimble;
import org.jesktop.frimble.JInternalFrameFrimbleFactory;
import org.jesktop.launchable.LaunchableTarget;

import javax.swing.*;
import java.awt.*;


/**
 * Class WindozeWindowManager1
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.2 $
 */
public class WindozeWindowManager1 extends WindozeWindowManager {

    private JInternalFrameFrimbleFactory mFrimbleFactory;
    protected JDesktopPane mDesktopPane = new JInternalFrameFrimbleDesktopPane();
    protected Integer LAYER = new Integer(2);


    /**
     * Constructor WindozeWindowManager1
     *
     *
     */
    public WindozeWindowManager1(ImageRepository imageRepository, AppLauncher appLauncher) {
        super(imageRepository, appLauncher);

        mFrimbleFactory = new JInternalFrameFrimbleFactory(mDesktopPane);

        JFrimble.setFrimbleFactory(mFrimbleFactory);
    }

    /**
     * Method createFrimble
     *
     *
     * @param title
     *
     * @return
     *
     */
    public Frimble createFrimble(final String title) {
        return mFrimbleFactory.getFrimble(title);
    }

    protected final void setBackdrop2(final String bPath, final String type) {
        mDesktopPane.invalidate();
        mDesktopPane.repaint();
    }




    /**
     * Method setup
     *
     *
     */
    public void initializeView2() {

        super.initializeView2();
        frame.getContentPane().add(mDesktopPane, BorderLayout.CENTER);
        mDesktopPane.invalidate();
        mDesktopPane.repaint();
        frame.setVisible(true);
    }

    protected void acceptLaunchableTarget(final LaunchableTarget lTarget, final Point pt) {

        SwingUtilities.convertPointFromScreen(pt, mDesktopPane);

        LaunchableTargetLabel ltl = new LaunchableTargetLabel(lTarget);

        //mDesktopPanel.add(ltl);
        //ltl.setBounds(pt.x, pt.y, (int) ltl.getPreferredSize().getWidth(),
         //             (int) ltl.getPreferredSize().getHeight());
        //ltl.setVisible(true);

    }

    /**
     * Class JInternalFrameFrimbleDesktopPane
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.2 $
     */
    class JInternalFrameFrimbleDesktopPane extends JDesktopPane {

        public JInternalFrameFrimbleDesktopPane() {
            setBackground(new Color(57, 109, 165));    // windows(tm) blue.
        }


        public void repaint() {
            super.repaint();
        }

        public void paint(final Graphics g) {
            super.paint(g);
        }

        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            paintComponentHelper(g, this);
        }
    }
}
