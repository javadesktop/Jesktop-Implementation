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
 * @author Paul Hammant
 * @version $Revision: 1.6 $
 */
public class WindozeWindowManager2 extends WindozeWindowManager {

    private JFrameFrimbleFactory mFrimbleFactory;
    protected JPanel mBackdropPanel = new JFrameFrimblePanel();

    /**
     * Constructor WindozeWindowManager2
     *
     *
     */
    public WindozeWindowManager2(ImageRepository imageRepository) {
        super(imageRepository);


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
     * @author Paul Hammant
     * @version $Revision: 1.6 $
     */
    class JFrameFrimblePanel extends JPanel {

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintComponentHelper(g, this);
        }
    }
}
