import org.nanocontainer.script.groovy.NanoGroovyBuilder

builder = new NanoGroovyBuilder()

pico = builder.container(parent:parent,class:ImplementationHidingSoftCompositionPicoContainer) {
  classpathelement(path:"lib/jesktop.jar");
  component(key:"java.io.File", instance:new java.io.File(".").getAbsoluteFile())
  component(key:"javax.xml.parsers.DocumentBuilderFactory", instance:javax.xml.parsers.DocumentBuilderFactory.newInstance())
  component(key:"net.sourceforge.jesktopimpl.services.LaunchableTargetFactory", class:"net.sourceforge.jesktopimpl.core.LaunchableTargetFactoryImpl")
  component(key:org.jesktop.ImageRepository, class:"net.sourceforge.jesktopimpl.core.ImageRepositoryImpl")
  component(key:org.jesktop.ObjectRepository, class:"net.sourceforge.jesktopimpl.core.DefaultObjectRepository")
  component(key:org.jesktop.ThreadPool, class:"net.sourceforge.jesktopimpl.core.NotReallyAThreadPool")
  component(key:org.jesktop.mime.MimeManager, class:"net.sourceforge.jesktopimpl.core.MimeManagerImpl")
  component(key:org.jesktop.services.DesktopKernelService, class:"net.sourceforge.jesktopimpl.core.DesktopKernelImpl")
  component(key:org.jesktop.services.KernelConfigManager, class:"net.sourceforge.jesktopimpl.core.ConfigManagerImpl")
  component(key:org.jesktop.services.WindowManagerService, class:"net.sourceforge.jesktopimpl.windowmanagers.windoze.WindozeWindowManager1")

}
//pico.start();
