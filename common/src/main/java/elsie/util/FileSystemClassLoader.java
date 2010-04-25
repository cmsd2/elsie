package elsie.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class FileSystemClassLoader extends AbstractClassLoader {
	private static final Log log = LogFactory.getLog(FileSystemClassLoader.class);

	private File root;

	public FileSystemClassLoader()
	{
	}
	
	public String getClasses()
	{
		return root.getPath();
	}
	
	public void setClasses(String classesDir)
	{
		root = new File(classesDir);

		if(!(root.exists() && root.isDirectory() && root.canRead())) {
			throw new IllegalArgumentException("Cannot read from plugins directory");
		}
	}

	@Override
	protected URL findResource(String name) {
		File f = new File(root, name);
		
		if(f.exists())
		{
			try {
				return f.toURI().toURL();
			} catch (MalformedURLException e) {
				log.error("Error converting resource name to URL", e);
			}
		}
		
		return null;
	}

	@Override
	protected Class findClass(String name) throws ClassNotFoundException {

		Class c = null;
		String filename = getClassPathFromName(name);

		try {
			byte data[] = loadClassFromFile(root, filename);
			
			log.debug("Found class " + name + " in " + filename);
			
			c = defineClass (name, data, 0, data.length);
		} catch (IOException e) {
			throw new ClassNotFoundException ("Error reading file: " + filename);
		}

		return c;
	}
}
