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



import javax.swing.tree.TreeNode;

import java.io.File;

import java.util.Vector;
import java.util.Enumeration;


/**
     * Class DirectoryTreeNode
     *
     *
     * @author Paul Hammant Dec 2000.
     * @version V1.0%
     */
public class DirectoryTreeNode implements TreeNode {

    private File mDirectory;
    private Vector mDirectories;
    private String[] mListedEntries;
    private TreeNode mParentTreeNode;

    /**
     * Constructor DirectoryTreeNode
     *
     *
     * @param parent
     * @param dir
     *
     */
    protected DirectoryTreeNode(TreeNode parent, File dir) {

        this.mParentTreeNode = parent;
        this.mDirectory = dir;
        mListedEntries = dir.list();

        if (mListedEntries == null) {
            mListedEntries = new String[0];
        }
    }

    /**
     * Method toString
     *
     *
     * @return
     *
     */
    public String toString() {
        return mDirectory.getName();
    }

    /**
     * Method getDirectory
     *
     *
     * @return
     *
     */
    public File getDirectory() {
        return mDirectory;
    }

    /**
     * Method getChildAt
     *
     *
     * @param childIndex
     *
     * @return
     *
     */
    public TreeNode getChildAt(int childIndex) {

        buildIfNeeded();

        return (TreeNode) mDirectories.elementAt(childIndex);
    }

    /**
     * Method getChildCount
     *
     *
     * @return
     *
     */
    public int getChildCount() {
        return dirCount();
    }

    /**
     * Method getParent
     *
     *
     * @return
     *
     */
    public TreeNode getParent() {
        return mParentTreeNode;
    }

    /**
     * Method getIndex
     *
     *
     * @param node
     *
     * @return
     *
     */
    public int getIndex(TreeNode node) {

        int rc = 0;

        for (Enumeration e = mDirectories.elements(); e.hasMoreElements(); ) {
            rc++;

            if (e.nextElement() == node) {
                return rc;
            }
        }

        return -1;
    }

    /**
     * Method getAllowsChildren
     *
     *
     * @return
     *
     */
    public boolean getAllowsChildren() {
        return true;
    }

    /**
     * Method isLeaf
     *
     *
     * @return
     *
     */
    public boolean isLeaf() {
        return (dirCount() == 0);
    }

    /**
     * Method children
     *
     *
     * @return
     *
     */
    public Enumeration children() {

        buildIfNeeded();

        return mDirectories.elements();
    }

    private void buildIfNeeded() {

        if (mDirectories == null) {
            mDirectories = new Vector();

            for (int f = 0; f < mListedEntries.length; f++) {
                File fil = new File(mDirectory, mListedEntries[f]);

                if (fil.isDirectory()) {
                    DirectoryTreeNode tn = new DirectoryTreeNode(this, fil);

                    mDirectories.add(tn);
                }
            }
        }
    }

    private int dirCount() {

        int rc = 0;

        for (int f = 0; f < mListedEntries.length; f++) {
            File fil = new File(mDirectory, mListedEntries[f]);

            if (fil.isDirectory()) {
                rc++;
            }
        }

        return rc;
    }
}
