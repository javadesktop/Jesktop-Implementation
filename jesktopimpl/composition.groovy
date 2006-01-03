
pico = builder.container(parent:parent, class:org.nanocontainer.reflection.DefaultNanoPicoContainer, parentClassLoader:"org.picocontainer.PicoContainer") {
  classPathElement(path:"lib/api/jesktop-api.jar")
  classPathElement(path:"lib/api/jesktop-frimble.jar")
  classLoader {
    classPathElement(path:"lib/impl/jesktop-impl.jar") {
      grant(new java.security.AllPermission())
    }
    classPathElement(path:"lib/hidden/nanocontainer-1.0-RC-3.jar")
    component(key:java.io.File, class:"net.sourceforge.jesktopimpl.core.RootDirectory")
    component(key:javax.xml.parsers.DocumentBuilderFactory, instance:javax.xml.parsers.DocumentBuilderFactory.newInstance())
    component(classNameKey:"net.sourceforge.jesktopimpl.services.LaunchableTargetFactory", class:"net.sourceforge.jesktopimpl.core.LaunchableTargetFactoryImpl")
    component(classNameKey:"org.jesktop.ImageRepository", class:"net.sourceforge.jesktopimpl.core.ImageRepositoryImpl")
    component(classNameKey:"org.jesktop.ObjectRepository", class:"net.sourceforge.jesktopimpl.core.DefaultObjectRepository")
    component(classNameKey:"org.jesktop.ThreadPool", class:"net.sourceforge.jesktopimpl.core.NotReallyAThreadPool")
    component(classNameKey:"org.jesktop.mime.MimeManager", class:"net.sourceforge.jesktopimpl.core.MimeManagerImpl")
    component(classNameKey:"org.jesktop.services.DesktopKernelService", class:"net.sourceforge.jesktopimpl.core.DesktopKernelImpl")
    component(classNameKey:"org.jesktop.services.KernelConfigManager", class:"net.sourceforge.jesktopimpl.core.ConfigManagerImpl")
    component(classNameKey:"org.jesktop.services.WindowManagerService", class:"net.sourceforge.jesktopimpl.windowmanagers.windoze.WindozeWindowManager1")
  }
}
//pico.start();
