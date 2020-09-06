$('#summernote').summernote({
	codeviewFilter: false,
	codeviewIframeFilter: true,
	height: 500,
	focus: true,
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

$('#postType').change(function(){
	$('#postState').empty();
	var url = '/state/' + $(this).val();
	$.ajax({
		type: 'GET',
		url: url,
		success: function(res){
			var options = '';
			for(var i = 0; i < res.length; i++){
				options += '<option value="' + res[i].ms_code + '">' + res[i].ms_nm + '</option>';
			}
			$('#postState').append(options);
		}
	});
});

$('#save').click(function(e){
	e.preventDefault();
	var title = $('#title').val().trim();
	if(title.length == 0 || title.length > 50){
		alert('제목을 50자 이하까지 입력해주세요 ');
		return;
	}
	var txt = $('#summernote').val().trim();
	if(txt.length == 0 || txt == '<p><br></p>'){
		alert('게시글을 입력해주세요');
		return;
	}
	var fileArray = [];
	for(var i = 0; i < files.length; i++){
		fileArray.push(files[i].f_id + ":" + files[i].deleted + ":" + files[i].stored_file_nm);
	}
	$('#postForm').append('<input type="hidden" name="files" value="'+fileArray.join()+'" />');
	var data = $('#postForm').serializeArray();
	$.ajax({
		type: 'POST',
		data: data, 
		url: $('#postForm').attr('action'),
		async: false,
		traditional : true,
		success: function(res){
			location.href = res.redirect;
		}
	});
});