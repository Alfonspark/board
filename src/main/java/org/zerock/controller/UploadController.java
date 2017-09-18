package org.zerock.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.util.MediaUtils;
import org.zerock.util.UploadFileUtils;

@Controller
public class UploadController {

  private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
  

  
  @Resource(name = "uploadPath") //servlet.xml에 지정해 빈으로 지정해 놓은 상수 경로임.
  private String uploadPath;

  @RequestMapping(value = "/uploadForm", method = RequestMethod.GET)
  public void uploadForm() {
  }
  
  @RequestMapping(value = "/uploadForm", method = RequestMethod.POST)
  public String uploadForm(MultipartFile file, Model model) throws Exception {

  logger.info("originalName: " + file.getOriginalFilename());
  logger.info("size: " + file.getSize());
  logger.info("contentType: " + file.getContentType());

  String savedName = uploadFile(file.getOriginalFilename(), file.getBytes());
  
  // 파일이름 (랜덤아이디+오리지널 아이디)
  model.addAttribute("savedName", savedName);
  
  return "uploadResult";
  }
  
  private String uploadFile(String originalName, byte[] fileData) throws Exception {

	    UUID uid = UUID.randomUUID();

	    String savedName = uid.toString() + "_" + originalName;
	    
	    //파일이름과 uploadPath를 이용해 파일전송을 위한 target을 만듦.
	    File target = new File(uploadPath, savedName);
	    System.out.println("target"+target);
	    
	    //파일전송을 위해 byte타입의 파일과, target(uploadPath,파일이름)이 필요함.
	    FileCopyUtils.copy(fileData, target);

	    return savedName;

	  }
  
  
  ///////////////////////////////////  uploadAjax   ////////////////////////////////////////
  @RequestMapping(value = "/uploadAjax", method = RequestMethod.GET)
  public void uploadAjax() {
  }

  
  @ResponseBody
  @RequestMapping(value ="/uploadAjax", method=RequestMethod.POST, 
                  produces = "text/plain;charset=UTF-8")
  public ResponseEntity<String> uploadAjax(MultipartFile file)throws Exception{
    
    logger.info("originalName: " + file.getOriginalFilename());
    
   
    return new ResponseEntity<>(
    		//UploadFileUtils에있는 uploadFile호출(static이므로 메모리에 올리지 않아도  UploadFileUtils.uploadFile이런식으로 바로 실행 가능.)
            UploadFileUtils.uploadFile(uploadPath, 
                    file.getOriginalFilename(), 
                    file.getBytes()), 
              HttpStatus.CREATED);
    //  new ResponseEntity<>( file.getOriginalFilename(), HttpStatus.CREATED);
    
    
  }
  
  
  //이미지를 드레그 했을때 아래에 이미지 나타나게 하는 메소드
  //fileName == 드레그된 파일의 이름을 의미한다
  @ResponseBody
  @RequestMapping("/displayFile")
  public ResponseEntity<byte[]>  displayFile(String fileName)throws Exception{
    
    InputStream in = null; 
    ResponseEntity<byte[]> entity = null;
    
    logger.info("FILE NAME: " + fileName);
    
    try{
      
      String formatName = fileName.substring(fileName.lastIndexOf(".")+1);//확장자명을 가져옴.
      
      MediaType mType = MediaUtils.getMediaType(formatName);//확장자가 이미지타입인지 걸러냄
      logger.info("mType == "+mType);
      
      HttpHeaders headers = new HttpHeaders();
      
      //FileInputStream 는 InputStream 를 상속받았으며, 파일로 부터 바이트로 입력받아, 바이트 단위로 출력할 수 있는 클래스이다.
      in = new FileInputStream(uploadPath+fileName);//경로+파일이름을 InputStream 객체로 만들어줌.
      logger.info("in.toString() == "+in);
      
      if(mType != null){
        headers.setContentType(mType);//이미지타입일경우 맵에서 매칭되는 확장자명을 가져옴.
      }else{
        fileName = fileName.substring(fileName.indexOf("_")+1);   
        
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        
        //파일이름의 한글처리
        headers.add("Content-Disposition", "attachment; filename=\""+ 
          new String(fileName.getBytes("UTF-8"), "ISO-8859-1")+"\"");
      }

        entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
        		//toByteArray 함수 == InputStream 값을 byteArray에 담습니다(in을 byte[] 형으로 바꾸어주는 역할임.)
        
        logger.info("IOUtils.toByteArray(in) == "+IOUtils.toByteArray(in));
        logger.info("headers == "+headers);
        
    }catch(Exception e){
      e.printStackTrace();
      entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
    }finally{
      in.close();
    }
      
      //byte화 된 파일을 리턴함.
      return entity;    
  }
    
  @ResponseBody
  @RequestMapping(value="/deleteFile", method=RequestMethod.POST)
  public ResponseEntity<String> deleteFile(String fileName){
    
    logger.info("delete file: "+ fileName);
    
    String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
    
    MediaType mType = MediaUtils.getMediaType(formatName);
    
    if(mType != null){      
      
      String front = fileName.substring(0,12);
      String end = fileName.substring(14);
      new File(uploadPath + (front+end).replace('/', File.separatorChar)).delete();
    }
    
    new File(uploadPath + fileName.replace('/', File.separatorChar)).delete();
    
    
    return new ResponseEntity<String>("deleted", HttpStatus.OK);
  }  
  
  //파일들 삭제
  @ResponseBody
  @RequestMapping(value="/deleteAllFiles", method=RequestMethod.POST)
  public ResponseEntity<String> deleteFile(@RequestParam("files[]") String[] files){
    
    logger.info("delete all files: "+ files);
    
    if(files == null || files.length == 0) {
      return new ResponseEntity<String>("deleted", HttpStatus.OK);
    }
    
    for (String fileName : files) {
      String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
      
      MediaType mType = MediaUtils.getMediaType(formatName);
      
      if(mType != null){      
        
        String front = fileName.substring(0,12);
        String end = fileName.substring(14);
        new File(uploadPath + (front+end).replace('/', File.separatorChar)).delete();
      }
     
      new File(uploadPath + fileName.replace('/', File.separatorChar)).delete();
     
    }
    return new ResponseEntity<String>("deleted", HttpStatus.OK);
  }  


//  @ResponseBody
//  @RequestMapping(value = "/uploadAjax", 
//                 method = RequestMethod.POST, 
//                 produces = "text/plain;charset=UTF-8")
//  public ResponseEntity<String> uploadAjax(MultipartFile file) throws Exception {
//
//    logger.info("originalName: " + file.getOriginalFilename());
//    logger.info("size: " + file.getSize());
//    logger.info("contentType: " + file.getContentType());
//
//    return 
//        new ResponseEntity<>(file.getOriginalFilename(), HttpStatus.CREATED);
//  }



}
