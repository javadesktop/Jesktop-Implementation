/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.apps.imageviewer;

import org.jesktop.appsupport.ContentViewer;
import org.jesktop.frimble.FrimbleAware;
import org.jesktop.frimble.Frimble;
import org.jesktop.frimble.JFrameFrimble;


import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Component;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;

/**
 * Class ImageViewer allows the viewing of GIFs and JPGs.  To be replaced by a beefier app later.
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a> Dec 2000.
 * @version V1.0
 */
public class ImageViewer extends JPanel implements ContentViewer, FrimbleAware, ActionListener {

    private Frimble frimble;
    JMenuItem openMi = new JMenuItem("Open");
    JMenuItem exitMi = new JMenuItem("Exit");
    JScrollPane scrollPane;
    ImageIcon image;
    int height = 0;
    int width = 0;

    //boolean          doThumbNail=false;

    /**
     * Constructor ImageViewer
     *
     *
     */
    public ImageViewer() {

        this.setLayout(new BorderLayout());

        scrollPane = new JScrollPane(new JLabel("?"));

        add(scrollPane, BorderLayout.CENTER);
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

        setPreferredSize(new Dimension(400, 300));

        this.frimble = frimble;

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("File");

        mb.add(menu);
        menu.add(openMi);
        menu.add(exitMi);
        openMi.addActionListener(this);
        exitMi.addActionListener(this);
        frimble.setJMenuBar(mb);
    }

    /**
     * Method viewContent
     *
     *
     * @param url
     * @param thumbNail
     *
     */
    public void viewContent(URL url, boolean thumbNail) {

        image = new ImageIcon(url);
        width = image.getIconWidth();
        height = image.getIconHeight();

        if (thumbNail) {
            scrollPane.setViewportView(makeThumbNail(image, (Component) this.getParent()));
        } else {
            scrollPane.setViewportView(new FullImagePanel(image));
        }

        repaint();
    }

    /**
     * Method actionPerformed
     *
     *
     * @param ae
     *
     */
    public void actionPerformed(ActionEvent ae) {

        try {
            if (ae.getSource() == openMi) {
                JFileChooser fc = new JFileChooser();

                fc.setFileFilter(new ImageFilter());

                int returnVal = fc.showOpenDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    viewContent(file.toURL(), false);
                }
            } else {
                if (ae.getSource() == exitMi) {
                    frimble.dispose();
                }
            }
        } catch (MalformedURLException mue) {}
    }

    private ThumbNailImagePanel makeThumbNail(ImageIcon ii, Component inHere) {

        Image image = ii.getImage().getScaledInstance(inHere.getWidth(), inHere.getHeight(),
                                                      Image.SCALE_DEFAULT);

        return new ThumbNailImagePanel(new ImageIcon(image));
    }

    /**
     * Class ThumbNailImagePanel
     *
     *
     * @author
     * @version %I%, %G%
     */
    private class ThumbNailImagePanel extends JLabel {

        private ThumbNailImagePanel(ImageIcon ii) {
            super(ii);
        }

        /**
         * Method getMinimumSize
         *
         *
         * @return
         *
         */
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        /**
         * Method getPreferredSize
         *
         *
         * @return
         *
         */
        public Dimension getPreferredSize() {
            return new Dimension(20, 20);
        }
    }

    /**
     * Class FullImagePanel
     *
     *
     * @author
     * @version %I%, %G%
     */
    private class FullImagePanel extends JLabel {

        private FullImagePanel(ImageIcon ii) {
            super(ii);
        }

        /**
         * Method getMinimumSize
         *
         *
         * @return
         *
         */
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        /**
         * Method getPreferredSize
         *
         *
         * @return
         *
         */
        public Dimension getPreferredSize() {
            return new Dimension(width, height);
        }
    }

    /**
     * Class ImageFilter
     *
     *
     * @author
     * @version %I%, %G%
     */
    private class ImageFilter extends javax.swing.filechooser.FileFilter {

        /**
         * Method accept
         *
         *
         * @param f
         *
         * @return
         *
         */
        public boolean accept(File f) {
            return (f.getName().toLowerCase().endsWith(".jpg")
                    | f.getName().toLowerCase().endsWith(".gif"));
        }

        /**
         * Method getDescription
         *
         *
         * @return
         *
         */
        public String getDescription() {
            return "Image files";
        }
    }

    /**
     * Method main
     *
     *
     * @param args
     *
     */
    public static void main(String[] args) {

        JFrame frame = new JFrame("ImageViewer");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        Frimble frimble = JFrameFrimble.createJFrameFrimble(frame);
        ImageViewer iv = new ImageViewer();

        frame.getContentPane().add(iv, BorderLayout.CENTER);
        iv.setFrimble(frimble);
        frame.pack();
        frame.setVisible(true);
    }
}
