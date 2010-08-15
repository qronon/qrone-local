package org.qrone.mongo;

import java.util.Hashtable;
import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.qrone.kvs.KVSService;
import org.qrone.kvs.KVSTable;
import org.qrone.r7.Extension;
import org.qrone.r7.script.ScriptableWrapper;

import com.mongodb.DB;

@Extension
public class MongoService extends ScriptableWrapper<DB> implements KVSService{
	private static final long serialVersionUID = -2832247783317655621L;
	
	private DB db;
	private Map<String, MongoTable> map = new Hashtable<String, MongoTable>();

	public MongoService(DB db){
		this.db = db;
	}
	
	@Override
	public KVSTable getCollection(String name) {
		MongoTable t = map.get(name);
		if(t == null){
			t = new MongoTable(db.getCollection(name));
		}
		return t;
	}

	@Override
	public Object get(String key, Scriptable start) {
		if(map.containsKey(key)){
			return map.get(key);
		}
		
		KVSTable table = getCollection(key);
		map.put(key, (MongoTable)table);
		return table;
	}

	@Override
	public Object[] getIds() {
		return map.keySet().toArray(new Object[map.size()]);
	}
}
