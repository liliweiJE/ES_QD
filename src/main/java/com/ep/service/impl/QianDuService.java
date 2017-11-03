package com.ep.service.impl;

import java.util.List;

import com.ep.domain.ImportNewDomain;

public interface QianDuService {
	
	public List<String> complateKey(String keyWords);

	public int searchTotal(String keyWords);

	public List<ImportNewDomain> searchDoc(String keyWords, Integer pageNum, Integer pageSize);

}
