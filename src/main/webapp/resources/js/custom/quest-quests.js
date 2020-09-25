$('.add').click(function(){
	var type = $(this).attr('btn-type');
	var html = '<tr><td colspan="3"><textarea class="form-control" rows="3"></textarea><button class="btn btn-primary btn-sm mt-2 save" btn-type="'+type+'">저장</button></td></tr>';
	var tbody = $(this).parents('table').find('tbody');
	tbody.find('.save').detach();
	tbody.append(html);
});
$('.cleared').click(function(){
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
$(document).on('click', '.save', function(){
	var tbody = $(this).parents('tbody');
	var textareas = tbody.find('textarea');
	var data = [];
	for(var i = 0; i < textareas.length; i++){
		data.push(textareas[i].value);
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
function cancelMakeHtml(list){
	var html = '';
	for(var i = 0; i < list.length; i++) {
		html += '<tr><td><span>' + list[i].qc_content + '</span><button class="btn btn-info btn-circle btn-sm"><i class="fas fa-info-circle"></i></button></td>';
		html += '<td>'
		if(list[i].qc_compl_yn == 'Y'){
			html += '<div class="bg-success text-white p-1 rotate-n-15 d-inline-block float-right">Cleared!</div>';
		}
		html += '</td><td><button class="btn btn-success btn-circle btn-sm float-right"';
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
		html += '<input class="form-check-input cleared" type="checkbox" value="Y" name="cleared" id="cleared' + list[i].qc_id + '">'
		html += '<label class="form-check-label" for="cleared' + list[i].qc_id + '">클리어</label></div><div class="form-check form-check-inline">'
		html += '<input class="form-check-input deleted" type="checkbox" value="Y" name="deleted" id="deleted' + list[i].qc_id + '">';
		html += '<label class="form-check-label" for="deleted' + + list[i].qc_id + '">삭제</label></div></div></td></tr>';
	}
	html += '<tr><td colspan="3"><button class="btn btn-primary btn-sm mr-1 edit">저장</button><button class="btn btn-dark btn-sm cancel" btn-type='+type+'>취소</button></td></tr>';
	return html;
}