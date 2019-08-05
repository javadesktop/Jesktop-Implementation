# Jesktop Implementation

Historical work I did on SourceForge while unemployed after 9/11: An attempt to make a desktop app using J2SE 1.4 
and Swing. This was the implementation of the [API](https://github.com/javadesktop/Jesktop-API) that attempted 
to hide the implementation kernel in a classloader tree.  I first did the hierarchy with Apache's Avalon project
(the first Inversion of Control framework), but that project had infighting, and was cancelled. Luckily I'd 
started PicoContainer (the first constructor injection container) and migrated it to that. Years active 2002 - 2004.

See also the [Jesktop Applications](https://github.com/javadesktop/Jesktop-Implementation) repo.

# Conversion from SourceForge CVS.

I tried a few things that didn't work, but this is what I doing: Two stages using Subversion temporarily:

```
rsync -a a.cvs.sourceforge.net::cvsroot/jesktopimpl jesktopimpl
mkdir Svn_conv
cd Svn_conv/
svnadmin create foo
cvs2svn --existing-svnrepos -s foo /path/to/jesktopimpl
git svn --ignore-paths CVSROOT --stdlayout --no-metadata clone file:///path/to/jesktopimpl/Svn_conv/foo bar 
cd bar/
git remote add origin git@github.com:javadesktop/Jesktop-Implementation.git
git push -u origin master
```
