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
package net.sourceforge.jesktopimpl.core;

import org.jesktop.LaunchedTarget;
import org.jesktop.LaunchedTarget;


/**
 * Class LaunchedTarget
 *
 *
 * @author  Dec 2000.
 * @version $Revision: 1.4 $
 */
public abstract class LaunchedTargetImpl implements LaunchedTarget {

    private String targetName;
    private String displayName;

    /**
     * Constructor LaunchedTarget
     *
     *
     *
     * @param targetName
     * @param dislayName
     *
     */
    public LaunchedTargetImpl(final String targetName, final String dislayName) {
        this.targetName = targetName;
        this.displayName = displayName;
    }

    /**
     * Method getName
     *
     *
     * @return
     *
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Method getDisplaytName
     *
     *
     * @return
     *
     */
    public String getDisplaytName() {
        return displayName;
    }
}
