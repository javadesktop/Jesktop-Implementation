/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.apps.notepad;

import org.jesktop.appsupport.ContentViewer;
import org.jesktop.frimble.FrimbleAware;
import org.jesktop.frimble.Frimble;

import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * Class TextViewer
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TextViewer extends JPanel implements ContentViewer, FrimbleAware, ActionListener {

    private Frimble frimble;
    JMenuItem openMi = new JMenuItem("Open");
    JMenuItem exitMi = new JMenuItem("Exit");
    StringBuffer sb = new StringBuffer();
    JTextArea textArea;
    boolean doThumbNail = false;
    Font smallFont = new Font("Helvetica", Font.PLAIN, 6);

    /**
     * Constructor TextViewer
     *
     *
     */
    public TextViewer() {}

    /**
     * Method setFrimble
     *
     *
     * @param frimble
     *
     */
    public void setFrimble(Frimble frimble) {

        setPreferredSize(new Dimension(400, 100));

        this.frimble = frimble;

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("File");

        mb.add(menu);
        menu.add(openMi);
        menu.add(exitMi);
        openMi.addActionListener(this);
        exitMi.addActionListener(this);
        frimble.setJMenuBar(mb);
        this.setLayout(new BorderLayout());

        textArea = new JTextArea();

        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        add(scrollPane, BorderLayout.CENTER);
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

        try {
            fillTextArea(new LineNumberReader(new InputStreamReader(url.openStream())));

            if (thumbNail) {
                doThumbNail = true;
            } else {
                textArea.setText(sb.toString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void fillTextArea(LineNumberReader lnr) {

        try {
            String line = lnr.readLine();

            while (line != null) {
                sb.append(line + "\n");

                line = lnr.readLine();
            }
        } catch (IOException ioe) {
            sb.append("<IOE reading lines>");
        }
    }

    /**
     * Method paintComponent
     *
     *
     * @param g
     *
     */
    public void paintComponent(Graphics g) {

        if (doThumbNail) {
            String str = sb.toString().trim().substring(0, 10);
            Font origFont = g.getFont();

            g.setFont(smallFont);
            g.drawString(str, 10, 10);
            g.setFont(origFont);
        } else {
            super.paintComponent(g);
        }
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

                fc.setFileFilter(new PlainTextFilter());

                int returnVal = fc.showOpenDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    viewContent(file.toURL(), false);
                }
            } else if (ae.getSource() == exitMi) {
                frimble.dispose();
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }
    }

    /**
     * Class PlainTextFilter
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1 $
     */
    private class PlainTextFilter extends javax.swing.filechooser.FileFilter {

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
            return (f.getName().toLowerCase().endsWith(".txt"));
        }

        /**
         * Method getDescription
         *
         *
         * @return
         *
         */
        public String getDescription() {
            return "Text files";
        }
    }
}
