package com.cocolab.client.message;

public class CCDoNextJob {
	public static final int doDefault = -1;
	public static final int doNext = 1001;
	
	public static final int ReceiverCall = 1002;  // 전화가 수신되면 회의실 검색 후 join
	
	public static final int MobileWebCall = 1003; // 모바일 웹페이지를 통해 전화 걸기
	public static final int ManualCall = 1004; 	// 키패드 입력으로 전화 걸기
	public static final int MultiGuestCall = 1005;// 회의실에 여러명을 초대할때
	
	public static final int ccGetContent = 1090;
	public static final int ccJoin = 1091;
	public static final int ccSearchContent = 1095;
	public static final int ccExit = 1098;
	public static final int ccFinish = 1099;
	//ccViewActivity
}
