var fileChooser;
var img_content;

if (typeof window.FileReader === 'undefined') {
	alert( 'Sistema integrado de Câmera não suportado pela versão do Android.' );
}

function onFileChange(e) {
    //e.preventDefault();

    var file = fileChooser.files[0],
        reader = new FileReader();
        
    reader.onerror = function (event) {
        img_content.innerHTML = "Error reading file";
    }

    reader.onload = function (event) {
        var img = new Image();

		document.getElementById('base64').value = event.target.result.split('data:image/jpeg;base64,').join('');
        // files from the Gallery need the URL adjusted
        if (event.target.result && event.target.result.match(/^data:base64/)) {
            img.src = event.target.result.replace(/^data:base64/, 'data:image/jpeg;base64');
        } else {
            img.src = event.target.result;
        }

        // Guess photo orientation based on device orientation, works when taking picture, fails when loading from gallery
        if (navigator.userAgent.match(/mobile/i) && window.orientation === 0) {
            img.height = 250;
            img.className = 'rotate';
        } else {
            img.width = 400;
        }

        img_content.innerHTML = '';
        img_content.appendChild(img);
    };
    
    reader.readAsDataURL(file);

    return false;
}