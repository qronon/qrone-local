package org.qrone.r7.app;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.handler.URIHandler;

/**
 * Servlet implementation class QrONEServer
 */
public class QrONEServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private URIHandler handler;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QrONEServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(handler == null)
			handler = new QrONEURIHandler(this);
		handler.handle(request, response, request.getPathInfo(), "");
		/*
		try {
			URI uri = new URI("index.html").resolve(
					new URI(request.getPathInfo().substring(1)));
			String uristr = uri.toString();
			
			String[] paths = uristr.split("/");
			String path = "";
			String args = "";
			for (int i = 0; i < paths.length; i++) {
				path += paths[i];
				
				if(resolver.exist(path + ".js")){
					path += ".js";
					uri = new URI(path);
					uristr = path;
					
					if(paths.length > i+1){
						for (int j = i+1; j < paths.length; j++) {
							args += "/" + paths[j];
						}
					}
					
					ScriptOM om = vm.compile(uri);
					if(om != null){
						om.run(request, response);
					}else{
						response.sendError(404);
					}
					
					return;
				}
				
				path += "/";
			}
			
			
			if(uristr.equals("qrone-server/index.html")){
				Writer out = response.getWriter();
				HTML5OM om = deck.compile(uri);
				if(om != null){
					HTML5Template t = new HTML5Template(om);
					final File root = new File(".").getAbsoluteFile().getParentFile();
					t.set("#homepath", root.getAbsolutePath());
					t.set("#files", new NodeLister() {
						@Override
						public void accept(HTML5Template t, HTML5Element e) {
							File[] list = root.listFiles();
							for (int i = 0; i < list.length; i++) {
								t.set("#file", list[i].getName());
								t.visit(e);
							}
						}
					});
					
					out.append(t.output());
					deck.getSpriter().create();
					out.flush();
					out.close();
				}else{
					response.sendError(404);
				}
				
			}else if(resolver.exist(uristr)){
				deck.update(uri);
				
				InputStream in = resolver.getInputStream(uri);
				OutputStream out = response.getOutputStream();
				QrONEUtils.copy(in, out);
				out.flush();
				out.close();
				in.close();
			}else{
				
				
				if(resolver.exist(uri.toString())){
					Writer out = response.getWriter();
					HTML5OM om = deck.compile(uri);
					if(om != null){
						deck.getSpriter().create();
						
						out.append(om.serialize());
						out.flush();
						out.close();
						
					}else{
						response.sendError(404);
					}
				}else{
					response.sendError(404);
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e){
			
		}
		*/
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
