package net.sourceforge.jesktopimpl.core;

import java.io.File;

/**
 * @author Paul Hammant
 * @version $Revision: 1.1 $
 */
public class RootDirectory extends File {

    public RootDirectory() {
        super(new File(".").getAbsolutePath());
    }
}
