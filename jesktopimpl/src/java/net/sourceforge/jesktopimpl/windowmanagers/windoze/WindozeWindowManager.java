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
package net.sourceforge.jesktopimpl.windowmanagers.windoze;

import net.sourceforge.jesktopimpl.JesktopConstants;
import org.jesktop.DesktopKernel;
import org.jesktop.LaunchedTarget;
import org.jesktop.*;
import org.jesktop.services.WindowManagerService;
import org.jesktop.launchable.LaunchableTarget;
import org.jesktop.config.PersistableConfig;
import org.jesktop.config.ConfigHelper;
import org.jesktop.appsupport.DraggedItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.picocontainer.Startable;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.Box;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * @author Paul Hammant
 * @version 1.0
 *
 * This one like Windows...
 *
 */
public abstract class WindozeWindowManager
        implements PropertyChangeListener, WindowManagerService, Startable {

    protected JPanel bottomBar;
    protected JButton startBtn;
    protected Vector launched = new Vector();
    protected JFrame frame;
    protected JComponent lastDragRep;
    protected LaunchableTarget lastLaunchableTargetDragged;
    protected JLabel lastLaunchableTargetDraggedLabel;
    protected boolean mRenderLaunchableTargetDrag = true;
    protected JLayeredPane layeredPane;
    protected LaunchableTarget[] launchableTargets;
    protected DesktopKernel desktopKernel;
    protected ImageRepository imageRepository;
    private AppLauncher appLauncher;
    protected PersistableConfig persistableConfig;
    protected Rectangle bounds;
    protected Image backgroundImage;
    protected String backgroundType = "tile";

    /**
     * Constructor WindozeWindowManager
     *
     *
     */
    public WindozeWindowManager(ImageRepository imageRepository) {

        this.imageRepository = imageRepository;

        frame = new JFrame("Jesktop: Windows Style Window Manager - v" + JesktopConstants.SOFTWARE_VERSION);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we) {
                closeRequested();
            }
        });

        layeredPane = frame.getLayeredPane();

    }

    public void start() {
        //todo - needed for NanoContainer start?
//        frame.setVisible(true);
    }

    public void stop() {
        //todo - needed for NanoContainer start?

//        frame.setVisible(false);
//        frame.dispose();
    }

    public void setAppLauncher(AppLauncher appLauncher) {
        this.appLauncher = appLauncher;
    }

    /**
     * Method addLaunchedTarget
     *
     *
     * @param launchedTarget
     *
     */
    public void addLaunchedTarget(final LaunchedTarget launchedTarget) {

        LaunchedButton btn = new LaunchedButton(launchedTarget, desktopKernel, imageRepository);

        bottomBar.add(btn);
    }

    /**
     * Method removeLaunchedTarget
     *
     *
     * @param launchedTarget
     *
     */
    public void removeLaunchedTarget(final LaunchedTarget launchedTarget) {

        java.awt.Component[] comps = bottomBar.getComponents();

        for (int f = 0; f < comps.length; f++) {
            if (comps[f] instanceof LaunchedButton) {
                LaunchedButton lchd = (LaunchedButton) comps[f];

                if (lchd.isLaunchedTarget(launchedTarget)) {
                    bottomBar.remove(lchd);
                    bottomBar.repaint();

                    break;
                }
            }
        }
    }

    /**
     * Method close
     *
     *
     */
    public void close() {
        frame.dispose();
    }

    /**
     * Method setKernelCallback
     *
     *
     *
     * @param desktopKernel
     *
     */
    public void setKernelCallback(final DesktopKernel desktopKernel) {

        this.desktopKernel = desktopKernel;
        launchableTargets = desktopKernel.getNormalLaunchableTargets();

        for (int i = 0; i < launchableTargets.length; i++) {
            LaunchableTarget target = launchableTargets[i];
            //System.out.println("--> lt " + target);
        }

        desktopKernel.addPropertyChangeListener(this);

    }

    /**
     * Method renderDragRepresentation
     *
     *
     * @param draggedItem
     * @param pt
     *
     */
    public void renderDragRepresentation(final DraggedItem draggedItem, final Point pt) {

        JComponent dragRep = null;

        if (draggedItem != null) {
            dragRep = draggedItem.getDragRep();
        }

        if (lastDragRep != dragRep) {
            if (lastDragRep != null) {
                lastDragRep.setVisible(false);
                layeredPane.remove(lastDragRep);

                lastDragRep = null;
            }

            if (dragRep != null) {
                lastDragRep = dragRep;

                layeredPane.add(dragRep, JLayeredPane.DRAG_LAYER, 1);
            }
        }

        if (dragRep != null) {
            SwingUtilities.convertPointFromScreen(pt, layeredPane);
            dragRep.setBounds(pt.x, pt.y, (int) dragRep.getPreferredSize().getWidth(),
                              (int) dragRep.getPreferredSize().getHeight());
            dragRep.setVisible(true);
        }
    }

    /**
     * Method closeRequested
     *
     *
     */
    protected void closeRequested() {

        persistableConfig.put("bounds", frame.getBounds());

        try {
            desktopKernel.initiateShutdown(DesktopKernel.SHUTDOWN_SHUTDOWN);
        } catch (JesktopLaunchException jle) {
            jle.printStackTrace();
        }
    }

    /**
     * Method propertyChange
     *
     *
     * @param evt
     *
     */
    public void propertyChange(final PropertyChangeEvent evt) {

        if (evt.getPropertyName().equals(DesktopKernel.LAUNCHABLE_TARGET_CHANGE)) {
            launchableTargets = desktopKernel.getNormalLaunchableTargets();
        }
        if (ConfigHelper.isConfigPropChange(evt)) {
            this.setConfig(ConfigHelper.getConfigPath(evt), (Document) evt.getNewValue());
        }


    }

    /**
     * Method updateComponentTreeUI
     *
     *
     */
    public void updateComponentTreeUI() {
        SwingUtilities.updateComponentTreeUI(frame);
    }







    /**
     * Method setPersistableConfig
     *
     *
     * @param persistableConfig
     *
     */
    public void setPersistableConfig(final PersistableConfig persistableConfig) {
        this.persistableConfig = persistableConfig;
        bounds = (Rectangle) persistableConfig.get("bounds");
    }

    protected void setBackdrop(final String bPath, final String type) {
        try {
            backgroundImage = new ImageIcon(new URL(bPath)).getImage();
            this.backgroundType = type;

            frame.invalidate();

            setBackdrop2(bPath, type);

            frame.repaint();

            //        getRepaintable().repaint();
        } catch (MalformedURLException mufe) {
            mufe.printStackTrace();
        }
    }

    protected abstract void setBackdrop2(final String bPath, final String type);

    protected void paintComponentHelper(final Graphics g, final JComponent repaintable) {

        if (backgroundImage != null) {
            if (backgroundType.equals("tiled")) {

                // this code copied (public domain) from www.afu.com (JavaFaq)
                int w = 0, h = 0;

                while (w < repaintable.getSize().width) {
                    g.drawImage(backgroundImage, w, h, repaintable);

                    while ((h + backgroundImage.getHeight(repaintable)) < repaintable.getSize().height) {
                        h += backgroundImage.getHeight(repaintable);

                        g.drawImage(backgroundImage, w, h, repaintable);
                    }

                    h = 0;
                    w += backgroundImage.getWidth(repaintable);
                }
            } else {
                g.drawImage(backgroundImage, 0, 0, repaintable.getSize().width, repaintable.getSize().height,
                            repaintable);
            }
        }
    }

    /**
     * Method setConfig
     *
     *
     * @param configPath
     * @param config
     *
     */
    public void setConfig(final String configPath, final Document config) {

        if (configPath.equals("desktop/settings")) {

            Element root = config.getDocumentElement();
            NodeList nodes = root.getElementsByTagName("backdrop");

            if (nodes.getLength() != 0) {
                Element elem = (Element) nodes.item(0);           // the hopefully only backdrop node
                NodeList nodes2 = elem.getChildNodes();           // the "text" node inside that

                nodes2.item(0).getAttributes();

                String bdfile = nodes2.item(0).getNodeValue();    // the value of the text node

                setBackdrop(bdfile, elem.getAttribute("type"));
            } else {
            }

        }
    }

    /**
     * Method initializeView
     *
     *
     */
    public final void initializeView() {

        if (SwingUtilities.isEventDispatchThread()) {
            initializeView2();
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    WindozeWindowManager.this.initializeView();
                }
            });
        }
    }

    /**
     * Method initializeView2
     *
     *
     */
    public void initializeView2() {

        if (bounds != null) {
            frame.setBounds(bounds);
        } else {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

            frame.setSize(new Dimension((int) (dim.width * 0.9), (int) (dim.height * 0.9)));
        }

        bottomBar = new JPanel();
        startBtn = new JButton("Start");

        BoxLayout boxLayout = new BoxLayout(bottomBar, BoxLayout.X_AXIS);

        bottomBar.setLayout(boxLayout);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(bottomBar, BorderLayout.SOUTH);
        startBtn.setEnabled(false);
        bottomBar.add(startBtn);
        bottomBar.add(Box.createHorizontalStrut(10));
        startBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                LaunchBar lb = new LaunchBar(desktopKernel, launchableTargets, imageRepository,
                        WindozeWindowManager.this, appLauncher);

                int height = (int) lb.getPreferredSize().getHeight();
                int y = startBtn.getY();
                int x = startBtn.getX() + 3;
                lb.show((java.awt.Component) ae.getSource(), x, (y - height));
            }
        });
        startBtn.setEnabled(true);
    }

    private JLabel getMenuDragRep(final LaunchableTarget lTarget) {

        JLabel lbl = new JLabel(lTarget.getDisplayName(),
                                imageRepository.getAppSmallImageIcon(lTarget.getTargetName()),
                                JLabel.RIGHT);

        lbl.setForeground(Color.black);

        return lbl;
    }

    /**
     * Method renderLaunchableTargetDragRepresentation
     *
     *
     * @param lTarget
     * @param pt
     * @param released
     *
     */
    public void renderLaunchableTargetDragRepresentation(final LaunchableTarget lTarget, final Point pt,
                                                         final boolean released) {

        if (lastLaunchableTargetDragged != lTarget) {
            if (lastLaunchableTargetDragged != null) {
                if (released) {
                    this.acceptLaunchableTarget(lTarget, pt);
                }

                lastLaunchableTargetDraggedLabel.setVisible(false);
                layeredPane.remove(lastLaunchableTargetDraggedLabel);

                lastLaunchableTargetDraggedLabel = null;
                lastLaunchableTargetDragged = null;
            } else {
                lastLaunchableTargetDragged = lTarget;
                lastLaunchableTargetDraggedLabel = getMenuDragRep(lTarget);
            }

            if (lastLaunchableTargetDraggedLabel != null) {
                layeredPane.add(lastLaunchableTargetDraggedLabel, JLayeredPane.DRAG_LAYER, 1);
            }
        }

        if ((lastLaunchableTargetDraggedLabel != null) && mRenderLaunchableTargetDrag) {
            SwingUtilities.convertPointFromScreen(pt, layeredPane);
            lastLaunchableTargetDraggedLabel
                .setBounds(pt.x, pt.y, (int) lastLaunchableTargetDraggedLabel.getPreferredSize()
                    .getWidth(), (int) lastLaunchableTargetDraggedLabel.getPreferredSize()
                    .getHeight());
            lastLaunchableTargetDraggedLabel.setVisible(true);
        }
    }

    protected abstract void acceptLaunchableTarget(final LaunchableTarget lTarget, final Point pt);

}
