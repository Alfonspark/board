<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
  "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<style>
.fileDrop {
	width: 100%;
	height: 200px;
	border: 1px dotted blue;
}

small {
	margin-left: 3px;
	font-weight: bold;
	color: gray;
}
</style>
</head>
<body>

	<h3>Ajax File Upload</h3>
	<div class='fileDrop'></div>

	<div class='uploadedList'></div>

	<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
	<script>
	$(function(){

		$(".fileDrop").on("dragenter dragover", function(event) {
			event.preventDefault();
		});
		
		$(".fileDrop").on("drop", function(event){
			event.preventDefault();
			
			var files = event.originalEvent.dataTransfer.files;
			
			var file = files[0];

			//console.log(file);
			
			var formData = new FormData();
			
			formData.append("file", file);
			
			$.ajax({
				  url: '/uploadAjax2',
				  data: formData,
				  dataType:'text',
				  processData: false,
				  contentType: false,
				  type: 'POST',
				  success: function(data){
					  
					  var str ="";
	
					  if(checkImageType(data)){
						  str ="<div><a href=displayFile?fileName="+getImageLink(data)+" target=blank>"
								  +"<img src='displayFile?fileName="+data+"'/>"
								 +getOriginalName(data)+"</a><small data-src="+data+">X</small></div>";
					  }else{
						  str = "<div><a href='displayFile?fileName="+data+"'>" 
								  + data+"</a>"
								  +"<small data-src="+data+">X</small></div></div>";
					  }
					  
					  $(".uploadedList").append(str);
				  }
				});	
		});
		
		
		$(".uploadedList").on("click", "small", function(event){
			
			 var that = $(this); //지금 클릭한것의 객체를 ajax에 가져감

		   $.ajax({
			   url:"deleteFile",
			   type:"post",
			   data: {fileName:$(this).attr("data-src")},
			   dataType:"text",
			   success:function(result){
				   if(result == 'deleted'){
					   that.parent("div").remove();
				   }
			   }
			   });
			});
		
		//이미지타입 체크
		function checkImageType(fileName){
			
			var pattern = /jpg|gif|png|jpeg/i;
			
			return fileName.match(pattern);			
		}
		
		function getOriginalName(fileName){	

			if(checkImageType(fileName)=="null"){
				alert(11)
				return;
			}
			
			var idx = fileName.indexOf("_") + 1 ;
			var idxnum= fileName.substr(idx);

			return idxnum;			
		}
		
		function getImageLink(fileName){
			
			if(!checkImageType(fileName)){
				return;
			}
			var front = fileName.substr(0,12);
			var end = fileName.substr(14);
			
			console.log("front == "+front);
			console.log("end == "+end);
			
			return front + end;
			
		}
		
		
		
	})
	</script>

</body>
</html>