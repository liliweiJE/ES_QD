package com.ep.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ep.utils.FastdfsClient;

@Controller
public class UploadController {

	@Value("${IMAGE_SERVER_BASE_URL}")
	private String IMAGE_SERVER_BASE_URL;
	
	@RequestMapping("/upload")
	@ResponseBody
	public void upload(MultipartFile file){
		try {
		//取图片扩展名
		String originalFilename = file.getOriginalFilename();
		//取扩展名不要“.”
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		
		FastdfsClient fclient=new FastdfsClient("classpath:property/fdfs_client.conf");
        
	    String uploadFileurl = fclient.uploadFile(file.getBytes(), extName);
	    
	    System.out.println(uploadFileurl);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
	
}
