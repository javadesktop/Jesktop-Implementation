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
 */
package net.sourceforge.jesktopimpl.boot;

import net.sourceforge.jesktopimpl.core.ConfigManagerImpl;
import net.sourceforge.jesktopimpl.core.DefaultObjectRepository;
import net.sourceforge.jesktopimpl.core.DesktopKernelImpl;
import net.sourceforge.jesktopimpl.core.ImageRepositoryImpl;
import net.sourceforge.jesktopimpl.core.LaunchableTargetFactoryImpl;
import net.sourceforge.jesktopimpl.core.NotReallyAThreadPool;
import net.sourceforge.jesktopimpl.core.MimeManagerImpl;
import org.jesktop.services.KernelConfigManager;
import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;
import net.sourceforge.jesktopimpl.windowmanagers.windoze.WindozeWindowManager1;
import org.jesktop.ImageRepository;
import org.jesktop.ObjectRepository;
import org.jesktop.ThreadPool;
import org.jesktop.mime.MimeManager;
import org.jesktop.services.DesktopKernelService;
import org.jesktop.services.WindowManagerService;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

public class Bootstrap {

    public Bootstrap() throws ParserConfigurationException {

        File baseDir = new File(".").getAbsoluteFile();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        ObjectRepository or = new DefaultObjectRepository(baseDir);
        LaunchableTargetFactory ltf = new LaunchableTargetFactoryImpl(or);
        KernelConfigManager cm = new ConfigManagerImpl(dbf, ltf, or);
        ImageRepository ir = new ImageRepositoryImpl(or);
        WindowManagerService wm = new WindozeWindowManager1(ir);
        ThreadPool tp = new NotReallyAThreadPool();
        MimeManager mm = new MimeManagerImpl(or,ltf);
        DesktopKernelService kernel = new DesktopKernelImpl(wm, or, tp, cm, ir, ltf, cm, mm, dbf, baseDir);

    }

    public static void main(String[] args) throws ParserConfigurationException {
        new Bootstrap();
    }

}
