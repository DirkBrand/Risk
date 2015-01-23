/**
 * 
 */
package risk.aiplayers.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU Cache using LinkedHashMap - note we must use access-order for the cache
 * to ensure old values are removed.
 * 
 * Basis from http://java-planet.blogspot.pt/2005/08/how-to-set-up-simple-lru-cache-using.html
 * 
 * @author kroon
 * 
 */
public class CacheMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = -1122826653182028194L;
	private final int capacity;

	public CacheMap(int capacity) {
		super(capacity + 1, 1.1f, true);
		this.capacity = capacity;
	}

	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {

		return size() > capacity;
	}
}