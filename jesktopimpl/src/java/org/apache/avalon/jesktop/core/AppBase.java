/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.jesktop.core;



import org.apache.avalon.framework.configuration.*;

import org.xml.sax.SAXException;
import org.jesktop.api.JesktopPackagingException;

import java.net.URLClassLoader;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;




/**
 * Class AppBase
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public abstract class AppBase {

    protected static DefaultConfigurationBuilder CONF_BUILDER = null;
    protected File mBaseDir;

    static {
        CONF_BUILDER = new DefaultConfigurationBuilder();
    }

    protected AppBase(File baseDir) {
        mBaseDir = baseDir;
    }
    
    protected Configuration getApplicationsDotXML(final String jarName, final URLClassLoader urlClassLoader)
            throws JesktopPackagingException {

        try {
            URL url = urlClassLoader.getResource("JESKTOP-INF/applications.xml");

            if (url == null) {
                throw new JesktopPackagingException("missing JESKTOP-INF/applications.xml in jar " + jarName);
            }

            Configuration appJarApplicationsConf = CONF_BUILDER.build(url.openStream());

            return appJarApplicationsConf;
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ConfigurationException ce) {
            ce.printStackTrace();
        }

        return null;
    }

    protected String internJar(final boolean temporary, final InputStream inputStream, final String prefix,
                               JarSuffixHolder suffix) {

        String DIR = "ApplicationStore";

        new File(mBaseDir,DIR).mkdir();

        if (temporary) {
            DIR = DIR + File.separator + "Temp" + File.separator + "UninstalledApps";
        } else {
            DIR = DIR + File.separator + "InstalledApps";
        }

        String jarName = DIR + File.separator + prefix + "_Jar" + suffix.getNewSuffix();

        try {
            File outputFile = new File(mBaseDir,jarName);
            
            outputFile.getParentFile().mkdirs();

            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            byte[] block = new byte[2048];
            int bytes = bis.read(block);

            while (bytes != -1) {
                bos.write(block, 0, bytes);

                bytes = bis.read(block);
            }

            bis.close();
            bos.close();
            fos.close();
            inputStream.close();

            return mBaseDir.getAbsolutePath() + File.separator + jarName;
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return null;
    }

    // this can be used for full installations,
    // or in Temp for uninstalled launches.

    /**
     * Class JarSuffixHolder
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.1 $
     */
    protected class JarSuffixHolder {

        private int val;

        protected JarSuffixHolder() {
            val = 1;
        }

        protected String getNewSuffix() {

            String str = "0000" + val++;

            return str.substring(str.length() - 5, str.length());
        }
    }
}