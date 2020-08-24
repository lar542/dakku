	$('#summernote').summernote({
		codeviewFilter: false,
		codeviewIframeFilter: true,
		height: 300,
		focus: true,
		lang: "ko-KR",
		toolbar: [
			['fontname', ['fontname']],
			['fontsize', ['fontsize']],
			['style', ['bold', 'italic', 'underline', 'strikethrough', 'color', 'superscript', 'subscript', 'clear']],
			['para', ['paragraph', 'ul', 'ol', 'height']],
			['insert', ['hr', 'table', 'link', 'picture', 'video']],
			['view', ['codeview', 'help']]
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
	
	function extensionsChk(file){
		if(!/\.(gif|jpg|jpeg|png)$/i.test(file.name)){
			alert('gif, jpg, png 파일만 선택해 주세요.\n\n현재 파일 : ' + file[0].name);
			return false;
		}
		return true;
	}
	
	var files = [];
	
	function uploadSummernoteImageFile(file, editor) {
		var reader  = new FileReader();
		reader.onloadend = function(){
			var img = $("<img>").attr({src: reader.result, class: "img-fluid"});
			$(TariffHTMLId).summernote("insertNode", img[0]);
		}
		var url = '/upload/img';
		var data = new FormData();
		data.append('file', file);
		$.ajax({
			data: data,
			type: 'POST',
			url: url,
			contentType: false,
			processData: false,
			success: function(res) {
				if(res.resultCode == "isEmpty") {
					alert('빈 파일 입니다');
					return;
				}
				if(res.resultCode == "noExt") {
					alert('허용되지 않은 파일 확장자 입니다');
					return;
				}
				if(res.resultCode == "error") {
					alert('파일 업로드 에러');
					return;
				}
				if(res.resultCode == "success") {
					files.push(res);
					$(editor).summernote('insertImage', res.url);
				}
			},
			error: function(req, status, error) {
				alert(req);
			}
		});
	}
	
	function uploadSummernoteImageFileDelete(path){
		var header = $("meta[name='_csrf_header']").attr('content');
		var token = $("meta[name='_csrf']").attr('content');
		
		var data = {};
		for(var i = 0; i < files.length; i++){
			if(files[i].url == path){
				data = {
					stored_file_nm: files[i].stored_file_nm,
					f_id: files[i].f_id
				}		
			}
		}
		
		$.ajax({
			data: data,
			type: 'POST',
			url: '/delete/img',
			async: false,
			beforeSend: function(xhr){
				xhr.setRequestHeader(header, token);
			},
			success: function(res) {
				if(res.resultCode == 'fail'){
					alert('파일 삭제 실패');
				} else {
					for(var i = 0; i < files.length; i++){
						if(files[i].url == path){
							files.splice(i, 1);
						}
					}
				}
			}
		});
	}
	
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
