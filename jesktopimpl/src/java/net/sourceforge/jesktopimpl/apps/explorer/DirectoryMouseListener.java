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



import org.jesktop.JesktopPackagingException;
import org.jesktop.JesktopLaunchException;
import org.jesktop.DesktopKernel;
import org.jesktop.AppLauncher;
import org.jesktop.appsupport.DraggedItem;

import javax.swing.JComponent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.IOException;


/**
     * Class DirectoryMouseListener
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.4 $
     */
public class DirectoryMouseListener extends MouseAdapter {

    private DirectoryTableModel mDirectoryTableModel;
    private final DesktopKernel mDesktopKernel;
    private DirectoryPanel mDirectoryPanel;
    private AppLauncher appLauncher;

    protected DirectoryMouseListener(final DesktopKernel desktopKernel, final DirectoryPanel directoryPanel, AppLauncher appLauncher) {
        mDesktopKernel = desktopKernel;
        mDirectoryPanel = directoryPanel;
        this.appLauncher = appLauncher;
    }

    protected void setDirectoryTableModel(final DirectoryTableModel directoryTableModel) {
        mDirectoryTableModel = directoryTableModel;
    }

    /**
     * Method mouseClicked
     *
     *
     * @param e
     *
     */
    public void mouseClicked(final MouseEvent e) {

        try {
            int r = mDirectoryPanel.getContentsTable().rowAtPoint(e.getPoint());
            File file = mDirectoryTableModel.getFileAtRow(r);

            if (e.getClickCount() == 1) {
                if (!file.isDirectory()) {
                    JComponent prvComp = mDirectoryPanel.getItemPreview().getPreviewPanel();

                    mDesktopKernel.viewDocument(file.toURL(), prvComp, true);
                }
            }

            if (e.getClickCount() == 2) {
                if (file.isDirectory()) {
                    mDirectoryPanel.setDirectory(file);
                } else {
                    if (file.getCanonicalPath().toLowerCase().endsWith(".jar")) {
                        try {
                            appLauncher.launchAppWithoutInstallation(file.toURL());
                        } catch (JesktopPackagingException baxmle) {
                            System.err.println("Jar not executable, reason "
                                               + baxmle.getMessage());
                        }
                    } else {
                        mDesktopKernel.viewDocument(file.toURL());
                    }
                }
            }
        } catch (JesktopLaunchException jle) {
            jle.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();

            //} catch (MalformedURLException mue) {
            //mue.printStackTrace();
        }
    }

    /**
     * Method mousePressed
     *
     *
     * @param e
     *
     */
    public void mousePressed(final MouseEvent e) {

        int r = mDirectoryPanel.getContentsTable().rowAtPoint(e.getPoint());
        File file = mDirectoryTableModel.getFileAtRow(r);

        mDirectoryPanel.setCurrentlyDraggedItem(new DraggedItem(new DragLabel(file.getName()),
                                                                file));
    }

    /**
     * Method mouseReleased
     *
     *
     * @param e
     *
     */
    public void mouseReleased(final MouseEvent e) {
        mDesktopKernel.itemBeingDragged(null, null);
    }
}
