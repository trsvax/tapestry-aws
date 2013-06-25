package com.trsvax.tapestry.aws.utils;

import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

/**
 * Keep order on DynamoDB sets
 */
public class SortedSet<E extends Sortable> extends TreeSet<E> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7146227494728616911L;

	public SortedSet() {
		super( new Comparator<E>() {

			public int compare(Sortable a, Sortable b) {
				return a.sortOn() == b.sortOn() ? 0 : a.sortOn() > b.sortOn() ? 1 : -1;
			}			
		});
	}
	
	public static long now() {
		return new Date().getTime();
	}

}
