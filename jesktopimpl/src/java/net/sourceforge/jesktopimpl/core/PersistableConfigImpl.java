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

import org.jesktop.ObjectRepository;

/**
 * Class PersistableConfigImpl
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.4 $
 */
public class PersistableConfigImpl implements org.jesktop.config.PersistableConfig {

    private ObjectRepository mObjectRepository;
    private String mPrefix;

    protected PersistableConfigImpl(final ObjectRepository rep, final String prefix) {
        this.mObjectRepository = rep;
        this.mPrefix = prefix + "-";
    }

    /**
     * Method put
     *
     *
     * @param key
     * @param obj
     *
     */
    public void put(final String key, final Object obj) {
        mObjectRepository.put(mPrefix + key, obj);
    }

    /**
     * Method get
     *
     *
     * @param key
     *
     * @return
     *
     */
    public Object get(final String key) {

        if (mObjectRepository.containsKey(mPrefix + key)) {
            return mObjectRepository.get(mPrefix + key);
        } else {
            return null;
        }
    }
}
