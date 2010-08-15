package org.qrone.r7;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.servlet.GzipFilter;
import org.qrone.r7.app.QrONEServlet;

public class QrONEApp {
	private static Server server = null;

	public static void main(String[] args) {
		server = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(9601);
		server.addConnector(connector);

		ServletHandler handler = new ServletHandler();
		FilterHolder gzip = handler.addFilterWithMapping(GzipFilter.class,"/*",0);
        //gzip.setAsyncSupported(true);
        gzip.setInitParameter("minGzipSize","256");
        
		handler.addServletWithMapping(QrONEServlet.class, "/*");
		server.addHandler(handler);

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					server.start();
					server.join();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		};
		Thread jettyThread = new Thread(runnable);
		jettyThread.start();

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("QrONE JavaScript Server");
		shell.setSize((int) (240 * 1.618), 240);
		final Browser browser;
		try {
			browser = new Browser(shell, SWT.NONE);
		} catch (SWTError e) {
			System.out.println("Could not instantiate Browser: "
					+ e.getMessage());
			display.dispose();
			return;
		}
		
		shell.open();
		browser.setUrl("http://localhost:9601/qrone-server/index");
		browser.addLocationListener(new LocationListener() {
			@Override
			public void changing(LocationEvent event) {
				if(event.location.equals("http://qrone-server-home/")){
					Program.launch("file:///" + new File(".").getAbsoluteFile()
							.getParentFile().getAbsolutePath());
					event.doit = false;
				}else{
					Program.launch(event.location);
					event.doit = false;
				}
			}

			@Override
			public void changed(LocationEvent event) {
			}
		});
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}