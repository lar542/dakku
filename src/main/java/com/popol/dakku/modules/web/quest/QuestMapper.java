package com.popol.dakku.modules.web.quest;

import java.util.List;
import java.util.Map;

public interface QuestMapper {
	
	/**
	 * 퀘스트 로그 조회
	 */
	public List getQuestLog(Long u_id);
	
	/**
	 * 다이어리 조회
	 */
	public List getDiaryList(Long u_id);

	/**
	 * 일일/주간/월간 퀘스트 조회 
	 */
	public List getQuestList(Map map);
	
	/**
	 * 퀘스트 추가
	 */
	public void addQuest(Map map);
	
	/**
	 * 퀘스트 항목 추가 
	 */
	public void addQuestDetail(Map map);
	
	/**
	 * 기존에 저장되어 있는지 확인
	 */
	public String getQuestInfo(Map map);
	
	/**
	 * 퀘스트 1개 완료
	 */
	public void clearedOneQuest(Long qc_id);
	
	/**
	 * 퀘스트 수정
	 */
	public void modifyQuest(List list);
	
	/**
	 * 퀘스트 수 확인
	 */
	public List getQuestCnt(Map map);
}
