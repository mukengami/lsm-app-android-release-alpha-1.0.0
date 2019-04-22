package org.lsm.mobile.model;

/**
* Created by hanning on 5/27/15.
*/
public interface Filter<T>{
    boolean apply(T t);
}
