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

import org.xml.sax.SAXException;
import org.jesktop.JesktopPackagingException;
import org.jesktop.JesktopPackagingException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.ConfigurationException;

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
 * @version $Revision: 1.5 $
 */
public abstract class AppBase {

    protected static DefaultConfigurationBuilder CONF_BUILDER = null;
    protected File baseDir;

    static {
        CONF_BUILDER = new DefaultConfigurationBuilder();
    }

    protected AppBase(File baseDir) {
        this.baseDir = baseDir;
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

        new File(baseDir,DIR).mkdir();

        if (temporary) {
            DIR = DIR + File.separator + "Temp" + File.separator + "UninstalledApps";
        } else {
            DIR = DIR + File.separator + "InstalledApps";
        }

        String jarName = DIR + File.separator + prefix + "_Jar" + suffix.getNewSuffix();

        try {
            File outputFile = new File(baseDir,jarName);

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

            return baseDir.getAbsolutePath() + File.separator + jarName;

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
     * @version $Revision: 1.5 $
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
