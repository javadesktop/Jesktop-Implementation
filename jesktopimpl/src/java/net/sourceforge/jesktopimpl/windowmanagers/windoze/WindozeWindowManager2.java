/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.windowmanagers.windoze;

import org.jesktop.frimble.JFrameFrimbleFactory;
import org.jesktop.frimble.JFrimble;
import org.jesktop.frimble.Frimble;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.ImageRepository;
import org.jesktop.AppLauncher;
import org.jesktop.ImageRepository;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;


/**
 * Class WindozeWindowManager2
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.3 $
 */
public class WindozeWindowManager2 extends WindozeWindowManager {

    private JFrameFrimbleFactory mFrimbleFactory;
    protected JPanel mBackdropPanel = new JFrameFrimblePanel();

    /**
     * Constructor WindozeWindowManager2
     *
     *
     */
    public WindozeWindowManager2(ImageRepository imageRepository, AppLauncher appLauncher) {
        super(imageRepository, appLauncher);


        mFrimbleFactory = new JFrameFrimbleFactory();

        JFrimble.setFrimbleFactory(mFrimbleFactory);
    }

    /**
     * Method initializeView2
     *
     *
     */
    public void initializeView2() {

        super.initializeView2();
        frame.getContentPane().add(mBackdropPanel, BorderLayout.CENTER);
        mBackdropPanel.setBackground(new Color(57, 109, 165));    // windows(tm) blue.
        mBackdropPanel.invalidate();
        mBackdropPanel.repaint();
        frame.setVisible(true);
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

    protected void setBackdrop2(final String bPath, final String type) {}

    /**
     * Method updateComponentTreeUI
     *
     *
     */
    public void updateComponentTreeUI() {

        // TODO - all frames
        //SwingUtilities.updateComponentTreeUI(frame);
    }

    protected void acceptLaunchableTarget(final LaunchableTarget lTarget, final Point pt) {}

    /**
     * Class JFrameFrimblePanel
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.3 $
     */
    class JFrameFrimblePanel extends JPanel {

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintComponentHelper(g, this);
        }
    }
}
