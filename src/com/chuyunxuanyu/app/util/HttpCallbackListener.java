package com.chuyunxuanyu.app.util;

public interface HttpCallbackListener {

	/*
	 * author:liuyang
	 * time:2015-12-30
	 * description:�¶����ӱ�������
	 */
	void onFinish(String response);
	
	void onError(Exception e);
	
}
