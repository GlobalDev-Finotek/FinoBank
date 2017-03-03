package com.cocolab.client.message;

public class JetOpenErrorCode {
	/* JET Engine 오류 코드*/
	public static final int OPEN_SUCCESS = 1;				// Open 성공
	public static final int CLOSE_SUCCESS = 1;				// Close 성공
	public static final int INVALID_FILE_ERROR = 0;			// 파일이 없거나 손상된 파일
	public static final int OPEN_MEMORY_ERROR = -1;			// Open시 메모리 오류
	public static final int OPEN_INTERRUPT_ERROR = -2;		// Open시 인터럽트 오류
	public static final int DRM_DECRYPTION_ERROR = -3;		// DRM 복호화 오류
	public static final int OPEN_DUP_ERROR = -100;			// File Open 중에 중복으로 Open이 들어올때

	/* Viewer 전달 오류코드*/
	public static final int FILE_OPEN_EXCEPTION = -10;		// JNI(엔진)오픈 Exception
	public static final int FILE_CLOSE_ERROR = -11;			// 파일닫기 오류
	public static final int CSD_PASSWORD_ERROR = -20;		// 패스워드입력오류
	public static final int DANTOTSU_PASSWORD_ERROR = -21;	// 패스워드입력오류
	public static final int SONY_PASSWORD_ERROR = -22;		// 패스워드입력오류
	public static final int DATE_EXPIRY_ERROR = -30;		// 열람기간 만료
	public static final int OPEN_COUNT_ERROR = -31;			// 열람횟수 초과
	public static final int OPEN_CANCEL_INCOMPLETE = -70;	// 오픈취소 실패
	public static final int CORUPPTED_FILE_ERROR = -80;		// 기타 알 수없는 파일오류
	public static final int UNKNOWN_FORMAT_ERROR = -99;		// 알수 없는 파일포멧
}
