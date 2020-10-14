(function() {

    var $ = function(id){return document.getElementById(id)};

    var addFont = $('add-font'),
        fontFamily = $('font-family'),
        fontWeight = $('font-weight'),
        fontItalic = $('font-italic'),
        fontUnderline = $('font-underline'),
        fontLeft = $('font-left'),
        fontCenter = $('font-center'),
        fontRight = $('font-right'),
        fontColor = $('font-color'),
        fontSize = $('font-size'),
        fontBackgroundColor = $('font-background-color');

    var fonts = ["fantasy", "cursive", "monospace", "Times New Roman", "Pacifico"];

    fonts.forEach(function(font) {
        var option = document.createElement('option');
        option.innerHTML = font;
        option.value = font;
        fontFamily.appendChild(option);
    });
    fonts.unshift('fantasy');

    addFont.onclick = function() {
        var textbox = new fabric.Textbox('Hello World!', {
            left: 50,
            top: 50,
            width: 300,
            fontWidth: 200,
            fontSize: 30,
            fontFamily: 'fantasy'
        });
    
        canvas.add(textbox).setActiveObject(textbox);
    };

    fontFamily.onchange = function() {
        canvas.getActiveObject().set("fontFamily", this.value);
        canvas.requestRenderAll();
    };

    fontWeight.onclick = function() {
        var fw = canvas.getActiveObject().get('fontWeight');
        canvas.getActiveObject().set('fontWeight', fw === 'bold' ? 'normal' : 'bold');
        canvas.requestRenderAll();
    };

    fontItalic.onclick = function() {
        var fs = canvas.getActiveObject().get('fontStyle');
        canvas.getActiveObject().set('fontStyle', fs === 'italic' ? 'normal' : 'italic');
        canvas.requestRenderAll();
    };

    fontUnderline.onclick = function() {
        canvas.getActiveObject().set('underline', !canvas.getActiveObject().get('underline'));
        canvas.requestRenderAll();
    };

    fontLeft.onclick = function() {
        canvas.getActiveObject().set('textAlign', 'left');
        canvas.requestRenderAll();
    };

    fontCenter.onclick = function() {
        canvas.getActiveObject().set('textAlign', 'center');
        canvas.requestRenderAll();
    };

    fontRight.onclick = function() {
        canvas.getActiveObject().set('textAlign', 'right');
        canvas.requestRenderAll();
    };

    fontColor.oninput = function() {
        canvas.getActiveObject().set('fill', this.value);
        canvas.requestRenderAll();
    }

    fontSize.oninput = function() {
        canvas.getActiveObject().set('fontSize', this.value);
        canvas.requestRenderAll();
    };

    fontBackgroundColor.oninput = function() {
        canvas.getActiveObject().set('textBackgroundColor', this.value);
        canvas.requestRenderAll();
    };

})();