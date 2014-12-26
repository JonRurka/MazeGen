package com.nug700.MazeGen;

import java.util.ArrayList;

public final class Stack {
	ArrayList<Object> tStack;
	
	public int getCount(){
		return tStack.size();
	}
	
	public Object push(Object o){
		tStack.add(o);
		return o;
	}
	
	public Object pop(){
		if (tStack.size() > 0){
			Object val = tStack.get(tStack.size() - 1);
			tStack.remove(tStack.size() - 1);
			return val;
		}
		else
			return null;
	}
	
	public Object top(){
		if (tStack.size() > 0){
			return tStack.get(tStack.size() - 1);
		}
		else return null;
	}
	
	public boolean isEmpty(){
		return (tStack.size() == 0);
	}
	
	public Stack() {
		tStack = new ArrayList<Object>();
	}
}
