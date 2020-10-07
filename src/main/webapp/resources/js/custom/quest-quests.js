$('.add').click(function(){
	var type = $(this).attr('btn-type');
	var html = '<tr><td colspan="3"><textarea class="form-control" rows="3"></textarea><button class="btn btn-dark btn-sm mt-2 cancel-btn" btn-type="'+type+'">삭제</button></td></tr><tr class="save-tr"><td colspan="3"><button class="btn btn-primary btn-sm mr-1 mt-2 save" btn-type="'+type+'">저장</button></td></tr>';
	var tbody = $(this).parents('table').find('tbody');
	tbody.find('.save-tr').detach();
	tbody.append(html);
});
$(document).on('click', '.cleared', function(){
	var btn = $(this);
	var id = btn.attr('qc-id');
	var header = $("meta[name='_csrf_header']").attr('content');
	var token = $("meta[name='_csrf']").attr('content');
	$.ajax({
		type: 'POST',
		url: '/auth/cleared/quest',
		data: 'qcId=' + id,
		beforeSend: function(xhr){
			xhr.setRequestHeader(header, token);
		},
		success: function(res){
			var html = '<div class="bg-success text-white p-1 rotate-n-15 d-inline-block float-right">Cleared!</div>';
			btn.parents('tr').children().eq(1).html(html);
			btn.attr('disabled', 'disabled');
		}
	});
});
function getQuests(th, flag){
	var type = th.attr('btn-type');
	var tbody = th.parents('table').find('tbody');
	$.ajax({
		type: 'GET',
		url: '/auth/get/quest',
		data: 'qDiv=' + type,
		success: function(res){
			if(res.length == 0){
				alert('작성한 퀘스트가 없습니다');
				th.prev('button').attr('disabled', false);
				return;
			}
			var html = '';
			if(flag == 'get'){
				html = getMakeHtml(res, type);
			} else {
				html = cancelMakeHtml(res);
			}
			tbody.html(html);
		}
	});
}
$('.get').click(function(){
	$(this).prev('button').attr('disabled', true);
	getQuests($(this), 'get');
});
$(document).on('click', '.cancel', function(){
	$(this).parents('table').find('.add').attr('disabled', false);
	getQuests($(this), 'cancel');
});
$(document).on('click', '.cancel-btn', function(){
	var _this = $(this);
	var cnt = _this.parents('tbody').find('textarea').length;
	if(cnt == 1){
		$('.save-tr').detach();
	}
	_this.parents('tr').detach();
});
$(document).on('click', '.save', function(){
	var tbody = $(this).parents('tbody');
	var textareas = tbody.find('textarea');
	var data = [];
	for(var i = 0; i < textareas.length; i++){
		var value = textareas[i].value;
		if(value.length == 0){
			alert('퀘스트 내용이 비어있습니다!');
			return;
		}
		data.push(value);
	}
	var type = $(this).attr('btn-type');
	if(!(type == 'D' || type == 'W' || type == 'M')){
		alert('잘못된 요청입니다!');
		return;
	} else {
		var header = $("meta[name='_csrf_header']").attr('content');
		var token = $("meta[name='_csrf']").attr('content');
		$.ajax({
			type: 'POST',
			url: '/auth/add/quest',
			data: {detail: data, qDiv: type},
			traditional: true,
			beforeSend: function(xhr){
				xhr.setRequestHeader(header, token);
			},
			success: function(res){
				if(res.resultCode == 'N'){
					alert('잘못된 요청입니다!');
					return;
				} else if(res.resultCode == 'ERROR'){
					alert('[에러] 잠시 후 다시 시도해주세요!');
					return;
				} else if(res.resultCode == 'Y') {
					var list = res.list;
					var html = cancelMakeHtml(list);
					tbody.html(html);
				}
			}
		});
	}
});
$(document).on('click', '.edit', function(){
	var list = [];
	var trs = $(this).parents('tbody').find('tr');
	for(var i = 0; i < trs.length - 1; i++){
		var tr = trs.eq(i);
		var qc_id = tr.find('input[name="qc_id"]').val();
		var qc_content = tr.find('textarea').val();
		if(qc_content.trim().length == 0) {
			alert('퀘스트 내용이 비어있습니다!');
			return;
		}
		var deleted = tr.find('input[name="deleted"]').is(':checked');
		var cleared = tr.find('input[name="cleared"]').is(':checked');
		var qc_compl_yn = 'N';
		var flag = 'U';
		if(deleted) flag = 'D';
		if(cleared) qc_compl_yn = 'Y'
		list.push({
			'qc_id': qc_id,
			'flag': flag,
			'qc_content': qc_content,
			'qc_compl_yn': qc_compl_yn
		});
	}
	var header = $("meta[name='_csrf_header']").attr('content');
	var token = $("meta[name='_csrf']").attr('content');
	var q_div = $(this).attr('btn-type');
	if(q_div.trim().length == 0 || !(q_div == 'D' || q_div == 'W' || q_div == 'M')) {
		alert('잘못된 요청입니다!');
		return;
	}
	$.ajax({
		type: 'POST',
		url: '/auth/modify/quest',
		data: {'list': JSON.stringify(list), 'q_div': q_div},
		traditional: true,
		beforeSend: function(xhr){
			xhr.setRequestHeader(header, token);
		},
		success: function(res){
			$('.cancel').trigger('click');
		}
	});
});
function cancelMakeHtml(list){
	var html = '';
	for(var i = 0; i < list.length; i++) {
		html += '<tr><td><span>' + list[i].qc_content + '</span></td>';
		html += '<td>'
		if(list[i].qc_compl_yn == 'Y'){
			html += '<div class="bg-success text-white p-1 rotate-n-15 d-inline-block float-right">Cleared!</div>';
		}
		html += '</td><td><button class="btn btn-success btn-circle btn-sm float-right cleared" qc-id="' + list[i].qc_id + '"';
		if(list[i].qc_compl_yn == 'Y'){
			html += ' disabled="disabled"';
		}
		html += '><i class="fas fa-check"></i></button></td></tr>';
	}
	return html;
}
function getMakeHtml(list, type){
	var html = '';
	for(var i = 0; i < list.length; i++) {
		html += '<tr><td colspan="3"><textarea class="form-control" rows="3">' + list[i].qc_content + '</textarea><div class="form-group"><div class="form-check form-check-inline">';
		html += '<input class="form-check-input" type="checkbox" value="Y" name="cleared" id="cleared' + list[i].qc_id + '"';
		if(list[i].qc_compl_yn == 'Y') html += ' checked'
		html += '><label class="form-check-label" for="cleared' + list[i].qc_id + '">클리어</label></div><div class="form-check form-check-inline">'
		html += '<input class="form-check-input deleted" type="checkbox" value="Y" name="deleted" id="deleted' + list[i].qc_id + '">';
		html += '<label class="form-check-label" for="deleted' + + list[i].qc_id + '">삭제</label></div>';
		html += '<input type="hidden" name="qc_id" value="'+list[i].qc_id+'"></div></td></tr>';
	}
	html += '<tr><td colspan="3"><button class="btn btn-primary btn-sm mr-1 edit" btn-type='+type+'>저장</button><button class="btn btn-dark btn-sm cancel" btn-type='+type+'>취소</button></td></tr>';
	return html;
}
$('.btn-date').click(function(){
	var title = $(this).attr('data-original-title');
	var cnt = title.substring(12, title.length - 1);
	if(Number(cnt) == 0) {
		alert('완료한 퀘스트가 없습니다!');
		return
	}
	var date = title.substr(0, 10).split('-');
	if(!(date[0].length == 4 && date[1].length == 2 && date[2].length == 2)){
		alert('잘못된 요청입니다!');
		return;
	}
	$.ajax({
		type: 'GET',
		url: '/auth/get/cleared/quest',
		data: {'year': date[0], 'month': date[1], 'dayOfMonth': date[2]},
		success: function(res){
			if(res == 'null' || res.length == 0){
				alert('잘못된 요청입니다!');
				return;
			}
			var html = '';
			for(var i = 0; i < res.length; i++){
				html += '<tr><td>' + res[i].q_div_nm + '</td><td>' + res[i].qc_content + '</td><td>' + res[i].completed_at + '</td></tr>'; 
			}
			$('#completed-quests-tbody').html(html);
			$('#questModal').modal('show');
		}
	});
});
$('#questModal').on('hide.bs.modal', function(){
	$('#completed-quests-tbody').empty();
});