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
import org.jesktop.AppLauncher;
import org.jesktop.appsupport.DraggedItem;
import org.jesktop.appsupport.DropAware;
import org.jesktop.frimble.Frimble;
import org.jesktop.frimble.FrimbleAware;
import org.jesktop.ImageRepository;
import org.jesktop.AppLauncher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;


/**
 * Class DirectoryPanel
 *
 *
 * @author Paul Hammant Dec 2000.
 * @version V1.0
 */
public class DirectoryPanel extends JPanel
        implements FrimbleAware, DropAware {

    private DirectoryTableModel mDirectoryTableModel;
    private DirectoryMouseListener mDirectoryMouseListener;
    private JTable mContentsTable;
    private JScrollPane mScrollPane;
    private DraggedItem mCurrentlyDraggedItem;
    private DefaultTableModel mDefaultTableModel = new DefaultTableModel(0, 0);
    private JLabel mDropTargetLabel = new JLabel("?");
    private Frimble mFrimble;
    private ItemPreview mItemPreview = new ItemPreview();
    private ImageRepository imageRepository;

    /**
     * Constructor DirectoryPanel
     *
     *
     */
    protected DirectoryPanel(DesktopKernel desktopKernel, ImageRepository imageRepository, AppLauncher appLauncher) {

        this.imageRepository = imageRepository;

        this.setLayout(new BorderLayout());

        mContentsTable = new JTable(mDefaultTableModel);

        mContentsTable.setRowSelectionAllowed(false);

        mScrollPane = new JScrollPane(mContentsTable,
                                      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(mScrollPane, BorderLayout.CENTER);
        add(mItemPreview, BorderLayout.WEST);
        add(mDropTargetLabel, BorderLayout.SOUTH);

        mDirectoryMouseListener = new DirectoryMouseListener(desktopKernel, this, appLauncher);

        mContentsTable.addMouseListener(mDirectoryMouseListener);
        mContentsTable.addMouseMotionListener(new DirectoryMouseMotionListener(desktopKernel, this));
        setDirectory(new File("C:\\"));

    }

    protected DraggedItem getCurrentlyDraggedItem() {
        return mCurrentlyDraggedItem;
    }

    protected void setCurrentlyDraggedItem(DraggedItem currentlyDraggedItem) {
        mCurrentlyDraggedItem = currentlyDraggedItem;
    }

    /**
     * Method getContentsTable
     *
     *
     * @return
     *
     */
    public JTable getContentsTable() {
        return mContentsTable;
    }

    /**
     * Method setContentsTable
     *
     *
     * @param contentsTable
     *
     */
    public void setContentsTable(JTable contentsTable) {
        this.mContentsTable = contentsTable;
    }

    /**
     * Method getItemPreview
     *
     *
     * @return
     *
     */
    public ItemPreview getItemPreview() {
        return mItemPreview;
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
        mFrimble = frimble;
    }

    /**
     * Method getPreferredSize
     *
     *
     * @return
     *
     */
    public Dimension getPreferredSize() {
        return new Dimension(400, 100);
    }

    /**
     * Method setParentDirectory
     *
     *
     */
    protected void setParentDirectory() {

        File parentDir = mDirectoryTableModel.getDirectory().getParentFile();

        if (parentDir != null) {
            setDirectory(parentDir);
        }
    }

    /**
     * Method setDirectory
     *
     *
     * @param directory
     *
     */
    protected void setDirectory(File directory) {

        mDirectoryTableModel = new DirectoryTableModel(directory);

        mDirectoryMouseListener.setDirectoryTableModel(mDirectoryTableModel);
        mContentsTable.setModel(mDirectoryTableModel);
        mContentsTable.setDefaultRenderer(Object.class,
                                          new DirectoryTableCellRenderer(imageRepository));
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

        Point p2 = (Point) pt.clone();
        java.awt.Component comp = mFrimble.getLayeredPane();

        SwingUtilities.convertPointFromScreen(p2, comp);

        if (cl == File.class) {
            if ((0 <= p2.x) && (p2.x <= comp.getWidth())) {
                if ((0 <= p2.y) && (p2.y <= comp.getHeight())) {
                    mDropTargetLabel.setText("I recognize dragged item, and it's over me");

                    return true;
                }
            }

            mDropTargetLabel.setText("I recognize dragged item, but it's not over me");

            return false;
        }

        mDropTargetLabel.setText("I do not recognize dragged item");

        return false;
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

        if (cl == File.class) {
            mDropTargetLabel.setText("I recognize dragged item, but it's over another");
        } else {
            mDropTargetLabel.setText("I do not recognize dragged item");
        }
    }

    /**
     * Method draggingStopped
     *
     *
     */
    public void draggingStopped() {
        mDropTargetLabel.setText("No item being dragged");
    }
}
