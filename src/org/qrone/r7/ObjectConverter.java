package org.qrone.r7;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.bson.BSONObject;
import org.bson.types.BasicBSONList;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.mongodb.BasicDBObject;

/**
 * Direct conversion between native Rhino objects and BSON.
 * <p>
 * This class can be used directly in Rhino.
 * 
 * @author Tal Liron
 */
public class ObjectConverter
{
	public static String stringify( Object s){
		if(s.getClass().getName().equals("org.mozilla.javascript.xmlimpl.XML")){
			return s.toString();
		}
		if(s instanceof Scriptable){
			BSONObject bson = ObjectConverter.to((Scriptable)s);
			return com.mongodb.util.JSON.serialize(bson);
		}
		return JSON.encode(s);
	}
	//
	// Static operations
	//

	/**
	 * Convert from native Rhino to BSON.
	 * 
	 * @param object
	 *        A Rhino native object
	 * @return A BSON object
	 */
	public static BSONObject to( Scriptable object )
	{
		Object[] ids = object.getIds();
		
		boolean isArray = true;
		for (int i = 0; i < ids.length; i++) {
			if(!(ids[i] instanceof Integer)) isArray = false;
		}
		
		if(isArray){
			BasicBSONList bson = new BasicBSONList();
			for( Object id : ids )
			{
				if(id instanceof Integer){
					bson.put((Integer)id, forBson( ScriptableObject.getProperty( object, (Integer)id )));
				}
			}
			return bson;
		}else{

			BasicDBObject bson = new BasicDBObject();

			for( Object id : ids )
			{
				String key = id.toString();
				if(id instanceof String){
					bson.put( key, forBson( ScriptableObject.getProperty( object, key )));
				}
				if(id instanceof Integer){
					bson.put( key, forBson( ScriptableObject.getProperty( object, (Integer)id )));
				}
			}
			return bson;
		}
	}

	/**
	 * Convert from BSON to native Rhino.
	 * 
	 * @param bson
	 *        A BSON object
	 * @return A Rhino native object
	 */
	public static NativeObject from( BSONObject bson )
	{
		if( bson == null )
			return null;

		NativeObject object = new NativeObject();

		for( String key : bson.keySet() )
		{
			Object value = forRhino( bson.get( key ) );
			ScriptableObject.putProperty( object, key, value );
		}

		return object;
	}

	// //////////////////////////////////////////////////////////////////////////
	// Private

	/**
	 * If necessary, convert from native Rhino to a type supported by BSON.
	 * 
	 * @param object
	 *        An object
	 * @return An object ready to be put inside a BSON object
	 */
	private static Object forBson( Object object )
	{
		if( object instanceof NativeJavaObject )
		{
			// This happens either because the developer purposely creates a
			// Java object, or because it was returned from a Java call and
			// wrapped by Rhino.

			return ( (NativeJavaObject) object ).unwrap();
		}
		else if( object instanceof NativeArray )
		{
			// Convert Rhino array to list

			NativeArray array = (NativeArray) object;
			int length = (int) array.getLength();
			List<Object> list = new ArrayList<Object>( length );

			for( int i = 0; i < length; i++ )
				list.add( forBson( ScriptableObject.getProperty( array, i ) ) );

			return list;
		}
		else if( object instanceof Scriptable )
		{
			Scriptable scriptable = (Scriptable) object;
			if( scriptable.getClassName().equals( "Date" ) )
			{
				// The NativeDate class is private in Rhino, but we can access
				// it like a regular object.

				Object time = ScriptableObject.callMethod( scriptable, "getTime", null );
				if( time instanceof Number )
					return new Date( ( (Number) time ).longValue() );
			}

			// Convert

			return to( scriptable );
		}
		else
			return object;
	}

	/**
	 * If necessary, convert from BSON to a type supported by Rhino.
	 * 
	 * @param object
	 *        An object
	 * @return An object ready to be put inside a Rhino object
	 */
	private static Object forRhino( Object object )
	{
		// System.out.println( object.getClass() );
		if( object instanceof BSONObject )
		{
			// Convert

			return from( (BSONObject) object );
		}
		else if( object instanceof List<?> )
		{
			// Convert list to NativeArray

			List<?> list = (List<?>) object;
			NativeArray array = new NativeArray( list.size() );

			int index = 0;
			for( Object item : list )
				ScriptableObject.putProperty( array, index++, forRhino( item ) );

			return array;
		}
		else if( object instanceof Date )
		{
			// The NativeDate class is private in Rhino, but we can create
			// it like a regular object.

			Date date = (Date) object;
			Context context = Context.getCurrentContext();
			Scriptable scope = ScriptRuntime.getTopCallScope( context );
			Scriptable nativeDate = context.newObject( scope, "Date", new Object[]
			{
				date.getTime()
			} );

			return nativeDate;
		}
		else
			return object;
	}
}
