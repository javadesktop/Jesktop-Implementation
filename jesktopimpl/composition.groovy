pico = new org.picocontainer.defaults.DefaultPicoContainer()

pico.registerComponentInstance(new java.io.File(".").getAbsoluteFile())
pico.registerComponentInstance(javax.xml.parsers.DocumentBuilderFactory.newInstance())
pico.registerComponentImplementation(net.sourceforge.jesktopimpl.core.DefaultObjectRepository)
pico.registerComponentImplementation(net.sourceforge.jesktopimpl.core.LaunchableTargetFactoryImpl)
pico.registerComponentImplementation(net.sourceforge.jesktopimpl.core.ConfigManagerImpl)
pico.registerComponentImplementation(net.sourceforge.jesktopimpl.core.ImageRepositoryImpl)
pico.registerComponentImplementation(net.sourceforge.jesktopimpl.windowmanagers.windoze.WindozeWindowManager1)
pico.registerComponentImplementation(net.sourceforge.jesktopimpl.core.NotReallyAThreadPool)
pico.registerComponentImplementation(net.sourceforge.jesktopimpl.core.DesktopKernelImpl)

pico.getComponentInstances();