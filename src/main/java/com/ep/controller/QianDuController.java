package com.ep.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ep.domain.ImportNewDomain;
import com.ep.service.impl.QianDuService;
import com.ep.utils.PageUtil;

@Controller
@RequestMapping("/qd")
public class QianDuController {
	
	@Autowired
	private QianDuService qianDuService;

	@RequestMapping("/index")
	public String index(
			Model model,
			@RequestParam(value="keyWords",required = false) String keyWords,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) throws Exception{
		
		
		if(!StringUtils.isNotBlank(keyWords)){
			return "index";
		}
		keyWords = new String(keyWords.getBytes("ISO-8859-1"),"UTF-8");
		int total=qianDuService.searchTotal(keyWords);
		List<ImportNewDomain> imports=qianDuService.searchDoc(keyWords,pageNum,pageSize);
		PageUtil<ImportNewDomain> page = new PageUtil<ImportNewDomain>(String.valueOf(pageNum),String.valueOf(pageSize),total);
		page.setList(imports);
		
		model.addAttribute("page",page);
		model.addAttribute("keyWords",keyWords);
		
		return "index";
	}
	
	
	@RequestMapping("/complateKey")
	@ResponseBody
	public List<String> complateKeyWords(Model model,
			@RequestParam(value="keyWords",required = false) String keyWords){
		return qianDuService.complateKey(keyWords);
	}
	
	/*public static void main(String[] args) {
		List<String> complateKeyWords = complateKeyWords("java");
		for (String string : complateKeyWords) {
			System.out.println(string);	
		}
	}*/
}
