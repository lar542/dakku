$('.summernote').summernote({
	codeviewFilter: false,
	codeviewIframeFilter: true,
	height: 150,
	lang: "ko-KR",
	toolbar: [
		['fontname', ['fontname']],
		['fontsize', ['fontsize']],
		['style', ['bold', 'italic', 'underline', 'strikethrough', 'color', 'superscript', 'subscript']],
		['para', ['paragraph', 'ul', 'ol', 'height']],
		['insert', ['hr', 'table', 'link', 'picture']],
		['view', ['clear', 'codeview']]
	],
	fontNames : ['맑은 고딕','굴림','굴림체','돋움', '돋움체', '바탕', '바탕체', '궁서', 'Arial', 'Arial Black', 'Comic Sans MS', 'Courier New'],
	fontSizes : ['10', '11', '14', '16', '18', '24', '36', '50', '72'],
	callbacks: {
		onImageUpload : function(files) {
			if(extensionsChk(files[0])){
				uploadSummernoteImageFile(files[0], this);
			}
		},
		onMediaDelete : function ($target, $editable) {
			uploadSummernoteImageFileDelete($target.attr('src'));
        }
	}
});

$('#search').click(function(){
	var word = $('#searchWord').val();
	if(word.trim().length > 0){
		$('#page').val('');
		$('#searchForm').submit();
	} else {
		alert('검색어를 입력해주세요');
	}
});

$('.page-link').click(function(){
	var page = $(this).attr('data-page');
	$('#page').val(page);
	$('#searchForm').submit();
});

$('.trs').click(function(){
	var id = $(this).children().first().text();
	var action = $('#searchForm').attr('action');
	var index = action.lastIndexOf('/') + 1;
	var newAction = action.substring(0, index) + id;
	$('#searchForm').attr('action', newAction);
	$('#searchForm').submit();
});

$('#removePostBtn').click(function(){
	if(confirm('삭제하시겠습니까?')){
		$(this).parent('form').submit();
	}
});

$('.emotion').click(function(e){
	e.preventDefault();
	var state = $(this).attr('data-state');
	if(!(state == 'G' || state == 'B')){
		alert('잘못된 요청입니다!');
		return;
	}
	var btn = $(this);
	$('#emotion').val(state);
	var data = $(this).parent('form').serializeArray();
	$.ajax({
		type: 'POST',
		url: $(this).parent('form').attr('action'),
		data : data,
		success: function(res){
			if(res.emotion == 'G'){
				alert('이미 좋아요 표시한 게시글 입니다. 좋아요를 취소해주세요.');
			} else if(res.emotion == 'B'){
				alert('이미 싫어요 표시한 게시글 입니다. 싫어요를 취소해주세요.');
			} else if(res.emotion == 'A') {
				btn.addClass('btn-lg');
			} else if(res.emotion == 'C') {
				btn.removeClass('btn-lg');
			} else if(res.emotion == 'N'){
				alert('잘못된 요청 입니다!');
			}
			$('#goodCnt').text(res.G);
			$('#badCnt').text(res.B);
		}
	});
});

$(document).on('click', '.cmtemotion', function(){
	var state = $(this).attr('data-state');
	if(!(state == 'G' || state == 'B')){
		alert('잘못된 요청입니다!');
		return;
	}
	var id = $(this).attr('data-id').trim();
	if(id.length == 0 || isNaN(id)){
		alert('잘못된 요청입니다!');
		return;
	}
	var header = $("meta[name='_csrf_header']").attr('content');
	var token = $("meta[name='_csrf']").attr('content');
	var btn = $(this);
	$.ajax({
		type: 'POST',
		url: '/auth/cmtemotion',
		data : {
			'emotion': state,
			'cmId': id,
			'pId': $('#pId').text()
		},
		beforeSend: function(xhr){
			xhr.setRequestHeader(header, token);
		},
		success: function(res){
			if(res.emotion == 'G'){
				alert('이미 좋아요 표시한 댓글 입니다. 좋아요를 취소해주세요.');
			} else if(res.emotion == 'B'){
				alert('이미 싫어요 표시한 댓글 입니다. 싫어요를 취소해주세요.');
			} else if(res.emotion == 'N'){
				alert('잘못된 요청 입니다!');
			} else {
				$('#cmtCnt').text(res.cmtCnt);
				$('#comments').html(res.html);
			}
		}
	});
});

