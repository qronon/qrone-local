package org.qrone.mongo;

import org.mozilla.javascript.Scriptable;
import org.qrone.kvs.KVSCursor;
import org.qrone.r7.Extension;
import org.qrone.r7.ObjectConverter;
import org.qrone.r7.script.ScriptablePrototype;
import org.qrone.r7.script.browser.Function;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Extension
public class MongoCursor implements ScriptablePrototype<DBCursor>, KVSCursor {
	private DBCursor c;
	public MongoCursor( DBCursor c) {
		this.c = c;
	}
	 
	@Override
	public void forEach(Function func) {
		while(hasNext()){
			func.call(next());
		}
	}
	
	@Override
	public boolean hasNext() {
		return c.hasNext();
	}
	@Override
	public KVSCursor limit(Number o) {
		return new MongoCursor(c.limit(o.intValue()));
	}
	@Override
	public Object next() {
		return ObjectConverter.from(c.next());
	}
	@Override
	public KVSCursor skip(Number o) {
		return new MongoCursor(c.skip(o.intValue()));
	}
	
	@Override
	public KVSCursor sort(Scriptable o) {
		return new MongoCursor(c.sort((DBObject)ObjectConverter.to(o)));
	}

}
