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

import java.util.Vector;


/**
 * Class DecoratorLaunchableTargetImpl
 *
 *
 * @author  Dec 2000.
 * @version $Revision: 1.3 $
 */
public abstract class ConfigurableLaunchableTargetImpl extends LaunchableTargetImpl {

    private String configPath;

    protected ConfigurableLaunchableTargetImpl(final String targetName, final String className,
                                               final String displayName, final String configPath) {

        super(targetName, className, displayName, true);

        this.configPath = configPath;
    }

    protected ConfigurableLaunchableTargetImpl(final String targetName, final String className,
                                               final String appFilePrefix, final Vector jarFileNames,
                                               final String displayName, final String configPath) {

        super(targetName, className, appFilePrefix, jarFileNames, displayName, true);

        this.configPath = configPath;
    }

    /**
     * Method getConfigPath
     *
     *
     * @return
     *
     */
    public String getConfigPath() {
        return configPath;
    }
}
