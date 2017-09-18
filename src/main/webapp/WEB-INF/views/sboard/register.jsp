<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@include file="../include/header.jsp"%>


<style>
.fileDrop {
  width: 80%;
  height: 100px;
  border: 1px dotted gray;
  background-color: lightslategrey;
  margin: auto;
  
}
</style>

<!-- Main content -->
<section class="content">
	<div class="row">
		<!-- left column -->
		<div class="col-md-12">
			<!-- general form elements -->
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">REGISTER BOARD</h3>
				</div>
				<!-- /.box-header -->

<form id='registerForm' role="form" method="post">
	<div class="box-body">
		<div class="form-group">
			<label for="exampleInputEmail1">Title</label> 
			<input type="text"
				name='title' class="form-control" placeholder="Enter Title">
		</div>
		<div class="form-group">
			<label for="exampleInputPassword1">Content</label>
			<textarea class="form-control" name="content" rows="3"
				placeholder="Enter ..."></textarea>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Writer</label> 
			<input type="text"
				name="writer" class="form-control" value="${login.uid } readonly">
		</div>

		<div class="form-group">
			<label for="exampleInputEmail1">File DROP Here</label>
			<div class="fileDrop"></div>
		</div>
	</div>

	<!-- /.box-body -->

	<div class="box-footer">
		<div>
			<hr>
		</div>

		<ul class="mailbox-attachments clearfix uploadedList">
		</ul>

		<button type="submit" class="btn btn-primary">Submit</button>

	</div>
</form>


			</div>
			<!-- /.box -->
		</div>
		<!--/.col (left) -->

	</div>
	<!-- /.row -->
</section>
<!-- /.content -->
</div>
<!-- /.content-wrapper -->

<script type="text/javascript" src="/resources/js/upload.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/handlebars.js/3.0.1/handlebars.js"></script>

<script id="template" type="text/x-handlebars-template">
<li>
  <span class="mailbox-attachment-icon has-img"><img src="{{imgsrc}}" alt="Attachment"></span>
  <div class="mailbox-attachment-info">
	<a href="{{getLink}}" class="mailbox-attachment-name" target=blank>{{fileName}}</a>
	<a href="{{fullName}}" 
     class="btn btn-default btn-xs pull-right delbtn"><i class="fa fa-fw fa-remove"></i></a>
  </div>
</li>                
</script>    

<script>

var template = Handlebars.compile($("#template").html());//template안에 $("#template").html()태그들을 담음
$(function(){
	
	$(".fileDrop").on("dragenter dragover", function(event){
		event.preventDefault();
	});
	
	
	$(".fileDrop").on("drop", function(event){
	
		event.preventDefault();
		
		var files = event.originalEvent.dataTransfer.files;//하드에서 받은 파일
		var file = files[0];//files[0]에 만든시간,사이즈,이름,타입 등 많은 정보가 있음.
		console.log(file);
		
		var formData = new FormData();
		
		formData.append("file", file);	
		
		
		$.ajax({
			  url: '/uploadAjax',
			  data: formData,
			  dataType:'text',
			  processData: false,
			  contentType: false,
			  type: 'POST',
			  success: function(data){
				  
				  console.log("data == "+data);
				  
				  var fileInfo = getFileInfo(data);//template에 넣어줄 json파일들 생성(js메소드에서 만들어줌)
				 
				  var html = template(fileInfo);//template에 json파일들 넣어줌.
				  
				  $(".uploadedList").append(html);
			  }
			});	
	});
	
	$(".uploadedList").on("click", ".delbtn", function(event){
		
		event.preventDefault();
		
		var that = $(this);
		 
		$.ajax({
		   url:"/deleteFile",
		   type:"post",
		   data: {fileName:$(this).attr("href")},
		   dataType:"text",
		   success:function(result){
			   if(result == 'deleted'){
				   that.closest("li").remove();
			   }
		   }
	   });
	});
	
	
	$("#registerForm").submit(function(event){
		
		//event.preventDefault()을 하지 않으면 최하단 that.get(0).submit();가 없어도 작동함.
		event.preventDefault();
		
		//#registerForm를 가르킴
		var that = $(this);
		
		var str ="";
		$(".uploadedList .btn").each(function(index){
			//index는 .btn의 갯수를 따져서 인댁스를 준 것
																		//$(".uploadedList .btn")를 가르킴
			 str += "<input type='hidden' name='files["+index+"]' value='"+$(this).attr("href") +"'> ";

		});
		
		that.append(str);

		alert("str == "+str);
		
		//searchBoard 컨트롤러의 /register에서 실행됨ㄴ
		that.get(0).submit();
		
	});
})


</script>

 

<%@include file="../include/footer.jsp"%>
