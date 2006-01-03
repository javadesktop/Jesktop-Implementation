package net.sourceforge.jesktopimpl.core;

import java.io.File;
import java.security.PrivilegedAction;
import java.security.AccessController;

/**
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class RootDirectory extends File {

    public RootDirectory() {
        super(getRootDir());
    }

    private static String getRootDir() {

        ClassLoader foo = RootDirectory.class.getClassLoader();
        if (foo != null) {
            System.out.println("--> 1 " + foo);
            foo = getParentclassLoader(foo);
            if (foo != null) {
                System.out.println("--> 2 " + foo);
                foo = getParentclassLoader(foo);
                if (foo != null) {
                    System.out.println("--> 3 " + foo);
                    foo = getParentclassLoader(foo);
                    if (foo != null) {
                        System.out.println("--> 4 " + foo);
                        foo = getParentclassLoader(foo);
                        if (foo != null) {
                            System.out.println("--> 5 " + foo);
                            foo = getParentclassLoader(foo);
                        }
                    }
                }
            }
        }

        return new File(".").getAbsolutePath();
    }

    private static ClassLoader getParentclassLoader(final ClassLoader foo) {
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return foo.getParent();
            }
        });
    }


}
