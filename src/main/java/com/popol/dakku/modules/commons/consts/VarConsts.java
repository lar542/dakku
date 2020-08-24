package com.popol.dakku.modules.commons.consts;

public class VarConsts {
	
	//썸머노트 이미지 요청 URL
	public static String SUMMERNOTE_URL;
	//썸머노트 이미지 로컬 업로드 위치
	public static String SUMMERNOTE_UPLOAD_PATH;
	
	public VarConsts(String summernote_url, String summernote_upload_path) {
		VarConsts.SUMMERNOTE_URL = summernote_url;
		VarConsts.SUMMERNOTE_UPLOAD_PATH = summernote_upload_path;
	}
}
