package com.popol.dakku.modules.web.quest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.popol.dakku.modules.commons.auth.UserDetailsHelper;
import com.popol.dakku.modules.commons.auth.vo.UserVO;
import com.popol.dakku.modules.commons.util.HtmlUtil;
import com.popol.dakku.modules.web.post.PostMapper;

@Controller
@Transactional
public class QuestController {
	
	@Resource(name = "postMapper")
	private PostMapper postMapper;
	
	@Resource(name = "questMapper")
	private QuestMapper questMapper;
	
	@GetMapping("/quests")
	public String list(Model model) {
		UserVO userVO = (UserVO) UserDetailsHelper.getAuthenticatedUser();
		Map params = new HashMap();
		params.put("u_id", userVO.getU_id());
		
		//퀘스트 로그
		LocalDate today = LocalDate.now();
		int todayDay = today.getDayOfWeek().getValue();
		LocalDate start_date = today.minusDays(364 + todayDay - 1);
		LocalDate end_date = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
		params.put("start_date", start_date);
		params.put("end_date", end_date);
		//이번주를 제외한 주
		List<Map> log = questMapper.getQuestLog(params);
		model.addAttribute("preWeek", HtmlUtil.dateHtml(log));
		//이번주
		params.put("start_date", end_date.plusDays(1));
		LocalDate nextDay = today.plusDays(1);
		params.put("end_date", nextDay);
		List thisWeek_1 = questMapper.getQuestLog(params);
		params.put("start_date", nextDay.plusDays(1));
		params.put("end_date", today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
		List thisWeek_2 = questMapper.getQuestLog(params);
		model.addAttribute("thisWeek", HtmlUtil.thisWeekHtml(thisWeek_1, false) + HtmlUtil.thisWeekHtml(thisWeek_2, true));
		
		//다이어리
		
		//일일
		params.put("q_div", "D");
		LocalDate tomorrow = today.plusDays(1);
		params.put("q_d_date", tomorrow);
		List daily = questMapper.getQuestList(params);
		model.addAttribute("daily", daily);
		//주간
		params.put("q_div", "W");
		LocalDate monDay = today.plusDays(8 - today.getDayOfWeek().getValue());
		params.put("q_wm_end", monDay);
		List weekly = questMapper.getQuestList(params);
		model.addAttribute("weekly", weekly);
		//월간
		params.put("q_div", "M");
		LocalDate nextFirstDate = today.with(TemporalAdjusters.firstDayOfNextMonth());
		params.put("q_wm_end", nextFirstDate);
		List monthly = questMapper.getQuestList(params);
		model.addAttribute("monthly", monthly);
		return "quest/quest";
	}
	
	private void setDate() {
		
	}
	
	@PostMapping(value = "/auth/add/quest", produces = "application/json;charset=utf8")
	@ResponseBody
	public Map add(@RequestParam String[] detail, @RequestParam String qDiv) {
		Map result = new HashMap();
		UserVO userVO = (UserVO) UserDetailsHelper.getAuthenticatedUser();
		Map params = new HashMap();
		params.put("u_id", userVO.getU_id());
		
		LocalDate today = LocalDate.now();
		params.put("q_div", qDiv);
		if(qDiv.equals("D")) {
			LocalDate tomorrow = today.plusDays(1);
			params.put("q_d_date", tomorrow);
		} else if(qDiv.equals("W")) {
			LocalDate monDay = today.plusDays(8 - today.getDayOfWeek().getValue());
			params.put("q_wm_start", today);
			params.put("q_wm_end", monDay);
		} else if(qDiv.equals("M")) {
			LocalDate nextFirstDate = today.with(TemporalAdjusters.firstDayOfNextMonth());
			params.put("q_wm_start", today);
			params.put("q_wm_end", nextFirstDate);
		} else {
			result.put("resultCode", "N");
			return result;
		}
		//이미 저장되어 있는지 확인
		String q_id = questMapper.getQuestInfo(params);
		if(q_id != null) {
			params.put("q_id", q_id);
		} else {
			questMapper.addQuest(params);
		}
		result.put("resultCode", "ERROR");
		
		q_id = params.get("q_id").toString();
		if(q_id != null) {
			params.put("detail", detail);
			questMapper.addQuestDetail(params);
			List qList = questMapper.getQuestList(params);
			result.put("list", qList);
			result.put("resultCode", "Y");
		}
		return result;
	}
	
	@PostMapping(value = "/auth/cleared/quest", produces = "application/text;charset=utf8")
	@ResponseBody
	public void cleared(@RequestParam Long qcId) {
		questMapper.clearedOneQuest(qcId);
	}
	
	@GetMapping(value = "/auth/get/quest", produces = "application/json;charset=utf8")
	@ResponseBody
	public List quests(@RequestParam String qDiv) {
		UserVO userVO = (UserVO) UserDetailsHelper.getAuthenticatedUser();
		Map params = new HashMap();
		params.put("u_id", userVO.getU_id());
		params.put("q_div", qDiv);
		
		LocalDate today = LocalDate.now();
		if(qDiv.equals("D")) {
			LocalDate tomorrow = today.plusDays(1);
			params.put("q_d_date", tomorrow);
		} else if(qDiv.equals("W")) {
			LocalDate monDay = today.plusDays(8 - today.getDayOfWeek().getValue());
			params.put("q_wm_start", today);
			params.put("q_wm_end", monDay);
		} else if(qDiv.equals("M")) {
			LocalDate nextFirstDate = today.with(TemporalAdjusters.firstDayOfNextMonth());
			params.put("q_wm_start", today);
			params.put("q_wm_end", nextFirstDate);
		}
		return questMapper.getQuestList(params);
	}
	
	@PostMapping("/auth/modify/quest")
	@ResponseBody
	public void modify(@RequestParam List list) {
		questMapper.modifyQuest(list);
	}
	
}
























