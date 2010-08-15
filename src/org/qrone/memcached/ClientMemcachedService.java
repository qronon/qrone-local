package org.qrone.memcached;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class ClientMemcachedService implements MemcachedService{
	private static SockIOPool pool;
	private MemCachedClient client;
	public ClientMemcachedService(String[] serverlist) {
		if (pool == null) {
			SockIOPool pool = SockIOPool.getInstance();
			pool.setServers(serverlist);
			pool.initialize();
		}
		client = new MemCachedClient();
	}
	
	@Override
	public void clearAll() {
		client.flushAll();
	}

	@Override
	public boolean contains(String key) {
		return client.keyExists(key);
	}

	@Override
	public boolean delete(String key) {
		return client.delete(key);
	}

	@Override
	public boolean delete(String key, long millisNoReAdd) {
		return client.delete(key, new Date(System.currentTimeMillis()+millisNoReAdd));
	}

	@Override
	public Set<String> deleteAll(Collection<String> keys) {
		Set<String> set = new HashSet<String>();
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			String string = i.next();
			if(delete(string))
				set.add(string);
		}
		return set;
	}

	@Override
	public Set<String> deleteAll(Collection<String> keys, long millisNoReAdd) {
		Set<String> set = new HashSet<String>();
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			String string = i.next();
			if(delete(string,millisNoReAdd))
				set.add(string);
		}
		return set;
	}

	@Override
	public Object get(String key) {
		return client.get(key);
	}

	@Override
	public Map<String, Object> getAll(Collection<String> keys) {
		return client.getMulti(keys.toArray(new String[keys.size()]));
	}

	@Override
	public long increment(String key, long delta) {
		return client.incr(key, delta);
	}

	@Override
	public void put(String key, Object value) {
		client.add(key, value);
	}

	@Override
	public void put(String key, Object value, int ttlmillis) {
		client.add(key, value, new Date(System.currentTimeMillis()+ttlmillis));
	}

	@Override
	public void put(String key, Object value, Date expire) {
		client.add(key, value, expire);
	}

	@Override
	public void put(String key, Object value, int ttlmillis, SetPolicy policy) {
		put(key, value, new Date(System.currentTimeMillis()+ttlmillis), policy);
	}

	@Override
	public void put(String key, Object value, Date expire, SetPolicy policy) {
		if(policy == SetPolicy.SET_ALWAYS){
			client.add(key, value, expire);
		}else if(policy == SetPolicy.REPLACE_ONLY_IF_PRESENT){
			client.replace(key, value, expire);
		}else if(policy == SetPolicy.ADD_ONLY_IF_NOT_PRESENT){
			if(!client.keyExists(key))
				client.add(key, value, expire);
		}
	}

	@Override
	public void putAll(Map<String, Object> values) {
		for (Iterator<Entry<String, Object>> i = values.entrySet().iterator(); i
				.hasNext();) {
			Entry<String, Object> v = i.next();
			put(v.getKey(),v.getValue());
		}
	}

	@Override
	public void putAll(Map<String, Object> values, int ttlmillis) {
		for (Iterator<Entry<String, Object>> i = values.entrySet().iterator(); i
				.hasNext();) {
			Entry<String, Object> v = i.next();
			put(v.getKey(),v.getValue(),ttlmillis);
		}
	}

	@Override
	public void putAll(Map<String, Object> values, Date expire) {
		for (Iterator<Entry<String, Object>> i = values.entrySet().iterator(); i
				.hasNext();) {
			Entry<String, Object> v = i.next();
			put(v.getKey(),v.getValue(),expire);
		}
	}

	@Override
	public void putAll(Map<String, Object> values, int ttlmillis,
			SetPolicy policy) {
		for (Iterator<Entry<String, Object>> i = values.entrySet().iterator(); i
				.hasNext();) {
			Entry<String, Object> v = i.next();
			put(v.getKey(),v.getValue(),ttlmillis, policy);
		}
	}

	@Override
	public void putAll(Map<String, Object> values, Date expire, SetPolicy policy) {
		for (Iterator<Entry<String, Object>> i = values.entrySet().iterator(); i
				.hasNext();) {
			Entry<String, Object> v = i.next();
			put(v.getKey(),v.getValue(),expire, policy);
		}
	}

}
