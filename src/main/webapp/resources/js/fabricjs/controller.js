(function(){
	var $ = function(id){return document.getElementById(id)};
	
	var circleEl = $('circle'),
		squareEl = $('square')
	;
	
	circleEl.onclick = function(){
		var coord = getRandomLeftTop();
		
		canvas.add(new fabric.Circle({
			left: coord.left,
			top: coord.top,
			fill: getRandomColor(),
			radius: 50
	    }));
	}
	
	squareEl.onclick = function(){
		var coord = getRandomLeftTop();
		
		canvas.add(new fabric.Rect({
			left: coord.left,
			top: coord.top,
			fill: getRandomColor(),
			width: 100,
			height: 100
		}));
	}
	
	
	var addShape = function(shapeName){
		var coord = getRandomLeftTop();
		
		fabric.loadSVGFromURL('' + shapeName + '.svg', function(objects, options){
		});
	}
})();

$('.sticker-btn').click(function(){
	var type = $(this).attr('type');
	var name = $(this).find('p').text();
	$.ajax({
		type: 'GET',
		url: '/stickers',
		data: {'type': type, 'name': name},
		success: function(res){
			var html = '';
			for(i = 0; i < res.length; i+=12){
				html += stickerModalHtml('/resources/img/sticker/'+type+'/'+name+'/', res.slice(i, i+12));
			}
			html += '</div>';
			$('#modal-body').html(html);
			$('#stickerModalLabel').text(type + " stcker : " + name + ' / made by ');
			$('#stickerModal').modal('show');
		}
	});
});

function stickerModalHtml(path, arr){
	var html = '<div class="row">';
	for(var i = 0; i < arr.length; i++) {
		html += '<div class="col-1"><button class="btn"><img src="'+path+arr[i]+'" class="img-fluid"></button></div>';
	}
	html += '</div>';
	return html;
}