package org.qrone.mongo;

import org.mozilla.javascript.Scriptable;
import org.qrone.kvs.KVSCursor;
import org.qrone.kvs.KVSTable;
import org.qrone.r7.Extension;
import org.qrone.r7.ObjectConverter;
import org.qrone.r7.script.ScriptablePrototype;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Extension
public class MongoTable implements ScriptablePrototype<DBCollection>, KVSTable {
	private DBCollection coll;
	public MongoTable(DBCollection coll) {
		this.coll = coll;
	}
	
	@Override
	public KVSCursor find() {
		return new MongoCursor(coll.find());
	}

	@Override
	public KVSCursor find(Scriptable o) {
		return new MongoCursor(coll.find((DBObject)ObjectConverter.to(o)));
	}

	@Override
	public void remove(Scriptable o) {
		coll.remove((DBObject)ObjectConverter.to(o));
	}

	@Override
	public void save(Scriptable o) {
		coll.save((DBObject)ObjectConverter.to(o));
	}

	@Override
	public void drop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(Scriptable o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public KVSCursor find(Scriptable o, Scriptable p) {
		return new MongoCursor(coll.find((DBObject)ObjectConverter.to(o),
				(DBObject)ObjectConverter.to(p)));
	}

	@Override
	public KVSCursor find(Scriptable o, Scriptable p, Number skip) {
		return find(o, p).skip(skip);
	}

	@Override
	public KVSCursor find(Scriptable o, Scriptable p, Number skip, Number limit) {
		return find(o, p).skip(skip).limit(limit);
	}

}
