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

import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.JesktopLaunchException;
import org.jesktop.DesktopKernel;
import org.jesktop.AppLauncher;
import org.jesktop.JesktopLaunchException;

import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.Point;

/**
 * Class LaunchBarMenuItem
 *
 * @author Dec 2000.
 * @version $Revision: 1.7 $
 */
public class LaunchBarMenuItem extends JMenuItem implements ActionListener {

    protected LaunchableTarget launchableTarget;
    protected DesktopKernel desktopKernel;
    protected WindozeWindowManager windozeWindowManager;
    private AppLauncher appLauncher;

    /**
     * Constructor LaunchBarMenuItem
     *
     * @param launchableTarget
     * @param im
     */
    protected LaunchBarMenuItem(final DesktopKernel desktopKernel, final LaunchableTarget launchableTarget,
                                final WindozeWindowManager windozeWindowManager,
                                final AppLauncher appLauncher,
                                final ImageIcon im) {

        super(launchableTarget.getDisplayName(), im);

        this.desktopKernel = desktopKernel;
        this.launchableTarget = launchableTarget;
        this.windozeWindowManager = windozeWindowManager;
        this.appLauncher = appLauncher;

        addActionListener(this);

/*this.addMenuDragMouseListener(new MenuDragMouseListener() {

                 public void menuDragMouseEntered( MenuDragMouseEvent event ) {
                    System.out.println( "mdm entered" );
                    mRenderLaunchableTargetDrag = false;
                 }

                 public void menuDragMouseExited( MenuDragMouseEvent event ) {
                    System.out.println( "mdm exit" );
                    mRenderLaunchableTargetDrag = true;
                 }

                 public void menuDragMouseDragged( MenuDragMouseEvent event ) {
                     System.out.println( "mdm dragged" );
                 }

                 public void menuDragMouseReleased( MenuDragMouseEvent event ) {
                     System.out.println( "mdm released" );
                 }
             });*/
        this.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent event) {

                Point pt = event.getPoint();

                SwingUtilities.convertPointToScreen(pt, LaunchBarMenuItem.this);
                LaunchBarMenuItem.this.windozeWindowManager.renderLaunchableTargetDragRepresentation(null, pt, true);
            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {

            public void mouseDragged(MouseEvent event) {

                Point pt = event.getPoint();

                SwingUtilities.convertPointToScreen(pt, LaunchBarMenuItem.this);
                LaunchBarMenuItem.this.windozeWindowManager.renderLaunchableTargetDragRepresentation(launchableTarget, pt, false);
            }

            public void mouseMoved(MouseEvent event) {
            }
        });
    }


    /**
     * Method getLaunchableTarget
     *
     * @return
     */
    protected LaunchableTarget getLaunchableTarget() {
        return launchableTarget;
    }

    /**
     * Method actionPerformed
     *
     * @param e
     */
    public void actionPerformed(final ActionEvent e) {

        try {
            appLauncher.launchApp(launchableTarget);
        } catch (JesktopLaunchException jle) {
            //TODO pop a dialog
            jle.printStackTrace();
        }
    }
}
