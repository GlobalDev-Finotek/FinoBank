package com.cocolab.client.message;

public class CCMsg {
	public static final int NONE = 0;
	
	public static final int USER_ADD = 1001;
	public static final int USER_SEARCH = 1002;
	public static final int USER_UPDATE = 1003;
	public static final int USER_EXPIRE = 1004;
	public static final int USER_LOGIN = 1005;
	public static final int USER_LOGOUT = 1007;
	public static final int USER_DELETE = 1010;
	
	public static final int VALIDATE_ADDRESS_BOOK = 1006;
	
	public static final int UPDATE_SESSION = 1008;
	
	public static final int GROUP_ADD = 2001;
	public static final int GROUP_SEARCH = 2002;
	public static final int GROUP_UPDATE = 2003;
	public static final int GROUP_DELETE = 2004;
	
	public static final int GROUP_CATEGORY_ADD = 2005;
	public static final int GROUP_CATEGORY_SEARCH = 2006;
	public static final int GROUP_CATEGORY_UPDATE = 2007;
	public static final int GROUP_CATEGORY_DELETE = 2008;
	
	public static final int WORKROOM_ADD = 3001;
	public static final int WORKROOM_SEARCH_WITH_TITLE = 3002;
	public static final int WORKROOM_SEARCH_WITH_USER = 3003;
	public static final int WORKROOM_SEARCH_WITH_OWNERUID = 3004;
	public static final int WORKROOM_GET = 3010;
	public static final int WORKROOM_SET = 3021;
	public static final int WORKROOM_DELETE = 3022;
	public static final int WORKROOM_GET_WHITEBOARD = 3031;
	public static final int WORKROOM_SET_WHITEBOARD = 3032;
	public static final int WORKROOM_UPDATE_JOIN = 3041;
	public static final int WORKROOM_INVITE = 3042;
	public static final int WORKROOM_INVITES = 3049;
	public static final int WORKROOM_JOIN = 3043;
	public static final int WORKROOM_RELEASE_HOST = 3044;
	public static final int WORKROOM_CAPTURE_HOST = 3045;
	public static final int WORKROOM_SEARCH_WITHOUT_LOGIN = 3046;
	public static final int WORKROOM_UPDATE = 3101;
	public static final int WORKROOM_UPDATE_WHITEBOARD = 3201;
	public static final int WORKROOM_UPDATE_WHITEBOARD_PAGE = 3202;
	public static final int WORKROOM_UPDATE_WHITEBOARD_SCROLL = 3203;
	public static final int WORKROOM_UPDATE_WHITEBOARD_ZOOM = 3204;
	public static final int WORKROOM_UPDATE_WHITEBOARD_ORIENTATION = 3205;
	//public static final int WORKROOM_UPDATE_WHITEBOARD_SHARP = 3205;
	public static final int MAX_SEQUENCE_GET = 3301;		// JHK
	public static final int WORKROOM_MESSAFE_GET = 3302;	// JHK
	
	public static final int CONTENTS_SEARCH = 5001;
	public static final int CONTENTS_ADD = 5002;
	public static final int CONTENTS_GET = 5003;
	public static final int CONTENTS_UPDATE = 5004;
	public static final int CONTENTS_DELETE = 5005;
	public static final int CONTENTS_RELOAD = 5006;
	public static final int CONTENTS_GET_INFO = 5010;	// CONTENTS_GET과 동일하나, "content"만 없음.
	
	public static final int SEND_MESSAGE = 10001;
	public static final int SEND_CHUNK = 10002;
	public static final int SEND_PUSH = 10003;
	public static final int SEND_PRIVATE_MESSAGE = 10004;
	public static final int SEND_PRIVATE_CHUNK = 10005;
	public static final int SEND_PRIVATE_PUSH = 10006;
	
//	public static final int NOTIFICATION_DRAW_LINE2 = 10105;
//	public static final int NOTIFICATION_DRAW_TXT = 10106;
	public static final int NOTIFICATION_UPDATE_DC = 3210;
	
	public static final int DRAW_PIXEL = 10101;
	public static final int DRAW_LINE = 10102;
	public static final int DRAW_RECTANGLE = 10103;
	public static final int DRAW_ELLIPSE = 10104;
	public static final int DRAW_LINE2 = 10105;
	public static final int DRAW_TEXT = 10106;
	public static final int DRAW_ERASE = 10107;
	public static final int DRAW_ERASEALL = 10108;
	public static final int DRAW_SHAPE = 10109;
	
	public static final int SEARCH_VERSION = 90001;
	public static final int HELLO = 100001;
	public static final int EXIT = 100002;
	public static final int NOOP = 100003;
	
	public static final int FREE = 99999;
	
	public static final int MESSAGE_TIMEOUT = -100001;

