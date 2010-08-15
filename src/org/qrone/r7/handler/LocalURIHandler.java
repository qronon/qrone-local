package org.qrone.r7.handler;

import java.io.File;

import javax.servlet.http.HttpServlet;

import org.qrone.r7.ExtensionIndex;
import org.qrone.r7.app.AwtImageBufferService;
import org.qrone.r7.resolver.FileResolver;
import org.qrone.r7.resolver.MemoryResolver;
import org.qrone.r7.store.MemoryStore;

public class LocalURIHandler extends ExtendableURIHandler{
	
	public LocalURIHandler(HttpServlet serv) {
		resolver.add(new MemoryResolver());
		resolver.add(new FileResolver(new File(".")));
		
		OpenIDHandler openidHandler = new OpenIDHandler(new MemoryStore());
		HTML5Handler html5handler = new HTML5Handler(
				resolver, new AwtImageBufferService(), openidHandler);
		ExtensionIndex ei = new ExtensionIndex();
		if(ei.unpack(resolver) == null){
			ei.find();
			ei.pack(resolver);
		}
		ei.extend(html5handler);
		ei.extend(this);
		
		handler.add(new PathFinderHandler(html5handler));
		handler.add(openidHandler);
		handler.add(new ResolverHandler(resolver));
		handler.add(new FaviconHandler(resolver));
		handler.add(new ProxyHandler(serv.getServletConfig(), 8080));
	}
}
