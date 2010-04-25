package elsie.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractClassLoader extends ClassLoader {

	@Override
	protected URL findResource(String name) {
		return super.findResource(name);
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		return super.findResources(name);
	}

	@Override
	public URL getResource(String name) {
		URL url = findResource(name);
		
		if(url == null)
		{
			url = super.findResource(name);
		}
		
		return url;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		Enumeration<URL> urls = findResources(name);
		
		if(urls == null || !urls.hasMoreElements())
		{
			urls = super.findResources(name);
		}
		
		return urls;
	}

	private static final Log log = LogFactory.getLog(AbstractClassLoader.class);

	private Set<String> prefixExceptions = new HashSet<String>();
	
	public Set<String> getPrefixExceptions()
	{
		return this.prefixExceptions;
	}
	
	public void setPrefixExceptions(Set<String> prefixExceptions)
	{
		this.prefixExceptions = prefixExceptions;
	}

	protected abstract Class findClass(String name) throws ClassNotFoundException;

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		
		Class c = findLoadedClass(name);
		
		if (c != null)
		{
			return c;
		}
		
		String prefix = matchPrefix(name);
		
		if(prefix != null)
		{
			log.debug("Loading class " + name + " from parent classloader");
			return super.loadClass(name, resolve);
		}

		c = findClass(name);
		
		if(resolve)
			resolveClass(c);
		
		return c;
	}
	
	protected String matchPrefix(String name) throws ClassNotFoundException
	{
		String[] parts = name.split("\\.");
		StringBuilder sb = new StringBuilder();

		if(parts.length == 0)
		{
			throw new ClassNotFoundException("suspicious class name " + name);
		}

		sb.append(parts[0]);
		
		if(prefixExceptions.contains(sb.toString()))
		{
			return sb.toString();
		}

		for(int i = 1; i < parts.length; i++)
		{
			sb.append('.');
			sb.append(parts[i]);

			if(prefixExceptions.contains(sb.toString()))
			{
				return sb.toString();
			}
		}

		return null;
	}

	protected byte[] loadClassFromFile (File root, String filename) 
	throws IOException {

		// Create a file object relative to directory provided
		File f = new File (root, filename);

		// Get size of class file
		int size = (int)f.length();
		
		FileInputStream fis = new FileInputStream(f);

		return loadClassFromStream(fis, size);
	}
	
	protected byte[] loadClassFromURL (URL url) throws IOException
	{
		URLConnection conn = url.openConnection();
		
		InputStream in = conn.getInputStream();
		
		return loadClassFromStream(in, 0);
	}
	
	public String getClassPathFromName(String name)
	{
		return name.replace ('.', File.separatorChar) + ".class";
	}
	
	protected byte[] loadClassFromJar(JarFile jarFile, String fileSystemName) throws IOException, ClassNotFoundException
	{
		ZipEntry je = jarFile.getEntry(fileSystemName);
		
		if(je == null)
		{
			throw new ClassNotFoundException("can't find " + fileSystemName + " in jar " + jarFile);
		}
		
		InputStream in = jarFile.getInputStream(je);
		
		if(je.getSize() > (long) Integer.MAX_VALUE)
		{
			throw new RuntimeException("class file is larger than we can handle");
		}

		int size = (int) je.getSize();
		return loadClassFromStream(in, size);
	}
	
	protected byte[] loadClassFromStream(InputStream in, int lengthHint) throws IOException
	{
		if(lengthHint == 0)
		{
			lengthHint = 4096;
		}

		int size = 0;
		int off = 0;
		int len = lengthHint;
		byte buff[] = new byte[lengthHint];
		
		DataInputStream dis = new DataInputStream (in);

		try {
			int count = dis.read(buff, off, len);
			while(count > 0)
			{
				size += count;
				buff = Arrays.copyOf(buff, lengthHint * 2);
				off = off + count;
				len = buff.length - off;
				
				count = dis.read(buff, off, len);
			}
		} finally {
			dis.close();
		}
		
		buff = Arrays.copyOf(buff, size);

		return buff;
	}
}
