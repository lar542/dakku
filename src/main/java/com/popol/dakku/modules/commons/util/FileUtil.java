package com.popol.dakku.modules.commons.util;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.popol.dakku.modules.commons.consts.VarConsts;

public class FileUtil {
	
	/**
	 * 파일 1개 삭제
	 * @param path 파일 경로
	 * @return Map 성공여부
	 */
	public static Map deleteFile(String path) {
		Map result = new HashMap<String, String>();
		if(StringUtils.isEmpty(path))
			throw new IllegalArgumentException("잘못된 파일 경로입니다!");
		File deleteFile = new File(path);
		if(deleteFile.isFile()) {
			if(deleteFile.delete()) {
				result.put("resultCode", "success");
			} else {
				result.put("resultCode", "fail");
			}
		}
		return result;
	}
	
	/**
	 * 파일 여러 개 삭제
	 * @param files
	 * @return 모든 파일 삭제 완료 여부
	 */
	public static boolean deleteFiles(List<Map> files) {
		int successCnt = 0;
		for (Map file : files) {
			String path = VarConsts.SUMMERNOTE_UPLOAD_PATH + file.get("stored_file_nm").toString();
			Map delResult = deleteFile(path);
			if(delResult.get("resultCode").toString().equals("success")) {
				successCnt++;
			}
		}
		return successCnt == files.size();
	}
	
	/**
	 * 첨부파일 1개 업로드
	 * @param multipartFile
	 * @param params	url			: 화면상 요청할 파일 url
	 * 					upload_path	: 파일이 저장될 실제 경로
	 * 					extensions	: 허용 확장자
	 * @return 파일 업로드 후 정보 반환 Map(resultCode, origin_file_nm, stored_file_nm, extension, file_size, url)
	 * @throws Exception
	 */
	public static Map<String, Object> uploadFile(MultipartFile multipartFile, Map params) throws Exception {
		Map<String, Object> fileMap = new HashMap<String, Object>();
		
		Long file_size = multipartFile.getSize();
		
		if(multipartFile.isEmpty() || file_size <= 0) {
			fileMap.put("resultCode", "isEmpty");
			return fileMap;
		}
		
		//디렉토리
		String path = (String) params.get("upload_path");
		File dir = new File(path);
		if(!dir.isDirectory()) {
			dir.mkdirs();
		}
		
		//허용 확장자
		String[] exts = (String[]) params.get("extensions");
		if (exts == null) {
			String [] defaultExts = {".xls",".xlsx",".doc",".docx",".hwp",".pdf",".zip",".jpg",".jpeg",".png",".gif",".bmp"};
			exts = defaultExts;
		}
		
		//파일 정보
		String origin_file_nm = multipartFile.getOriginalFilename();
		String extension = origin_file_nm.substring(origin_file_nm.lastIndexOf("."));
		
		boolean isNoPass = true;
		for (String ext : exts) {
			if(extension.equalsIgnoreCase(ext)) {
				isNoPass = false;
				break;
			}
		}
		if(isNoPass) {
			fileMap.put("resultCode", "noExt");
			return fileMap;
		}
		
		String stored_file_nm = getUniqueName(extension, path);
		File targetFile = new File(path, stored_file_nm);
		
		try(InputStream fileData = multipartFile.getInputStream();){
			FileUtils.copyInputStreamToFile(fileData, targetFile); //파일 저장
			fileMap.put("origin_file_nm", origin_file_nm);
			fileMap.put("stored_file_nm", stored_file_nm);
			fileMap.put("extension", extension);
			fileMap.put("file_size", file_size);
			fileMap.put("url", params.get("url") + stored_file_nm);
			fileMap.put("resultCode", "success");
		} catch (Exception e) {
			FileUtils.deleteQuietly(targetFile); //저장된 파일 삭제
			fileMap.put("resultCode", "error");
			e.printStackTrace();
		}
		
		return fileMap;
	}
	
	public static String getUniqueName(String extension, String uploadPath) {
		File dir = new File(uploadPath);
		String uniqueName = null;
		File file = null;
		do {
			uniqueName = System.nanoTime() + extension;
			file = new File(dir, uniqueName);
		} while (file.exists());
		return uniqueName;
	}
	
}
