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

import org.jesktop.launchable.LaunchableTarget;

import java.io.Serializable;
import java.util.Vector;
import java.util.StringTokenizer;


/**
 * Class LaunchableTarget
 *
 *
 * @author  Dec 2000.
 * @version $Revision: 1.3 $
 */
public abstract class LaunchableTargetImpl implements LaunchableTarget, Comparable, Serializable {

    private String targetName;
    private String className;
    private String displayName;
    private String appFilePrefix;
    private Vector jarFileNames;
    private String[] targetNameArray;
    private boolean singleInstance;

    /**
     * Constructor LaunchableTarget
     *
     *
     * @param name
     * @param className
     *
     */
    protected LaunchableTargetImpl(final String targetName, final String className, final String displayName,
                                   final boolean singleInstance) {
        this(targetName, className, null, null, displayName, singleInstance);
    }

    protected LaunchableTargetImpl(final String targetName, final String className,final  String appFilePrefix,
                                   final Vector jarFileNames, final String displayName,
                                   final boolean singleInstance) {

        this.targetName = targetName;
        this.className = className;
        this.appFilePrefix = appFilePrefix;
        this.jarFileNames = jarFileNames;
        this.displayName = displayName;
        this.singleInstance = singleInstance;

        StringTokenizer st = new StringTokenizer(targetName, "/");
        Vector parts = new Vector();

        while (st.hasMoreTokens()) {
            String part = st.nextToken();

            parts.add(part);
        }

        targetNameArray = new String[parts.size()];

        parts.copyInto(targetNameArray);
    }

    /**
     * Method getTargetName
     *
     *
     * @return
     *
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Method getDisplayName
     *
     *
     * @return
     *
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Method toString
     *
     *
     * @return
     *
     */
    public String toString() {
        return displayName;
    }

    /**
     * Method getTargetNameParts
     *
     *
     * @return
     *
     */
    public String[] getTargetNameParts() {
        return targetNameArray;
    }

    /**
     * Method getClassName
     *
     *
     * @return
     *
     */
    public String getClassName() {
        return className;
    }

    protected String getAppFilePrefix() {
        return appFilePrefix;
    }

    protected Vector getAppJarNames() {
        return jarFileNames;
    }

    /**
     * Method isSingleInstanceApp
     *
     *
     * @return
     *
     */
    public boolean isSingleInstanceApp() {
        return singleInstance;
    }

    /**
     * Method canBeUnInstalled
     *
     *
     * @return
     *
     */
    public boolean canBeUnInstalled() {

        //TODO - over simplifies for the moment.
        return (appFilePrefix != null);
    }

    /**
     * Method compareTo
     *
     *
     * @param o
     *
     * @return
     *
     */
    public int compareTo(Object o) {

        LaunchableTarget other = (LaunchableTarget) o;

        return targetName.compareTo(other.getTargetName());
    }
}
