package com.cocolab.viewer;

public class DrawMode {
	public static final int MOVE_TO_PAGE1  = 1;
	public static final int MOVE_TO_PAGE_STARIT_MODE  = 2;
	public static final int MOVE_TO_PAGE_LAST  = 3;
	public static final int RELOAD  = 4;
	public static final int PAN_MOVE  = 5;
	public static final int SET_ZOOM  = 6;
	public static final int DRAW_PAN_STRAIT1  = 7;
	public static final int DRAW_PAN_STRAIT2  = 8;
	public static final int DRAW_PAN_STRAIT3  = 9;
	public static final int DRAW_PAN_NOR1  = 10;
	public static final int DRAW_PAN_NOR2  = 11;
	public static final int DRAW_CSD_straitMode1  = 12;
	public static final int DRAW_CSD_straitMode2  = 13;
	public static final int DRAW_CSD_straitMode3  = 14;
	public static final int DRAW_CSD_straitMode4  = 15;
	public static final int DRAW_CSD_straitMode5  = 16;
	public static final int PV_HANDLER_WHAT8  = 17;
	public static final int PV_HANDLER_WHAT13_1  = 18;
	public static final int PV_HANDLER_WHAT13_2  = 19;
	public static final int PV_HANDLER_WHAT14_1  = 20;
	public static final int PV_HANDLER_WHAT14_2  = 21;
	public static final int PV_HANDLER_WHAT15_1  = 22;
	public static final int PV_HANDLER_WHAT15_2  = 23;
	public static final int PV_HANDLER_WHAT16_1  = 24;
	public static final int PV_HANDLER_WHAT16_2  = 25;
	public static final int PV_HANDLER_WHAT100  = 26;
	public static final int PV_HANDLER_WHAT501  = 27;
	public static final int SELECT_OBJECT  = 28;
	public static final int SELECT_OBJECT_TOUCHUP1  = 29;
	public static final int SELECT_OBJECT_TOUCHUP2  = 30;
	public static final int SET_POSITION_AND_SCALE_FIT  = 31;
	public static final int SET_POSITION_AND_SCALE  = 32;
	public static final int DEFAULT_MATRIX_SET  = 33;
	public static final int SCROLL_BAR_HIDE_NOT  = 34;
	public static final int ZOOM_MATRIX_SET  = 36;
	public static final int CSD_FIND  = 37;
	public static final int CSD_FIND_NEXT  = 38;
	public static final int CSD_FIND_AGAIN  = 39;
	public static final int RATIO_TUNNING  = 40;
	public static final int CALLBACK_REDRAW  = 100;
	public static final int CALLBACK_ORGFIN  = 101;
	public static final int CALLBACK_LANDFIN  = 102;
	public static final int CALLBACK_DOUBLETAB_FIT  = 103;
	public static final int CALLBACK_DOUBLETAB_ZOOM  = 104;
	public static final int CALLBACK_STRECH_ZOOM  = 105;
	public static final int CALLBACK_SCROLL  = 106;
	public static final int CALLBACK_REDRAW_SCREEN  = 107;
	public static final int CALLBACK_PAGE  = 200;
	public static final int FIT_MATRIX_SET0  = 50;
	public static final int FIT_MATRIX_SET1  = 51;
	public static final int FIT_MATRIX_SET2  = 52;
	public static final int FIT_MATRIX_SET3  = 53;
	public static final int FIT_MATRIX_SET4  = 54;
	public static final int FIT_MATRIX_SET5  = 55;
	public static final int FIT_MATRIX_SET6  = 56;
	public static final int FIT_MATRIX_SET7  = 57;
	public static final int FIT_MATRIX_SET8  = 58;
	
