package net.sourceforge.jesktopimpl.boot;

import net.sourceforge.jesktopimpl.core.ConfigManagerImpl;
import net.sourceforge.jesktopimpl.core.DefaultObjectRepository;
import net.sourceforge.jesktopimpl.core.DesktopKernelImpl;
import net.sourceforge.jesktopimpl.core.ImageRepositoryImpl;
import net.sourceforge.jesktopimpl.core.LaunchableTargetFactoryImpl;
import net.sourceforge.jesktopimpl.core.NotReallyAThreadPool;
import org.jesktop.services.KernelConfigManager;
import net.sourceforge.jesktopimpl.services.LaunchableTargetFactory;
import net.sourceforge.jesktopimpl.windowmanagers.windoze.WindozeWindowManager1;
import org.jesktop.ImageRepository;
import org.jesktop.ObjectRepository;
import org.jesktop.ThreadPool;
import org.jesktop.services.DesktopKernelService;
import org.jesktop.services.WindowManagerService;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

public class Bootstrap {

    public Bootstrap() throws ParserConfigurationException {

        File baseDir = new File(".");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        ObjectRepository or = new DefaultObjectRepository(baseDir);
        LaunchableTargetFactory ltf = new LaunchableTargetFactoryImpl(or);
        KernelConfigManager cm = new ConfigManagerImpl(dbf, ltf, or);
        ImageRepository ir = new ImageRepositoryImpl(or);
        WindowManagerService wm = new WindozeWindowManager1(ir);
        ThreadPool tp = new NotReallyAThreadPool();
        DesktopKernelService kernel = new DesktopKernelImpl(wm, or, tp, cm, ir, ltf, cm, baseDir);

    }

    public static void main(String[] args) throws ParserConfigurationException {
        new Bootstrap();
    }

}
