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

import org.jesktop.launchable.LaunchableTarget;

import javax.swing.JLabel;

/**
     * Class LaunchableTargetLabel
     *
     *
     * @author Paul Hammant
     * @version $Revision: 1.4 $
     */
    public class LaunchableTargetLabel extends JLabel {

        private LaunchableTarget launchableTarget;

        /**
         * Constructor LaunchableTargetLabel
         *
         *
         * @param ltgt
         *
         */
        protected LaunchableTargetLabel(final LaunchableTarget ltgt) {
            this.launchableTarget = ltgt;
        }
    }
