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

import org.jesktop.launchable.NormalLaunchableTarget;
import org.jesktop.launchable.LaunchableTarget;

import java.util.Vector;


/**
 * Class KernelLaunchableTargetImpl
 *
 *
 * @author  Jul 2001.
 * @version $Revision: 1.1 $
 */
public class KernelLaunchableTargetImpl extends NormalLaunchableTargetImpl
        implements LaunchableTarget {

    protected KernelLaunchableTargetImpl(final String targetName, final String className, final String displayName,
                                         final boolean singleInst) {
        super(targetName, className, displayName, singleInst);
    }

    protected KernelLaunchableTargetImpl(final String targetName, final String className,
                                         final String appFilePrefix, final Vector jarFileNames,
                                         final String displayName, final boolean singleInst) {
        super(targetName, className, appFilePrefix, jarFileNames, displayName, singleInst);
    }
}