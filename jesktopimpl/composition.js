var pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory())

pico.registerComponentInstance(new File("."))
pico.registerComponentInstance(Packages.javax.xml.parsers.DocumentBuilderFactory.newInstance())
pico.registerComponentImplementation(Packages.net.sourceforge.jesktopimpl.core.DefaultObjectRepository)
pico.registerComponentImplementation(Packages.net.sourceforge.jesktopimpl.core.LaunchableTargetFactoryImpl)
pico.registerComponentImplementation(Packages.net.sourceforge.jesktopimpl.core.ConfigManagerImpl)
pico.registerComponentImplementation(Packages.net.sourceforge.jesktopimpl.core.ImageRepositoryImpl)
pico.registerComponentImplementation(Packages.net.sourceforge.jesktopimpl.windowmanagers.windoze.WindozeWindowManager1)
pico.registerComponentImplementation(Packages.net.sourceforge.jesktopimpl.core.ThreadPoolImpl)
pico.registerComponentImplementation(Packages.net.sourceforge.jesktopimpl.core.DesktopKernelImpl)

pico.start();