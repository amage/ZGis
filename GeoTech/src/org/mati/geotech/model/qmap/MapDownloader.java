package org.mati.geotech.model.qmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;
import java.util.Vector;

public abstract class MapDownloader extends Thread {
	private Stack<String> _dwStack = new Stack<String>();
	private Vector<DownloaderListener> _dei = new Vector<DownloaderListener>();

	
	protected void downloadFile(String address, String localFileName) throws IOException {
		OutputStream out = null;
		URLConnection conn = null;
		InputStream  in = null;
		try {
			URL url = new URL(address);
			out = new BufferedOutputStream(
				new FileOutputStream(localFileName+".tmp"));
			conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
			//System.out.println(localFileName + "\t" + numWritten);
			out.close();
			new File(localFileName+".tmp").renameTo(new File(localFileName));
		} catch (IOException exception) {
			throw exception;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
				throw ioe;
			}
		}
	}
	
	public Stack<String> getStack() { return _dwStack; }
	protected Vector<DownloaderListener> getListeners() { return _dei; }

	public synchronized void addPath(String path) {
		if(getStack().search(path)==-1) {
			//System.out.println("got : "+path+" in queue");
			getStack().add(path);
			// Collections.shuffle(_dwStack);
			notifyAll();
		}
		else
		{
			getStack().remove(path);
			getStack().push(path);
			//System.err.println("path: "+path+" in queue");
		}
	}

	@Override
	public void run() {
		boolean bExit = false;
		String mapPath;
		// System.out.println("Downloader started");
		while(!bExit) {
			if(!getStack().isEmpty()) {
				mapPath=getStack().peek();
				System.out.println("[left: "+getStack().size()+"]");
				String filename = getStorePath();
				if(!filename.endsWith("/") || !filename.endsWith("\\")) filename+="/";
				for(int i = 0; i < mapPath.length()-1; i++) {
					filename+=mapPath.charAt(i)+"/";
				}
				new File(filename).mkdirs();
				filename+=mapPath.charAt(mapPath.length()-1)+getFileExt();;
				
				if(new File(filename).isFile()) {
					//System.err.println("file: "+filename+" is exists");
					getStack().pop();
					for(DownloaderListener dl:getListeners()) {
						dl.downloadComplite(mapPath, filename);
					}
				}
				else {
					try {
						downloadFile(makePath(mapPath),filename);
						getStack().pop();
						// System.out.println("downloaded: "+mapPath);
						for(DownloaderListener dl:getListeners()) {
							dl.downloadComplite(mapPath, filename);
						}
						try {
							Thread.sleep((long) (getDelayLoadTime()));
						} catch (InterruptedException e1) {	}
	
					} catch (IOException e) {
						try {
							System.err.println("exception: "+e.getMessage());
							if (!( e instanceof FileNotFoundException)) {
								getStack().pop();
								System.out.println("sleep for "+getSleepTime()/60000+" min");
								Thread.sleep((long) (getSleepTime()));
							} else {
								getStack().pop();
								for(DownloaderListener dl:getListeners()) dl.fileNotAvailable(mapPath);
							}
						} catch (InterruptedException ie) {	
							bExit=true;
						}
					}
				}
			}			
			bExit=waitForTask();
		}
		//System.out.println("Downloader stoped");
	}

	abstract protected long getDelayLoadTime();

	abstract protected String getFileExt();
	abstract protected long getSleepTime();
	abstract protected String makePath(String mapPath);
	abstract protected String getStorePath();

	private synchronized boolean waitForTask() {
		while(getStack().isEmpty()) {
			try { 
				wait();
			} catch (InterruptedException e) {
				return true;
			}
		}
		return false;
	}

	public void removeErrorListner(DownloaderListener dei) { getListeners().remove(dei); }
	public void addErrorListner(DownloaderListener dei) { getListeners().add(dei); }

}
