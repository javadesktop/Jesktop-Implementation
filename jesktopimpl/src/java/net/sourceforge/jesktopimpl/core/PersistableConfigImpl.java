/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */

package net.sourceforge.jesktopimpl.core;

import org.jesktop.ObjectRepository;

/**
 * Class PersistableConfigImpl
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.2 $
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
