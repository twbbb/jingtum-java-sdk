package com.jingtum.net;

public interface RequestListener<T> {
	public void onComplete(T result);
}
