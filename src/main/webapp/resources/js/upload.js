function checkImageType(fileName){
	
	var pattern = /jpg|gif|png|jpeg/i;
	
	return fileName.match(pattern);

}

function getFileInfo(fullName){
		
	var fileName,imgsrc, getLink;
	
	var fileLink;
	
	if(checkImageType(fullName)){
		imgsrc = "/displayFile?fileName="+fullName; //썸내일 이미지 경로
		fileLink = fullName.substr(14);
		
		var front = fullName.substr(0,12); // /2015/07/01/ 
		var end = fullName.substr(14); //s_를 제외한 그림파일
		
		getLink = "/displayFile?fileName="+front + end;//오리지널 이미지 경로
		
	}else{
		imgsrc ="/resources/dist/img/file.png";//이미지파일이 아니므로 대체 이미지 출력
		fileLink = fullName.substr(12);// /2015/07/01/ 이후의 오리지널 파일이름.
		getLink = "/displayFile?fileName="+fullName;
	}
	fileName = fileLink.substr(fileLink.indexOf("_")+1);
	
	return  {fileName:fileName, imgsrc:imgsrc, getLink:getLink, fullName:fullName};
	
}


