package com.ep.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MultiMatchQuery;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.stereotype.Service;

import com.ep.domain.ImportNewDomain;
import com.ep.service.impl.QianDuService;
import com.ep.utils.ElasticSearchUtil;

@Service
public class QianDuServiceImpl implements QianDuService {
	@Override
	public List<String> complateKey(String keyWords) {
		// TODO Auto-generated method stub
TransportClient client = ElasticSearchUtil.getElasticSearchClient();
		
		if(null==keyWords||"".equals(keyWords.trim())){
			return null;
		}
		boolean isFullChinese = false;
		// ^[a-z0-9A-Z\u4e00-\u9fa5]+$  是否包含数字 汉字 和 字母
		if(keyWords.matches("^[\u4e00-\u9fa5]+$")){
			isFullChinese =  true;
		}
		
		SuggestBuilder sb=new SuggestBuilder();
		CompletionSuggestionBuilder ngramSuggestion = SuggestBuilders.completionSuggestion("title.suggest_title.ngram")
				.text(keyWords).size(5);
		CompletionSuggestionBuilder simplePinyinSuggestion = SuggestBuilders.completionSuggestion("title.suggest_title.simple_pinyin")
				.text(keyWords).size(5);
		CompletionSuggestionBuilder fullPinyinSuggestion = SuggestBuilders.completionSuggestion("title.suggest_title.full_pinyin")
				.text(keyWords).size(5);
		CompletionSuggestionBuilder ikSuggestion = SuggestBuilders.completionSuggestion("title.suggest_title.ik")
				.text(keyWords).size(5);
		
		sb.addSuggestion("ngram_suggestion", ngramSuggestion);
		sb.addSuggestion("simple_pinyin_suggestion", simplePinyinSuggestion);
		sb.addSuggestion("full_pinyin_suggestion", fullPinyinSuggestion);
		sb.addSuggestion("ik_suggestion", ikSuggestion);
		
		SearchResponse searchResponse = client.prepareSearch("importnew").setTypes("news")
				.suggest(sb).execute().actionGet();
		
		List<String> keywords = new ArrayList<String>();
		List<? extends Option> options = 
				searchResponse.getSuggest().getSuggestion("ngram_suggestion").getEntries().get(0).getOptions();
		if(null!=options){
			for (Option option : options) {
				String key = option.getText().toString();
				if(!keywords.contains(key)&&keywords.size()<5){
					keywords.add(key);
				}
			}
		}
		options =searchResponse.getSuggest().getSuggestion("ik_suggestion").getEntries().get(0).getOptions();
		if(null!=options){
			for (Option option : options) {
				String key = option.getText().toString();
				if(!keywords.contains(key)&&keywords.size()<5){
					keywords.add(key);
				}
			}
		}
		options =searchResponse.getSuggest().getSuggestion("simple_pinyin_suggestion").getEntries().get(0).getOptions();
		if(null!=options&&!isFullChinese){
			for (Option option : options) {
				String key = option.getText().toString();
				if(!keywords.contains(key)&&keywords.size()<5){
					keywords.add(key);
				}
			}
		}
		options =searchResponse.getSuggest().getSuggestion("full_pinyin_suggestion").getEntries().get(0).getOptions();
		if(null!=options&&!isFullChinese){
			for (Option option : options) {
				String key = option.getText().toString();
				if(!keywords.contains(key)&&keywords.size()<5){
					keywords.add(key);
				}
			}
		}
		
		return keywords;
	}

	@Override
	public int searchTotal(String keyWords) {
		// TODO Auto-generated method stub
		TransportClient client = ElasticSearchUtil.getElasticSearchClient();
		SearchResponse response = client.prepareSearch("importnew").setTypes("news")
				.setQuery(QueryBuilders.disMaxQuery()
						.tieBreaker(0.3f)
						.add(QueryBuilders.boolQuery()
							.should(QueryBuilders.matchPhraseQuery("title", keyWords).boost(10).slop(20))
							.should(QueryBuilders.matchPhraseQuery("remark", keyWords).slop(50))
							.minimumShouldMatch("1")))
				.setSize(0).get();
		return (int) response.getHits().getTotalHits();
	}

	@Override
	public List<ImportNewDomain> searchDoc(String keyWords, Integer pageNum, Integer pageSize) {
		// TODO Auto-generated method stub
		
		HighlightBuilder highlightBuilder = new HighlightBuilder().field("title").field("remark").requireFieldMatch(false);
		highlightBuilder.forceSource(true);
		highlightBuilder.preTags("<span style='color:#ff0000;'>");
		highlightBuilder.postTags("</span>");
		highlightBuilder.numOfFragments(3);
		highlightBuilder.fragmentSize(20);
		TransportClient client = ElasticSearchUtil.getElasticSearchClient();
		Integer start=(pageNum-1)*pageSize;
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch("importnew").setTypes("news")
				.setFrom(start)
				.setSize(pageSize)
				.highlighter(highlightBuilder)
				.setQuery(QueryBuilders.disMaxQuery()
						.tieBreaker(0.3f)
						.add(QueryBuilders.boolQuery()
							.should(QueryBuilders.matchPhraseQuery("title", keyWords).boost(10).slop(20))
							.should(QueryBuilders.matchPhraseQuery("remark", keyWords).slop(50))
							.minimumShouldMatch("1")));
		SearchResponse searchResponse = searchRequestBuilder.get();
		SearchHits hits = searchResponse.getHits();
		int hitLength = hits.getHits().length;
		List<ImportNewDomain> list=new ArrayList<ImportNewDomain>();
		for(int i=0;i<hitLength;i++){
			SearchHit searchHit = hits.getAt(i);
			ImportNewDomain imp=new ImportNewDomain();
			Map<String, Object> source = searchHit.getSource();
			imp.setDocurl((String)source.get("docurl"));
			imp.setImgurl((String)source.get("imgurl"));
			imp.setTypeC((String)source.get("typec"));
			
			Map<String,HighlightField> highLightFields = searchHit.getHighlightFields();
			if(highLightFields!=null && highLightFields.size()>0 ){
				String[] needHighLightFields = new String[]{"title","remark"};
				for (String needHighLightField : needHighLightFields) {
					HighlightField highlightField = highLightFields.get(needHighLightField);
					if(highlightField!=null){
						Text[] fragments = highlightField.getFragments();
						if(fragments!=null && fragments.length>0){
							StringBuilder name = new StringBuilder();
							for (Text text : fragments) {
								name.append(text);
							}
							//source.put(needHighLightField, name.toString());
							if("title".equals(needHighLightField)){
									imp.setTitle(name.toString());
							}else{
									imp.setRemark(name.toString());
							}
						}
					}else{
						if("title".equals(needHighLightField)){
							imp.setTitle((String)source.get("title"));
						}else{
							imp.setRemark((String)source.get("remark"));
						}
					}
				}
			}
			list.add(imp);
		}
		return list;
	}

}
