$(document).on('change', 'input[type="file"]',function () {
    var gthis = this;
    //check the file size should not be greater than 5 mb
    filesize = this.files[0].size / 1024 / 1024;
    if (filesize > 2)
    {
        alert('File size cannot be greater than 2 MB');
        this.value = null;
        return;
    }
//    
////    debugger;
    //to checkk original mime type
    var format = /[!@#$%^&*+\=\[\]{};':"\\|,<>\/?]+/;
    var files = this.files;
    if (files.length > 0) {
        var file = files[0];
        var filename = file.name.split('.').slice(0, -1).join('.');
        if(format.test(filename)){
            alert('Filename cannot contain special characters.\nKindly edit the filename and upload again.');
            this.value = null;
            return;
        }
        
        var fileReader = new FileReader();
        fileReader.onloadend = function (e) {
                var arr = (new Uint8Array(e.target.result)).subarray(0, 4);
                var header = '';
                for (var i = 0; i < arr.length; i++) {
                        header += arr[i].toString(16);
                }

                // Check the file signature against known types
                var type = 'unknown';
                switch (header) {
                        case '89504e47':
                                type = 'image/png';
                                break;                       
                        case 'ffd8ffe0':
                        case 'ffd8ffe1':
                        case 'ffd8ffe2':
                                type = 'image/jpeg';
                                break;
                        case '25504446':
                                type = 'application/pdf';
                                break;
                }

                if (file.type !== type) {
                    alert('Uploaded File is not allowed. Kindly check the filetype!!');
                    gthis.value = null;
                    return;
                } else {
                if(!(type == "image/png" ||type == "image/jpeg" ||type == "application/pdf"))
                {
                    alert("Filetype not supported for upload.");
                    gthis.value = null;
                    return;
                }
            }
        };
        fileReader.readAsArrayBuffer(file);
    }
    
});