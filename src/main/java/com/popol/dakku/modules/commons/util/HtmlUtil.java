package com.popol.dakku.modules.commons.util;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class HtmlUtil {

	public static String comments(List<Map> comments, String pId, String uId) {
		StringBuffer buffer = new StringBuffer();
		for (Map c : comments) {
			if(c.get("cm_parent").toString().equals("0")) {
				buffer.append("<div class=\"border pb-2 mb-3\"><div class=\"row dropdown no-arrow c-row\">");
				buffer.append("<a class=\"nav-link dropdown-toggle col-sm-6 col-md-8 col-lg-8 col-xl-10\" href=\"#\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\" id=\"commentDropdown").append(c.get("cm_id")).append("\">");
				buffer.append("<i class=\"fas fa-2x c-icon fa-").append(c.get("icon_class")).append("\" style=\"color: ").append(c.get("icon_color")).append("\"></i>");
				buffer.append("<span class=\"c-nick\">").append(c.get("nick")).append("</span>");
				if(uId.equals(c.get("u_id"))) {
					buffer.append("<span class=\"bg-primary rounded-sm text-light text-xs\">작성자</span>");
				}
				buffer.append("</a>");
				buffer.append("<div class=\"dropdown-menu shadow\" aria-labelledby=\"commentDropdown").append(c.get("cm_id")).append("\">");
				buffer.append("<a class=\"dropdown-item\" href=\"#\">작성자 검색</a>");
				buffer.append("<a class=\"dropdown-item\" href=\"#\">로그 보기</a>");
				buffer.append("</div>");
				buffer.append("<div class=\"col text-center\">");
				buffer.append("<div class=\"btn-group\" role=\"group\">");
				if(c.get("emotion").toString().equals("G")) {
					buffer.append("<button type=\"button\" class=\"btn btn-outline-danger btn-sm cmtemotion active\" data-state=\"G\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-heart\">").append(c.get("cm_good")).append("</i></button>");
				} else {
					buffer.append("<button type=\"button\" class=\"btn btn-outline-danger btn-sm cmtemotion\" data-state=\"G\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-heart\">").append(c.get("cm_good")).append("</i></button>");
				}
				if(c.get("emotion").toString().equals("B")) {
					buffer.append("<button type=\"button\" class=\"btn btn-outline-secondary btn-sm cmtemotion active\" data-state=\"B\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-heart-broken\">").append(c.get("cm_bad")).append("</i></button>");
				} else {
					buffer.append("<button type=\"button\" class=\"btn btn-outline-secondary btn-sm cmtemotion\" data-state=\"B\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-heart-broken\">").append(c.get("cm_bad")).append("</i></button>");
				}
				buffer.append("<button type=\"button\" class=\"btn btn-primary btn-sm addCmt\" data-toggle=\"modal\" data-target=\"#commentModal\" data-parent=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-plus\"></i></button>");
				if(c.get("deleted_yn").toString().equals("N") && c.get("u_id").toString().equals(uId)) {
					buffer.append("<button type=\"button\" class=\"btn btn-info btn-sm editCmt\" data-toggle=\"modal\" data-target=\"#commentModal\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-edit\"></i></button>");
					buffer.append("<button type=\"button\" class=\"btn btn-danger btn-sm removeCmt\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-trash-alt\"></i></button>");
				}
				buffer.append("</div></div></div>");
				buffer.append("<div class=\"col\">");
				buffer.append("<div class=\"c-content\">");
				buffer.append("<div class=\"c-date\">");
				if(c.get("updated_at").toString().equals("null")) {
					buffer.append(c.get("created_at"));
				} else {
					buffer.append(c.get("updated_at")).append(" (수정됨)");
				}
				buffer.append("</div>");
				if(c.get("deleted_yn").toString().equals("N")) {
					if(Integer.parseInt(c.get("cm_bad").toString()) < 10) {
						buffer.append("<span>").append(c.get("cm_content")).append("</span>");
					} else {
						buffer.append("<span>[블라인드 처리된 댓글입니다]</span>");
					}
				} else {
					buffer.append("<span>[삭제된 댓글입니다]</span>");
				}
				if(Integer.parseInt(c.get("cm_bad").toString()) >= 10) {
					buffer.append("<button type=\"button\" class=\"btn btn btn-warning btn-sm\" onclick=\"javascript: $(this).next().css({'display': 'block'});\">블라인드 댓글 보기</button>");
				}
				if(c.get("deleted_yn").toString().equals("N") && Integer.parseInt(c.get("cm_bad").toString()) >= 10){
					buffer.append("<span style=\"display: none;\">").append(c.get("cm_content")).append("</span>");
				}
				buffer.append("</div></div></div>");
			} else {
				buffer.append("<div class=\"cc-comment\">");
				buffer.append("<div class=\"row dropdown no-arrow c-row\">");
				buffer.append("<a class=\"nav-link dropdown-toggle col-sm-6 col-md-8 col-lg-8 col-xl-10\" href=\"#\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\" id=\"commentDropdown").append(c.get("cm_id")).append("\">");
				buffer.append("<i class=\"fas fa-hand-point-right fa-2x text-primary cc-icon-arrow\"></i>");
				buffer.append("<i class=\"fas fa-2x cc-icon fa-").append(c.get("icon_class")).append("\" style=\"color: ").append(c.get("icon_color")).append("\"></i>");
				buffer.append("<span class=\"cc-nick\">").append(c.get("nick")).append("</span>");
				if(uId.equals(c.get("u_id"))) {
					buffer.append("<span class=\"bg-primary rounded-sm text-light text-xs\">작성자</span>");
				}
				buffer.append("<div class=\"dropdown-menu shadow\" aria-labelledby=\"commentDropdown").append(c.get("cm_id")).append("\">");
				buffer.append("<a class=\"dropdown-item\" href=\"#\">작성자 검색</a>");
				buffer.append("<a class=\"dropdown-item\" href=\"#\">로그 보기</a>");
				buffer.append("</div>");
				buffer.append("<div class=\"col text-center\">");
				buffer.append("<div class=\"btn-group\" role=\"group\">");
				if(c.get("emotion").toString().equals("G")) {
					buffer.append("<button type=\"button\" class=\"btn btn-outline-danger btn-sm cmtemotion active\" data-state=\"G\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-heart\">").append(c.get("cm_good")).append("</i></button>");
				} else {
					buffer.append("<button type=\"button\" class=\"btn btn-outline-danger btn-sm cmtemotion\" data-state=\"G\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-heart\">").append(c.get("cm_good")).append("</i></button>");
				}
				if(c.get("emotion").toString().equals("B")) {
					buffer.append("<button type=\"button\" class=\"btn btn-outline-secondary btn-sm cmtemotion active\" data-state=\"B\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-heart-broken\">").append(c.get("cm_bad")).append("</i></button>");
				} else {
					buffer.append("<button type=\"button\" class=\"btn btn-outline-secondary btn-sm cmtemotion\" data-state=\"B\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-heart-broken\">").append(c.get("cm_bad")).append("</i></button>");
				}
				if(c.get("deleted_yn").toString().equals("N") && c.get("u_id").toString().equals(uId)) {
					buffer.append("<button type=\"button\" class=\"btn btn-info btn-sm editCmt\" data-toggle=\"modal\" data-target=\"#commentModal\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-edit\"></i></button>");
					buffer.append("<button type=\"button\" class=\"btn btn-danger btn-sm removeCmt\" data-id=\"").append(c.get("cm_id")).append("\"><i class=\"fas fa-trash-alt\"></i></button>");
				}
				buffer.append("</div></div></div>");
				buffer.append("<div class=\"row justify-content-center\">");
				buffer.append("<div class=\"col-11\">");
				buffer.append("<div class=\"c-date\">");
				if(c.get("updated_at").toString().equals("null")) {
					buffer.append(c.get("created_at"));
				} else {
					buffer.append(c.get("updated_at")).append(" (수정됨)");
				}
				buffer.append("</div>");
				if(c.get("deleted_yn").toString().equals("N")) {
					if(Integer.parseInt(c.get("cm_bad").toString()) < 10) {
						buffer.append("<span>").append(c.get("cm_content")).append("</span>");
					} else {
						buffer.append("<span>[블라인드 처리된 댓글입니다]</span>");
					}
				} else {
					buffer.append("<span>[삭제된 댓글입니다]</span>");
				}
				if(Integer.parseInt(c.get("cm_bad").toString()) >= 10) {
					buffer.append("<button type=\"button\" class=\"btn btn btn-warning btn-sm\" onclick=\"javascript: $(this).next().css({'display': 'block'});\">블라인드 댓글 보기</button>");
				}
				if(c.get("deleted_yn").toString().equals("N") && Integer.parseInt(c.get("cm_bad").toString()) >= 10){
					buffer.append("<span style=\"display: none;\">").append(c.get("cm_content")).append("</span>");
				}
				buffer.append("</div></div></div>");
			}
		}
		return buffer.toString();
	}
	
	public static String dateHtml(List<Map> log) {
		StringBuffer buffer = new StringBuffer("<div class=\"btn-group-vertical mr-1\">");
		for (int i = 0; i < log.size(); i++) {
			buffer.append("<button data-toggle=\"tooltip\" data-placement=\"top\" title=\"")
				.append(log.get(i).get("dates")).append(" (").append(log.get(i).get("cnt")).append(")\" ");
			if(log.get(i).get("cnt").toString().equals("0")) {
				buffer.append("class=\"btn mb-1 btn-light btn-date\">");
			} else {
				buffer.append("class=\"btn mb-1 btn-primary btn-date\">");
			}
			if((i + 1) % 7 == 0) {
				buffer.append("</div>");
				if(i < log.size() - 1) {
					buffer.append("<div class=\"btn-group-vertical mr-1\">");
				}
			}
		}
		return buffer.toString();
	}
	
	public static String thisWeekHtml(List<Map> log, boolean flag) {
		String visibility = " ";
		if(flag) {
			visibility = " style=\"visibility: hidden;\" ";
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < log.size(); i++) {
			buffer.append("<button data-toggle=\"tooltip\" data-placement=\"top\" title=\"")
				.append(log.get(i).get("dates")).append(" (").append(log.get(i).get("cnt")).append(")\"").append(visibility);
			if(log.get(i).get("cnt").toString().equals("0")) {
				buffer.append("class=\"btn mb-1 btn-light btn-date\">");
			} else {
				buffer.append("class=\"btn mb-1 btn-primary btn-date\">");
			}
		}
		return buffer.toString();
	}
}
