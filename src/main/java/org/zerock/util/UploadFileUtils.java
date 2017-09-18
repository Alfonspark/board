//UploadFileUtils

package org.zerock.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import com.mysql.fabric.xmlrpc.base.Array;

public class UploadFileUtils {

  private static final Logger logger = 
      LoggerFactory.getLogger(UploadFileUtils.class);

  public static String uploadFile(String uploadPath, 
          String originalName, 
          byte[] fileData)throws Exception{

		UUID uid = UUID.randomUUID();
		
		String savedName = uid.toString() +"_"+originalName;
		
		String savedPath = calcPath(uploadPath);//년,월,일 폴더
		
		//target은 path와 파일 이름을 넣어줌.
		File target = new File(uploadPath +savedPath,savedName);
		
		//전송은 byte파일과 target을 넣어줌.(이 시점에서 전송됨)
		FileCopyUtils.copy(fileData, target);
		
		String formatName = originalName.substring(originalName.lastIndexOf(".")+1);//확장자 이름을 받아온다(jpg,png..)
		
		String uploadedFileName = null;
		
		if(MediaUtils.getMediaType(formatName) != null){
		//uploadPath를 제외한 년도,파일명이 리턴값으로 넘어감.
		uploadedFileName = makeThumbnail(uploadPath, savedPath, savedName);
		
		}else{
		uploadedFileName = makeIcon(uploadPath, savedPath, savedName);
		}
		
		logger.info("uploadedFileName == "+uploadedFileName);
		
		return uploadedFileName;
	
	}
  
  private static  String makeIcon(String uploadPath, 
	      String path, 
	      String fileName)throws Exception{

	    String iconName = 
	        uploadPath + path + File.separator+ fileName;
	    
	    return iconName.substring(uploadPath.length()).replace(File.separatorChar, '/');
	  }

  private static String calcPath(String uploadPath){
    
    Calendar cal = Calendar.getInstance();
    
    String yearPath = File.separator+cal.get(Calendar.YEAR);
    
    String monthPath = yearPath + 
        File.separator + 
        new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1);

    String datePath = monthPath + 
        File.separator + 
        new DecimalFormat("00").format(cal.get(Calendar.DATE));
    
    makeDir(uploadPath, yearPath,monthPath,datePath);
    
    logger.info(datePath);
    
    return datePath;
  }
  
  
  private static void makeDir(String uploadPath, String... paths){
    
    if(new File(paths[paths.length-1]).exists()){
      return;
    }
    
    for (String path : paths) {
      
      File dirPath = new File(uploadPath + path);
      
      if(! dirPath.exists() ){
        dirPath.mkdir();
      } 
    }
  }
  
  //path==년/월/일  ,fileName == 랜덤값+파일이름
  private static  String makeThumbnail(
          String uploadPath, 
          String path, 
          String fileName)throws Exception{
        	
	  		//메모리에 이미지를 형상화함                                                                          target
			BufferedImage sourceImg = ImageIO.read(new File(uploadPath + path, fileName));
			
			//형상화한 이미지틀의 사이지조절
			BufferedImage destImg = 
			    Scalr.resize(sourceImg, 
			        Scalr.Method.AUTOMATIC, 
			        Scalr.Mode.FIT_TO_HEIGHT,100);
			
			String thumbnailName = 
			    uploadPath + path + File.separator +"s_"+ fileName;
			
			logger.info("thumbnailName == "+thumbnailName);
			
			File newFile = new File(thumbnailName);//thumbnailName 을 파일화함
			logger.info("newFile == "+newFile.toString());
			
			String formatName = 
			    fileName.substring(fileName.lastIndexOf(".")+1);

			logger.info("formatName == "+fileName.substring(fileName.lastIndexOf(".")+1));
			logger.info("formatName.toUpperCase() == "+formatName.toUpperCase());
			
			
			//형상화한 이미지에 이미지를 넣음
			//destImg == 사이지 조절한 틀, formatName == 확장자명, thumbnailName 을 파일화함(총경로+파일이름)
			ImageIO.write(destImg, formatName.toUpperCase(), newFile);
			return thumbnailName.substring(
			    uploadPath.length()).replace(File.separatorChar, '/');
			} 
  
}
