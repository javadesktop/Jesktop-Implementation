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



import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

import java.awt.BorderLayout;

import java.io.File;

import java.util.Vector;
import java.util.Enumeration;


/**
 * Class DirectoryTree
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a> Dec 2000.
 * @version V1.0
 */
public class DirectoryTree extends JPanel implements TreeSelectionListener {

    private JTree mTree;
    private JScrollPane mScrollPane;
    private DirectoryTreeNode mCurrSelectedTreeNode;
    private DirectoryPanel mDirectoryPanel;

    /**
     * Constructor DirectoryTree
     *
     *
     */
    protected DirectoryTree() {

        this.setLayout(new BorderLayout());

        mCurrSelectedTreeNode = new DirectoryTreeNode(null, new File("c:\\"));
        mTree = new JTree(mCurrSelectedTreeNode);
        mScrollPane = new JScrollPane(mTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        mTree.setRootVisible(true);
        this.add(mScrollPane, BorderLayout.CENTER);
        mTree.addTreeSelectionListener(this);
    }

    /**
     * Method setDirectoryPanel
     *
     *
     * @param dirPanel
     *
     */
    public void setDirectoryPanel(DirectoryPanel dirPanel) {
        this.mDirectoryPanel = dirPanel;
    }

    /**
     * Method valueChanged
     *
     *
     * @param e
     *
     */
    public void valueChanged(TreeSelectionEvent e) {

        DirectoryTreeNode node = (DirectoryTreeNode) mTree.getLastSelectedPathComponent();

        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            mDirectoryPanel.setDirectory(node.getDirectory());
        } else {
            mDirectoryPanel.setDirectory(node.getDirectory());
            mTree.expandPath(e.getPath());
        }
    }
}
