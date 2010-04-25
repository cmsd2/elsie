package elsie.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LibDirClassLoader extends AbstractClassLoader {
	private static final Log log = LogFactory.getLog(LibDirClassLoader.class);

	private String path;
	private Map<String,String> jarFileMap = new HashMap<String, String> ();
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String libDir)
	{
		this.path = libDir;
	}
	
	public void init()
	{
		findJarFiles();
	}

	/*
	 * look in libDir for jars
	 */
	public void findJarFiles()
	{		
		File libDir = new File(path);
		
		File[] jarFiles = listJars(libDir);
		
		for(int i = 0; i < jarFiles.length; i++)
		{
			String jarName = jarFiles[i].getName();
			
			if(!jarFileMap.containsKey(jarName))
			{
				jarFileMap.put(jarName, getJarFile(jarName));
			}
		}
	}
	
	public static File[] listJars(File dir)
	{
		log.info("finding jars in " + dir);
		return dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && 
					pathname.canRead() && 
					pathname.getPath().endsWith(".jar");
			}
		});
	}
	
	public String getJarFile(String name)
	{
		return (new File(path, name)).getAbsolutePath();
	}
	
	public String getJarFileURL(String path)
	{
		return "jar:file:" + path;
	}

	@Override
	protected Class findClass(String name) throws ClassNotFoundException {
		findJarFiles();
		
		String fileSystemName = getClassPathFromName(name);
		
		for(String jarPath : jarFileMap.values())
		{
			try {
				URL jarURL = new URL(getJarFileURL(jarPath) + "!/" + fileSystemName);

				byte[] data = loadClassFromURL(jarURL);
				
				log.debug("Found class " + name + " in jar " + jarPath);
				
				return defineClass (name, data, 0, data.length);
			} catch (IOException e) {
				log.error("Error reading jar " + jarPath, e);
			}
		}
		
		throw new ClassNotFoundException(name);
	}
	
	@Override
	protected URL findResource(String name) {
		findJarFiles();
		
		for(String jarPath : jarFileMap.values())
		{
			try {
				log.info("looking for resource " + name + " in jar " + jarPath);
				JarFile jarFile = new JarFile(jarPath);

				ZipEntry entry = jarFile.getEntry(name);
				
				if(entry != null)
				{
					return new URL(getJarFileURL(jarPath) + "!/" + name);
				}
			} catch (IOException e) {
				log.error("Error reading jar " + jarPath, e);
			}
		}

		return null;
	}
}
