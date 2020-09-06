function extensionsChk(file){
	if(!/\.(gif|jpg|jpeg|png)$/i.test(file.name)){
		alert('gif, jpg, png 파일만 선택해 주세요.\n\n현재 파일 : ' + file[0].name);
		return false;
	}
	return true;
}

var files = [];

function uploadSummernoteImageFile(file, editor) {
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
				var img = document.createElement('img');
				img.src = res.url;
				img.className = 'img-fluid';
				$(editor).summernote('insertNode', img);
			}
		},
		error: function(req, status, error) {
			alert(req);
		}
	});
}

function uploadSummernoteImageFileDelete(path){
	for(var i = 0; i < files.length; i++){
		if(files[i].url == path){
			files[i]['deleted'] = 'Y';
		}
	}
}