function editorClear(){
	files = [];
	$('#comment-summernote').summernote('reset');
	$('#modal-summernote').summernote('reset');
}

function commonSave(form){
	var pId = form.find('input[name="pId"]').val().trim();
	if(pId.length == 0 || isNaN(pId)) {
		alert('잘못된 요청입니다');
		return;
	}
	var txt = form.find('textarea[name="cmContent"]').val().trim();
	if(txt.length == 0 || txt == '<p><br></p>' || txt.length > 1000){
		alert('댓글은 최대 1000자까지 입력할 수 있습니다');
		return;
	}
	var fileArray = [];
	for(var i = 0; i < files.length; i++){
		fileArray.push(files[i].f_id + ":" + files[i].deleted + ":" + files[i].stored_file_nm);
	}
	form.append('<input type="hidden" name="files" value="'+fileArray.join()+'" />');
	var data = form.serializeArray();
	form.find('input[name="files"]').remove();
	$.ajax({
		type: 'POST',
		url: form.attr('action'),
		data: data,
		success: function(res){
			editorClear();
			$('#commentModal').modal('hide');
			$('#cmtCnt').text(res.cmtCnt);
			$('#comments').html(res.html);
		}
	});
}

$('#save').click(function(e){
	e.preventDefault();
	var form = $('#commentForm');
	commonSave(form);
});

$('#modal-save').click(function(e){
	e.preventDefault();
	var form = $('#modalForm');
	commonSave(form);
});


$(document).on('click', '.addCmt', function(){
	editorClear();
	$('#commentModalLabel').text('대댓글 쓰기');
	$('#modalForm').attr('action', '/auth/save/comment');
	var id = $(this).attr('data-parent');
	$('#commentModal .modal-body input[name="cmParent"]').val(id);
});

$(document).on('click', '.editCmt', function(){
	$('#commentModalLabel').text('댓글 수정');
	$('#modalForm').attr('action', '/auth/modify/comment');
	var header = $("meta[name='_csrf_header']").attr('content');
	var token = $("meta[name='_csrf']").attr('content');
	var modal = $('#commentModal .modal-body');
	var cmId = $(this).attr('data-id');
	$.ajax({
		type: 'POST',
		url: '/auth/get/comment',
		data: 'cmId=' + cmId,
		beforeSend: function(xhr){
			xhr.setRequestHeader(header, token);
		},
		success: function(res){
			if(res.resultCode == 'success'){
				editorClear();
				modal.find('input[name="pId"]').val(res.p_id);
				modal.find('input[name="cmId"]').val(res.cm_id);
				modal.find('input[name="cmParent"]').val(res.cm_parent);
				$('#modal-summernote').summernote('pasteHTML', res.cm_content);
				files = res.files;
			} else {
				alert('요청 에러!');
			}
		}
	});
});

$(document).on('click', '.removeCmt', function(){
	if(confirm('댓글을 삭제하시겠습니까?')){
		var header = $("meta[name='_csrf_header']").attr('content');
		var token = $("meta[name='_csrf']").attr('content');
		var cmId = $(this).attr('data-id');
		var pId = $('#commentForm').find('input[name="pId"]').val();
		$.ajax({
			type: 'POST',
			url: '/auth/remove/comment',
			data: {'cmId': cmId, 'pId': pId},
			beforeSend: function(xhr){
				xhr.setRequestHeader(header, token);
			},
			success: function(res){
				editorClear();
				$('#cmtCnt').text(res.cmtCnt);
				$('#comments').html(res.html);
			}
		});
	}
});

$('#commentModal').on('hide.bs.modal', function(){
	editorClear();
	var modal = $(this);
	modal.find('.modal-body input[name="cmId"]').val("");
	modal.find('.modal-body input[name="cmParent"]').val("");
});