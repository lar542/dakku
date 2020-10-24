(function(global){
	
	function capitalize(string) {
		return string.charAt(0).toUpperCase() + string.slice(1);
	}
	
	function pad(str, length) {
		while(str.length < length) {
			str = '0' + str;
		}
		return str;
	}
	
	var getRandomInt = fabric.util.getRandomInt;
	
	function getRandomColor() {
		return (
			'#' + 
			pad(getRandomInt(0, 255).toString(16), 2) + 
			pad(getRandomInt(0, 255).toString(16), 2) + 
			pad(getRandomInt(0, 255).toString(16), 2) 
		);
	}
	
	function getRandomNum(min, max) {
		return Math.random() * (max - min) + min;
	}
	
	function getRandomLeftTop() {
		var offset = 200;
		return {
			left: fabric.util.getRandomInt(0 + offset, 700 - offset),
			top: fabric.util.getRandomInt(0 + offset, 700 - offset),
		};
	}
	
	global.getRandomNum = getRandomNum;
	global.getRandomInt = getRandomInt;
	global.getRandomColor = getRandomColor;
	global.getRandomLeftTop = getRandomLeftTop;
	
})(this);