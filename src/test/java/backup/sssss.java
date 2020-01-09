package backup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class sssss {
	
	public static void main(String[] args) throws Exception {
		
			String fileName = "!";
			if(fileName.contains("!")){
				String jarName = "D:/backup1/backup-0.0.1-SNAPSHOT.jar";
				System.out.println(jarName);
				JarFile jar = new JarFile(jarName);
				Enumeration<JarEntry> jarent = jar.entries();
				while(jarent.hasMoreElements()){
					ZipEntry ent = jarent.nextElement();
					if(ent.getName().endsWith("Rar.exe")){
						copyFileUsingFileStreams(jar.getInputStream(ent),new File("d:/temp/Rar.exe"));
						break;
					}
				}
			}else{

			}
	}
	
	
	private static void copyFileUsingFileStreams(InputStream source, File dest) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = source;
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

}
