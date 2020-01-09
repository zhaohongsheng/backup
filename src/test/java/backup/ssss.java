package backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ssss {
	
public static void main(String[] args) throws Exception {
	
	System.out.println("file:/D:/backup1/backup-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!".split("!")[0]);
	copyFileUsingFileStreams(new File("D:/backup1/backup-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!".split("!")[0]),new File("d:/temp"));
}
private static void copyFileUsingFileStreams(File source, File dest) throws IOException {
	InputStream input = null;
	OutputStream output = null;
	try {
		input = new FileInputStream(source);
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
