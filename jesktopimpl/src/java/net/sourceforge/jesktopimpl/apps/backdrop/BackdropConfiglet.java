/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package net.sourceforge.jesktopimpl.apps.backdrop;

import org.jesktop.config.XMLConfiglet;
import org.jesktop.config.ConfigManager;
import org.jesktop.config.ConfigHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.beans.PropertyChangeEvent;


/**
 * Class BackdropConfiglet allows the setting of the backdrop for the desktop.
 *
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a> July 2001.
 * @version %I%, %G%
 */
public class BackdropConfiglet extends JPanel implements XMLConfiglet {

    private final JTextField backdropLocn = new JTextField();
    private final JButton browseBtn = new JButton("Browse..");
    private static final String[] TYPES = new String[]{ "Tiled", "Stretched" };
    private final JComboBox typeBox = new JComboBox(TYPES);
    private Document cfgDoc;
    private ConfigManager configManager;

    /**
     * Constructor BackdropConfiglet
     *
     *
     */
    public BackdropConfiglet(ConfigManager configManager) {
        this.configManager = configManager;

        this.setLayout(new BorderLayout());
        this.add(backdropLocn, BorderLayout.CENTER);
        this.add(browseBtn, BorderLayout.EAST);
        this.add(typeBox, BorderLayout.SOUTH);

        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                updateBackdrop(backdropLocn.getText());
            }
        };

        backdropLocn.addActionListener(al);
        typeBox.addActionListener(al);
        browseBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                try {
                    JFileChooser fc = new JFileChooser();

                    fc.setFileFilter(new ImageFilter());

                    int returnVal = fc.showOpenDialog(BackdropConfiglet.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();

                        backdropLocn.setText(file.toURL().toString());
                        updateBackdrop(file.toURL().toString());
                    }
                } catch (MalformedURLException mue) {
                    mue.printStackTrace();
                }
            }
        });
    }

    private void updateBackdrop(String locn) {

        Element root = cfgDoc.getDocumentElement();
        NodeList nodelist = root.getElementsByTagName("backdrop");
        Element bd = null;

        if (nodelist.getLength() == 0) {
            bd = cfgDoc.createElement("backdrop");

            root.appendChild(bd);
        } else {
            bd = (Element) nodelist.item(0);
        }

        bd.appendChild(cfgDoc.createTextNode(locn));

        String ty = ((String) typeBox.getSelectedItem()).toLowerCase();

        bd.setAttribute("type", ty);
        configManager.notifyUpdated(BackdropConfiglet.this);
    }

    /**
     * Method getConfig
     *
     *
     * @return
     *
     */
    public Document getConfig() {
        return cfgDoc;
    }

    /**
     * Method setConfig
     *
     *
     * @param config
     *
     */
    public void setConfig(Document config) {

        cfgDoc = config;

        Element root = config.getDocumentElement();
        NodeList nodes = root.getElementsByTagName("backdrop");

        if (nodes.getLength() != 0) {
            Element elem = (Element) nodes.item(0);           // the hopefully only backdrop node
            NodeList nodes2 = elem.getChildNodes();           // the "text" node inside that
            String bdfile = nodes2.item(0).getNodeValue();    // the value of the text node
            String type = elem.getAttribute("type");

            for (int i = 0; i < TYPES.length; i++) {
                String st = TYPES[i];

                if (st.toLowerCase().equals(type)) {
                    typeBox.setSelectedIndex(i);
                }
            }

            backdropLocn.setText(bdfile);
        }
    }

    /**
     * Class ImageFilter
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.2 $
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
     * Method propertyChange
     *
     *
     * @param event
     *
     */    
    public void propertyChange( PropertyChangeEvent event ) {
        if (ConfigHelper.isConfigPropChange(event)) {
            setConfig((Document)event.getNewValue());
        }
    }
}
