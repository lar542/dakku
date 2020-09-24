package com.popol.dakku.modules.commons.auth.vo;

import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Builder;
import lombok.Data;

@Alias("UserVO")
@Data
public class UserVO {

	private Long u_id;
	private String email;
	private String nick;
	private Integer exp;
	private String log_open_yn;
	private Integer cheer;
	private Integer stamp;
	private String nick_chg_yn;
	private String deleted_yn;
	private String sns_type;
	private String sns_id;
	private List roles;
	private String lv_code;
	private String icon_class;
	private String icon_color;
	private Integer lv;
	private Integer next_required_exp;
	private Integer additional_exp;
	
	@Builder
	public UserVO(Long u_id, String email, String nick,
				Integer exp, String log_open_yn, Integer cheer, Integer stamp, String nick_chg_yn,
				String deleted_yn, String sns_type, String sns_id, List roles, String lv_code) {
		this.u_id = u_id;
		this.email = email;
		this.nick = nick;
		this.exp = exp;
		this.log_open_yn = log_open_yn;
		this.cheer = cheer;
		this.stamp = stamp;
		this.nick_chg_yn = nick_chg_yn;
		this.deleted_yn = deleted_yn;
		this.sns_type = sns_type;
		this.sns_id = sns_id;
		this.roles = roles;
		this.lv_code = lv_code;
	}

	public UserVO() {}
	
}