	public static String getTCString(int tc){
		String strTC = "unknown tc";
		
		switch(tc){
		case USER_ADD:
			strTC = "USER_ADD"; break;
		case USER_SEARCH:
			strTC = "USER_SEARCH"; break;
		case USER_UPDATE:
			strTC = "USER_UPDATE"; break;
		case USER_EXPIRE:
			strTC = "USER_EXPIRE"; break;
		case USER_LOGIN:
			strTC = "USER_LOGIN"; break;
		case USER_LOGOUT:
			strTC = "USER_LOGOUT"; break;
		case USER_DELETE:
			strTC = "USER_DELETE"; break;
		case VALIDATE_ADDRESS_BOOK:
			strTC = "VALIDATE_ADDRESS_BOOK"; break;
		case UPDATE_SESSION:
			strTC = "UPDATE_SESSION"; break;
		case GROUP_ADD:
			strTC = "GROUP_ADD"; break;
		case GROUP_SEARCH:
			strTC = "GROUP_SEARCH"; break;
		case GROUP_UPDATE:
			strTC = "GROUP_UPDATE"; break;
		case GROUP_DELETE:
			strTC = "GROUP_DELETE"; break;
		case GROUP_CATEGORY_ADD:
			strTC = "GROUP_CATEGORY_ADD"; break;
		case GROUP_CATEGORY_SEARCH:
			strTC = "GROUP_CATEGORY_SEARCH"; break;
		case GROUP_CATEGORY_UPDATE:
			strTC = "GROUP_CATEGORY_UPDATE"; break;
		case GROUP_CATEGORY_DELETE:
			strTC = "GROUP_CATEGORY_DELETE"; break;
		case WORKROOM_ADD:
			strTC = "WORKROOM_ADD"; break;
		case WORKROOM_SEARCH_WITH_TITLE:
			strTC = "WORKROOM_SEARCH_WITH_TITLE"; break;
		case WORKROOM_SEARCH_WITH_USER:
			strTC = "WORKROOM_SEARCH_WITH_USER"; break;
		case WORKROOM_GET:
			strTC = "WORKROOM_GET"; break;
		case WORKROOM_SET:
			strTC = "WORKROOM_SET"; break;
		case WORKROOM_DELETE:
			strTC = "WORKROOM_DELETE"; break;
		case WORKROOM_GET_WHITEBOARD:
			strTC = "WORKROOM_GET_WHITEBOARD"; break;
		case WORKROOM_SET_WHITEBOARD:
			strTC = "WORKROOM_SET_WHITEBOARD"; break;
		case WORKROOM_UPDATE_JOIN:
			strTC = "WORKROOM_UPDATE_JOIN"; break;
		case WORKROOM_INVITE:
			strTC = "WORKROOM_INVITE"; break;
		case WORKROOM_INVITES:
			strTC = "WORKROOM_INVITES"; break;
		case WORKROOM_JOIN :
			strTC = "WORKROOM_JOIN"; break;
		case WORKROOM_RELEASE_HOST :
			strTC = "WORKROOM_RELEASE_HOST"; break;
		case WORKROOM_CAPTURE_HOST :
			strTC = "WORKROOM_CAPTURE_HOST"; break;
		case WORKROOM_UPDATE :
			strTC = "WORKROOM_UPDATE"; break;
		case WORKROOM_UPDATE_WHITEBOARD :
			strTC = "WORKROOM_UPDATE_WHITEBOARD"; break;
		case WORKROOM_UPDATE_WHITEBOARD_PAGE:
			strTC = "WORKROOM_UPDATE_WHITEBOARD_PAGE"; break;
		case WORKROOM_UPDATE_WHITEBOARD_SCROLL:
			strTC = "WORKROOM_UPDATE_WHITEBOARD_SCROLL"; break;
		case WORKROOM_UPDATE_WHITEBOARD_ZOOM:
			strTC = "WORKROOM_UPDATE_WHITEBOARD_ZOOM"; break;
//		case WORKROOM_UPDATE_WHITEBOARD_SHARP:
//			strTC = "WORKROOM_UPDATE_WHITEBOARD_SHARP"; break;
		case MAX_SEQUENCE_GET:
			strTC = "MAX_SEQUENCE_GET"; break;
		case WORKROOM_MESSAFE_GET:
			strTC = "WORKROOM_MESSAFE_GET"; break;
		case CONTENTS_SEARCH:
			strTC = "CONTENTS_SEARCH"; break;
		case CONTENTS_ADD:
			strTC = "CONTENTS_SEARCH"; break;
		case CONTENTS_GET:
			strTC = "CONTENTS_GET"; break;
		case CONTENTS_UPDATE:
			strTC = "CONTENTS_UPDATE"; break;
		case CONTENTS_DELETE:
			strTC = "CONTENTS_DELETE"; break;
		case CONTENTS_RELOAD:
			strTC = "CONTENTS_RELOAD"; break;
		case SEND_MESSAGE:
			strTC = "SEND_MESSAGE"; break;
		case SEND_CHUNK:
			strTC = "SEND_CHUNK"; break;
		case SEND_PUSH:
			strTC = "SEND_PUSH"; break;
		case SEND_PRIVATE_MESSAGE:
			strTC = "SEND_PRIVATE_MESSAGE"; break;
		case SEND_PRIVATE_CHUNK:
			strTC = "SEND_PRIVATE_CHUNK"; break;
		case SEND_PRIVATE_PUSH:
			strTC = "SEND_PRIVATE_PUSH"; break;
//		case NOTIFICATION_DRAW_LINE2:
//			strTC = "NOTIFICATION_DRAW_LINE2"; break;
//		case NOTIFICATION_DRAW_TXT:
//			strTC = "NOTIFICATION_DRAW_TXT"; break;
		case NOTIFICATION_UPDATE_DC:
			strTC = "NOTIFICATION_UPDATE_DC"; break;
		case DRAW_PIXEL:
			strTC = "DRAW_PIXEL"; break;
		case DRAW_LINE:
			strTC = "DRAW_LINE"; break;
		case DRAW_RECTANGLE:
			strTC = "DRAW_RECTANGLE"; break;
		case DRAW_ELLIPSE:
			strTC = "DRAW_ELLIPSE"; break;
		case DRAW_LINE2:
			strTC = "DRAW_LINE2"; break;
		case DRAW_TEXT:
			strTC = "DRAW_TEXT"; break;
		case SEARCH_VERSION:
			strTC = "DRAW_TEXT"; break;
		case HELLO:
			strTC = "HELLO"; break;
		case EXIT:
			strTC = "EXIT"; break;
		case NOOP:
			strTC = "NOOP"; break;
		case FREE:
			strTC = "FREE"; break;
		}
		
		return strTC;
	}
}
