// Configure settings and attach camera
function configure() {
    Webcam.set({
        width: 320,
        height: 240,
        image_format: 'jpeg',
        upload_name: 'file',
        jpeg_quality: 90
    });
    Webcam.attach('#my_camera');
}

function take_snapshot() {
    // take snapshot and get image data
    Webcam.snap(function (data_uri) {
        // display snapshot in page
        document.getElementById('snapshot').innerHTML =
            '<img id="imageprev" src="' + data_uri + '"/>';
    });
}

function saveSnap() {
    // Get base64 value from <img id='imageprev'> source
    var base64image = document.getElementById("imageprev").src;
    Webcam.upload(base64image, 'TensorApi/recognizeFile', function (code, text) {
        console.log('Uploaded successfully');
        console.log('Response: ' + text);
        var recognitionsArray = JSON.parse(text);
        var html = recognitionsArray.length > 0 ? 'Found objects: <br/>' : 'Nothing was detected';
        for (var i = 0; i < recognitionsArray.length; i++) {
            html += '<li> ' + recognitionsArray[i].name + ' : ' + recognitionsArray[i].confidence;
        }
        document.getElementById('recognitions').innerHTML = html;
    });
}