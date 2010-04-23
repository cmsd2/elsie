package elsie;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.util.HashSet;
import java.util.Hashtable;

public class PluginClassLoader extends ClassLoader {
	private File root;
	private HashSet<String> prefixExceptions;

	public PluginClassLoader(String rootDir, HashSet<String> prefixExceptions) {
		this.prefixExceptions = prefixExceptions;

		if (rootDir == null)
			throw new IllegalArgumentException ("Null root directory");


		root = new File(rootDir);

		if(!(root.exists() && root.isDirectory() && root.canRead())) {
			throw new IllegalArgumentException("Cannot read from plugins directory");
		}
	}
	
	public HashSet<String> getPrefixExceptions()
	{
		return prefixExceptions;
	}

	protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class c = findLoadedClass(name);

		if (c == null) {
			String[] parts = name.split("\\.");
			StringBuilder sb = new StringBuilder();

			if(parts.length == 0)
			{
				throw new ClassNotFoundException("suspicious class name " + name);
			}

			sb.append(parts[0]);

			if(prefixExceptions.contains(sb.toString()))
			{
				System.out.println("prefix " + sb.toString() + " matches. loading " + name + " from parent classloader");
				return super.loadClass(name, resolve);
			}

			for(int i = 1; i < parts.length; i++)
			{
				sb.append('.');
				sb.append(parts[i]);

				if(prefixExceptions.contains(sb.toString()))
				{
					System.out.println("prefix " + sb.toString() + " matches. loading " + name + " from parent classloader");
					return super.loadClass(name, resolve);
				}
			}

			// Convert class name argument to filename
			// Convert package names into subdirectories
			String filename = name.replace ('.', File.separatorChar) + ".class";

			try {
				byte data[] = loadClassData(filename);
				c = defineClass (name, data, 0, data.length);
				if (c == null)
					throw new ClassNotFoundException (name);
			} catch (IOException e) {
				throw new ClassNotFoundException ("Error reading file: " + filename);
			}
		}
		
		System.out.println("loaded " + c.getName());
		
		if(resolve)
			resolveClass(c);

		return c;
	}
	private byte[] loadClassData (String filename) 
	throws IOException {

		// Create a file object relative to directory provided
		File f = new File (root, filename);

		// Get size of class file
		int size = (int)f.length();

		// Reserve space to read
		byte buff[] = new byte[size];

		// Get stream to read from
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream (fis);

		// Read in data
		dis.readFully (buff);

		// close stream
		dis.close();

		// return data
		return buff;
	}

}