	public static String getMode(int code) {
		String msg = "";
	
		switch (code) {
		case MOVE_TO_PAGE1:
		msg = "MOVE_TO_PAGE1";
		break;
		case MOVE_TO_PAGE_STARIT_MODE:
		msg = "MOVE_TO_PAGE_STARIT_MODE";
		break;
		case MOVE_TO_PAGE_LAST:
		msg = "MOVE_TO_PAGE_LAST";
		break;
		case RELOAD:
		msg = "RELOAD";
		break;
		case PAN_MOVE:
		msg = "PAN_MOVE";
		break;
		case SET_ZOOM:
		msg = "SET_ZOOM";
		break;
		case DRAW_PAN_STRAIT1:
		msg = "DRAW_PAN_STRAIT1";
		break;
		case DRAW_PAN_STRAIT2:
		msg = "DRAW_PAN_STRAIT2";
		break;
		case DRAW_PAN_STRAIT3:
		msg = "DRAW_PAN_STRAIT3";
		break;
		case DRAW_PAN_NOR1:
		msg = "DRAW_PAN_NOR1";
		break;
		case DRAW_PAN_NOR2:
		msg = "DRAW_PAN_NOR2";
		break;
		case DRAW_CSD_straitMode1:
		msg = "DRAW_CSD_straitMode1";
		break;
		case DRAW_CSD_straitMode2:
		msg = "DRAW_CSD_straitMode2";
		break;
		case DRAW_CSD_straitMode3:
		msg = "DRAW_CSD_straitMode3";
		break;
		case DRAW_CSD_straitMode4:
		msg = "DRAW_CSD_straitMode4";
		break;
		case DRAW_CSD_straitMode5:
		msg = "DRAW_CSD_straitMode5";
		break;
		case PV_HANDLER_WHAT8:
		msg = "PV_HANDLER_WHAT8";
		break;
		case PV_HANDLER_WHAT13_1:
		msg = "PV_HANDLER_WHAT13_1";
		break;
		case PV_HANDLER_WHAT13_2:
		msg = "PV_HANDLER_WHAT13_2";
		break;
		case PV_HANDLER_WHAT14_1:
		msg = "PV_HANDLER_WHAT14_1";
		break;
		case PV_HANDLER_WHAT14_2:
		msg = "PV_HANDLER_WHAT14_2";
		break;
		case PV_HANDLER_WHAT15_1:
		msg = "PV_HANDLER_WHAT15_1";
		break;
		case PV_HANDLER_WHAT15_2:
		msg = "PV_HANDLER_WHAT15_2";
		break;
		case PV_HANDLER_WHAT16_1:
		msg = "PV_HANDLER_WHAT16_1";
		break;
		case PV_HANDLER_WHAT16_2:
		msg = "PV_HANDLER_WHAT16_2";
		break;
		case PV_HANDLER_WHAT100:
		msg = "PV_HANDLER_WHAT100" ;
		break;
		case PV_HANDLER_WHAT501:
		msg = "PV_HANDLER_WHAT501" ;
		break;
		case SELECT_OBJECT:
		msg = "SELECT_OBJECT" ;
		break;
		case SELECT_OBJECT_TOUCHUP1:
		msg = "SELECT_OBJECT_TOUCHUP1" ;
		break;
		case SELECT_OBJECT_TOUCHUP2:
		msg = "SELECT_OBJECT_TOUCHUP2" ;
		break;
		case SET_POSITION_AND_SCALE_FIT:
		msg = "SET_POSITION_AND_SCALE_FIT" ;
		break;
		case SET_POSITION_AND_SCALE:
		msg = "SET_POSITION_AND_SCALE" ;
		break;
		case DEFAULT_MATRIX_SET:
		msg = "DEFAULT_MATRIX_SET" ;
		break;
		case SCROLL_BAR_HIDE_NOT:
		msg = "SCROLL_BAR_HIDE_NOT" ;
		break;
		case ZOOM_MATRIX_SET:
		msg = "ZOOM_MATRIX_SET" ;
		break;
		case CSD_FIND:
		msg = "CSD_FIND" ;
		break;
		case CSD_FIND_NEXT:
		msg = "CSD_FIND_NEXT" ;
		break;
		case CSD_FIND_AGAIN:
		msg = "CSD_FIND_AGAIN" ;
		break;
		case RATIO_TUNNING:
		msg = "RATIO_TUNNING" ;
		break;
		case CALLBACK_REDRAW:
		msg = "CALLBACK_REDRAW" ;
		break;
		case CALLBACK_REDRAW_SCREEN:
		msg = "CALLBACK_REDRAW_SCREEN" ;
		break;
		case CALLBACK_ORGFIN:
		msg = "CALLBACK_ORGFIN" ;
		break;
		case CALLBACK_LANDFIN:
		msg = "CALLBACK_LANDFIN" ;
		break;
		case CALLBACK_DOUBLETAB_FIT:
		msg = "CALLBACK_DOUBLETAB_FIT" ;
		break;
		case CALLBACK_DOUBLETAB_ZOOM:
		msg = "CALLBACK_DOUBLETAB_ZOOM" ;
		break;
		case CALLBACK_STRECH_ZOOM:
		msg = "CALLBACK_STRECH_ZOOM" ;
		break;
		case CALLBACK_SCROLL:
		msg = "CALLBACK_SCROLL" ;
		break;
		case CALLBACK_PAGE:
		msg = "CALLBACK_PAGE" ;
		break;
		case FIT_MATRIX_SET0:
		msg = "FIT_MATRIX_SET0" ;
		break;
		case FIT_MATRIX_SET1:
		msg = "FIT_MATRIX_SET1" ;
		break;
		case FIT_MATRIX_SET2:
		msg = "FIT_MATRIX_SET2" ;
		break;
		case FIT_MATRIX_SET3:
		msg = "FIT_MATRIX_SET3" ;
		break;
		case FIT_MATRIX_SET4:
		msg = "FIT_MATRIX_SET4" ;
		break;
		case FIT_MATRIX_SET5:
		msg = "FIT_MATRIX_SET5" ;
		break;
		case FIT_MATRIX_SET6:
		msg = "FIT_MATRIX_SET6" ;
		break;
		case FIT_MATRIX_SET7:
		msg = "FIT_MATRIX_SET7" ;
		break;
		case FIT_MATRIX_SET8:
		msg = "FIT_MATRIX_SET8" ;
		break;
		default:
		msg = "Exception occurred.";
		break;
		}
		return msg;
	}
}
