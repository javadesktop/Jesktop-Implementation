/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.windowmanagers.windoze;

import org.jesktop.launchable.LaunchableTarget;

import javax.swing.JLabel;

/**
     * Class LaunchableTargetLabel
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1.1.1 $
     */
    public class LaunchableTargetLabel extends JLabel {

        private LaunchableTarget ltgt;

        /**
         * Constructor LaunchableTargetLabel
         *
         *
         * @param ltgt
         *
         */
        protected LaunchableTargetLabel(final LaunchableTarget ltgt) {
            this.ltgt = ltgt;
        }
    }
