package mil.pusdalops.webui.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Jul 11, 2022 - @author mrusli
 *
 * Ref: https://www.educative.io/answers/remove-duplicates-from-list-of-user-defined-class-objects-in-java
 */
public class UniqueList {

	@SuppressWarnings("rawtypes")
	private HashSet masterSet = new HashSet();
	@SuppressWarnings("rawtypes")
	private	ArrayList innerList;
	private Object[] returnable;
	
	@SuppressWarnings("rawtypes")
	public UniqueList() {
		innerList = new ArrayList();
	}
	
	@SuppressWarnings("rawtypes")
	public UniqueList(int size) {
		innerList = new ArrayList(size);
	}
	
	@SuppressWarnings("unchecked")
	public void add(Object thing) {
		if (!masterSet.contains(thing)) {
			masterSet.add(thing);
			innerList.add(thing);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List getList() {
		return innerList;
	}
	
	public Object get(int index) {
		return innerList.get(index);
	}
	
	public Object[] toObjectArray() {
		int size = innerList.size();
		returnable = new Object[size];
		for (int i=0; i < size; i++) {
			returnable[i] = innerList.get(i);
		}
		
		return returnable;
	}
	
}
