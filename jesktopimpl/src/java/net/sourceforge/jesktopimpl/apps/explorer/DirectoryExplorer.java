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



import org.jesktop.DesktopKernel;
import org.jesktop.ImageRepository;
import org.jesktop.DesktopKernel;
import org.jesktop.AppLauncher;
import org.jesktop.AppLauncher;
import org.jesktop.appsupport.DropAware;
import org.jesktop.frimble.Frimble;
import org.jesktop.frimble.FrimbleAware;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Class DirectoryExplorer
 *
 *
 * @author Paul Hammant Dec 2000.
 * @version V1.0
 */
public class DirectoryExplorer extends JPanel
        implements FrimbleAware, DropAware {

    private JSplitPane mSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private DirectoryPanel mDirectoryPanel;
    private DirectoryTree mDirectoryTree = new DirectoryTree();
    private JToolBar mToolBar = new JToolBar(JToolBar.HORIZONTAL);
    private JButton mParentBtn = new JButton("Up Dir");

    /**
     * Constructor DirectoryExplorer
     *
     *
     */
    public DirectoryExplorer(DesktopKernel desktopKernel, ImageRepository imageRepository, AppLauncher appLauncher) {

         mDirectoryPanel = new DirectoryPanel(desktopKernel, imageRepository, appLauncher);
        this.setLayout(new BorderLayout());
        this.add(mToolBar, BorderLayout.NORTH);
        this.add(mSplitPane, BorderLayout.CENTER);
        mSplitPane.add(mDirectoryPanel, JSplitPane.RIGHT);
        mSplitPane.add(mDirectoryTree, JSplitPane.LEFT);
        mDirectoryTree.setDirectoryPanel(mDirectoryPanel);
        mToolBar.add(mParentBtn);
        mToolBar.add(new JLabel(""));
        mParentBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                if (ae.getSource() == mParentBtn) {
                    mDirectoryPanel.setParentDirectory();
                }
            }
        });

    }

    // Javadocs will automatically import from interface.

    /**
     * Method setFrimble
     *
     *
     * @param frimble
     *
     */
    public void setFrimble(Frimble frimble) {
        mDirectoryPanel.setFrimble(frimble);
    }

    // Javadocs will automatically import from interface.

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
        return mDirectoryPanel.doYouRecognizeDraggedItem(pt, cl);
    }

    /**
     * Method droppedOnYou
     *
     *
     * @param obj
     *
     */
    public void droppedOnYou(Object obj) {

        // nothing for time being.
    }

    /**
     * Method anotherHasRecognizedDraggedItem
     *
     *
     * @param cl
     *
     */
    public void anotherHasRecognizedDraggedItem(Class cl) {
        mDirectoryPanel.anotherHasRecognizedDraggedItem(cl);
    }

    /**
     * Method draggingStopped
     *
     *
     */
    public void draggingStopped() {
        mDirectoryPanel.draggingStopped();
    }
}
