/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.builtinapps.installer;

import org.jesktop.frimble.FrimbleAware;
import org.jesktop.frimble.Frimble;
import org.jesktop.api.DesktopKernelAware;
import org.jesktop.api.DesktopKernel;
import org.jesktop.api.JesktopPackagingException;
import org.jesktop.appsupport.DropAware;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.JTextField;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Point;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;


/**
 * Class SimpleInstaller
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
 * @version $Revision: 1.1.1.1 $
 */
public class SimpleInstaller extends JPanel
        implements FrimbleAware, ActionListener, DesktopKernelAware, DropAware {

    protected DesktopKernel desktopKernel;
    protected JButton installButton;
    private String lastWorkingDirectory = null;
    private Frimble frimble;
    private JMenuItem installMi = new JMenuItem("Install");
    private JMenuItem exitMi = new JMenuItem("Exit");

    /**
     * Constructor SimpleInstaller
     *
     *
     */
    public SimpleInstaller() {

        mainButton();
        this.setLayout(new BorderLayout());
        this.add(installButton, BorderLayout.NORTH);
        installButton.addActionListener(this);
        addOtherWidgets();
    }

    /**
     * Method mainButton
     *
     *
     */
    protected void mainButton() {
        installButton = new JButton("Install (Jar file Chooser)");
    }

    /**
     * Method addOtherWidgets
     *
     *
     */
    protected void addOtherWidgets() {

        JPanel pnl = new JPanel();
        BoxLayout bl = new BoxLayout(pnl, BoxLayout.X_AXIS);

        pnl.setLayout(bl);
        pnl.add(new JLabel("Via URL:"));
        Box.createHorizontalStrut(2);

        JTextField urlFld = new JTextField();

        pnl.add(urlFld);
        this.add(pnl, BorderLayout.CENTER);
        urlFld.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                try {
                    URL url = new URL(ae.getActionCommand());

                    try {
                        desktopKernel.runAsychronously(new InstallDoer(url));
                    } catch (Exception e) {    // hmmm, all because workerpool throws it.
                        e.printStackTrace();
                    }
                } catch (MalformedURLException mfue) {
                    mfue.printStackTrace();
                }
            }
        });
        this.add(new JLabel("OR Drop a jar from Explorer on me"), BorderLayout.SOUTH);
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

        this.frimble = frimble;

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("File");

        mb.add(menu);
        menu.add(installMi);
        menu.add(exitMi);
        installMi.addActionListener(this);
        exitMi.addActionListener(this);
        frimble.setJMenuBar(mb);
    }

    // Javadocs will automatically import from interface.

    /**
     * Method setDesktopKernel
     *
     *
     * @param mDesktopKernel
     *
     */
    public void setDesktopKernel(DesktopKernel desktopKernel) {
        this.desktopKernel = desktopKernel;
    }

    // Javadocs will automatically import from interface.

    /**
     * Method actionPerformed
     *
     *
     * @param ae
     *
     */
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == installMi | ae.getSource() == installButton) {
            if (lastWorkingDirectory == null) {
                lastWorkingDirectory = System.getProperty("user.dir");
            }

            JFileChooser fc = new JFileChooser(lastWorkingDirectory);

            fc.setFileFilter(new JarFilter());

            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                lastWorkingDirectory = file.getAbsolutePath();

                try {
                    desktopKernel.runAsychronously(getInstallerDaemon(file));
                } catch (Exception e) {    // hmmm, all because workerpool throws it.
                    e.printStackTrace();
                }
            }
        } else {
            if (ae.getSource() == exitMi) {
                frimble.dispose();
            }
        }
    }

    /**
     * Method approvedJarFile
     *
     *
     * @param f
     *
     * @return
     *
     */
    protected boolean approvedJarFile(File f) {
        return f.getName().toLowerCase().endsWith(".jar");
    }

    /**
     * Method getFilterDescription
     *
     *
     * @return
     *
     */
    protected String getFilterDescription() {
        return "Jar Files";
    }

    /**
     * Method getInstallerDaemon
     *
     *
     * @param file
     *
     * @return
     *
     */
    protected Runnable getInstallerDaemon(File file) {
        return new InstallDoer(file);
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
        java.awt.Component comp = frimble.getLayeredPane();

        SwingUtilities.convertPointFromScreen(p2, comp);

        if (cl == File.class) {
            if ((0 <= p2.x) && (p2.x <= comp.getWidth())) {
                if ((0 <= p2.y) && (p2.y <= comp.getHeight())) {
                    System.err.println("OVER MEEEE");

                    return true;
                }
            }
        }

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

        System.err.println("obj===" + obj);

        try {
            desktopKernel.runAsychronously(getInstallerDaemon((File) obj));
        } catch (Exception e) {    // hmmm, all because workerpool throws it.
            e.printStackTrace();
        }
    }

    /**
     * Method anotherHasRecognizedDraggedItem
     *
     *
     * @param cl
     *
     */
    public void anotherHasRecognizedDraggedItem(Class cl) {}

    /**
     * Method draggingStopped
     *
     *
     */
    public void draggingStopped() {}

    /**
     * Class JarFilter
     *
     *
     * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
     * @version $Revision: 1.1.1.1 $
     */
    private class JarFilter extends javax.swing.filechooser.FileFilter {

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
            return (approvedJarFile(f) | f.isDirectory());
        }

        /**
         * Method getDescription
         *
         *
         * @return
         *
         */
        public String getDescription() {
            return getFilterDescription();
        }
    }

    /**
     * Class InstallDoer
     *
     *
     * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a> Dec 2000.
     * @version $Revision: 1.1.1.1 $
     */
    private class InstallDoer implements Runnable {

        private URL url;

        private InstallDoer(File jarFile) {

            try {
                this.url = jarFile.toURL();
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            }
        }

        private InstallDoer(URL url) {
            this.url = url;
        }

        /**
         * Method run
         *
         *
         */
        public void run() {

            try {
                desktopKernel.getAppInstaller().installApps(url);
            } catch (JesktopPackagingException be) {
                System.err.println("Exception during processing of applications.xml file = "
                                   + be.getMessage());
            }
        }
    }
}
