
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
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
     * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a> Dec 2000.
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
