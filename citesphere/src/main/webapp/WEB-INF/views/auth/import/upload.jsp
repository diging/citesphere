<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<style>
/* layout.css Style */
.upload-drop-zone {
  height: 200px;
  border-width: 2px;
  margin-bottom: 20px;
}

/* skin.css Style*/
.upload-drop-zone {
  color: #ccc;
  border-style: dashed;
  border-color: #ccc;
  line-height: 200px;
  text-align: center
}
.upload-drop-zone.drop {
  color: #222;
  border-color: #222;
}
</style>

<script>
$(document).ready(function() {
    var dropZone = document.getElementById('drop-zone');
    var uploadForm = document.getElementById('js-upload-form');

    var startUpload = function(files) {
    	var formData = new FormData();
    	formData.append("group", $("#groupSelect option:selected").val());
    	for (var i = 0; i < files.length; i++) {
    		formData.append("files", files[i]);
    	}
    	<c:url value="/auth/import/upload?${_csrf.parameterName}=${_csrf.token}"  var="postUrl" />
    	$.ajax({
			type: "POST",
			url: "${postUrl}",
			cache       : false,
	        contentType : false,
	        processData : false,
	        enctype: 'multipart/form-data',
	        data: formData, 
	        success: function(data) {
	        	var uploadedFilesList = $("#uploaded-files-list");
	        	var jobs = JSON.parse(data);
	        	$.each(jobs["jobs"], function(i, obj) {
	        		if (obj.status == "PREPARED") {
		        		var successBox = $('<a href="#" class="list-group-item list-group-item-success"></a>');
		        		var badge = $('<span class="badge alert-success pull-right">Success</span>');
		        		successBox.append(badge);
		        		successBox.append(obj.filename);
		        		uploadedFilesList.append(successBox);
	        		} else {
	        			var successBox = $('<a href="#" class="list-group-item list-group-item-danger"></a>');
		        		var badge = $('<span class="badge alert-danger pull-right">Failure</span>');
		        		successBox.append(badge);
		        		successBox.append(obj.filename);
		        		uploadedFilesList.append(successBox);
	        		}
	        	});
	        	$("#js-upload-form")[0].reset();
	        }
		});
    }

    $("#js-upload-submit").click(function(e) {
        var uploadFiles = document.getElementById('js-upload-files').files;
        e.preventDefault()

        startUpload(uploadFiles)
    });

    dropZone.ondrop = function(e) {
        e.preventDefault();
        this.className = 'upload-drop-zone';

        startUpload(e.dataTransfer.files)
    }

    dropZone.ondragover = function() {
        this.className = 'upload-drop-zone drop';
        return false;
    }

    dropZone.ondragleave = function() {
        this.className = 'upload-drop-zone';
        return false;
    }

});
</script>

<div class="container">
   <div class="panel panel-default">
     <div class="panel-heading"><strong>Upload References</strong> <small></small></div>
     <div class="panel-body">

       
       <h4>Select files from your computer</h4>
       <form action="" method="post" enctype="multipart/form-data" id="js-upload-form">
       	 <div class="form-inline" style="padding-bottom: 20px;">
       	 <label>Select Group for Upload: </label>
       	 <select id="groupSelect" name="group" class="form-control">
       	 	<option value="">Please select one</option>
       	 	<c:forEach items="${groups}" var="citGroup">
       	 		<option value="${citGroup.id}">${citGroup.name}</option>
       	 	</c:forEach>
       	 </select>
       	 </div>
       
         <div class="form-inline">
           <div class="form-group">
             <input type="file" name="files[]" id="js-upload-files" multiple>
           </div>
           <button type="submit" class="btn btn-sm btn-primary" id="js-upload-submit">Upload files</button>
         </div>
       </form>

       <!-- Drop Zone -->
       <h4>Or drag and drop files below</h4>
       <div class="upload-drop-zone" id="drop-zone">
         Just drag and drop files here
       </div>

       <!-- Upload Finished -->
       <div class="js-upload-finished">
         <h3>Processed files</h3>
         <div id="uploaded-files-list" class="list-group">
           
         </div>
       </div>
     </div>
   </div>
 </div> <!-- /container -->