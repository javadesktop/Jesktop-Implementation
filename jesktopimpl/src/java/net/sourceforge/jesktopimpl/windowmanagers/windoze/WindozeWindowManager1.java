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

import org.jesktop.AppLauncher;
import org.jesktop.ImageRepository;
import org.jesktop.AppLauncher;
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
 * @author Paul Hammant
 * @version $Revision: 1.6 $
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
    public WindozeWindowManager1(ImageRepository imageRepository) {
        super(imageRepository);

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
     * @author Paul Hammant
     * @version $Revision: 1.6 $
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
