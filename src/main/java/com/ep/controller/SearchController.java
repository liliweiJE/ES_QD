package com.ep.controller;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms.Bucket;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Controller;

import com.ep.utils.ElasticSearchUtil;

@Controller
public class SearchController {
	
	public void createIndex(){
		
	}
	
	public static void main(String[] args) {
		try {
			//roocooCreate();
			//roocooSearch();
			//roocooUpdate();
			//roocooDelete();
			//roocooFZSearch();
			roocooJHSearch();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void roocooCreate() throws Exception{
		TransportClient client = ElasticSearchUtil.getElasticSearchClient();
		client.prepareIndex("company", "employee", "1") 
		.setSource(XContentFactory.jsonBuilder()
				.startObject()
					.field("name", "jack")
					.field("age", 27)
					.field("position", "technique software")
					.field("country", "china")
					.field("join_date", "2017-01-01")
					.field("salary", 10000)
				.endObject())
		.get();

		client.prepareIndex("company", "employee", "2") 
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
							.field("name", "marry")
							.field("age", 35)
							.field("position", "technique manager")
							.field("country", "china")
							.field("join_date", "2017-01-01")
							.field("salary", 12000)
						.endObject())
				.get();
		
		client.prepareIndex("company", "employee", "3") 
		.setSource(XContentFactory.jsonBuilder()
				.startObject()
					.field("name", "tom")
					.field("age", 32)
					.field("position", "senior technique software")
					.field("country", "china")
					.field("join_date", "2016-01-01")
					.field("salary", 11000)
				.endObject())
		.get();
		
		client.prepareIndex("company", "employee", "4") 
		.setSource(XContentFactory.jsonBuilder()
				.startObject()
					.field("name", "jen")
					.field("age", 25)
					.field("position", "junior finance")
					.field("country", "usa")
					.field("join_date", "2016-01-01")
					.field("salary", 7000)
				.endObject())
		.get();
		
		client.prepareIndex("company", "employee", "5") 
		.setSource(XContentFactory.jsonBuilder()
				.startObject()
					.field("name", "mike")
					.field("age", 37)
					.field("position", "finance manager")
					.field("country", "usa")
					.field("join_date", "2015-01-01")
					.field("salary", 15000)
				.endObject())
		.get();
	}
	
	
	public static void roocooSearch(){
		TransportClient client = ElasticSearchUtil.getElasticSearchClient();
		GetResponse response = client.prepareGet("company", "employee", "1").get();
		System.out.println(response.getSourceAsString());
	}
	
	public static void roocooUpdate() throws Exception{
		TransportClient client = ElasticSearchUtil.getElasticSearchClient();
		UpdateResponse response = client.prepareUpdate("company", "employee", "1") 
				.setDoc(XContentFactory.jsonBuilder()
							.startObject()
								.field("position", "technique manager")
							.endObject())
				.get();
		System.out.println(response.getResult());  
	}

	public static void roocooDelete(){
		TransportClient client = ElasticSearchUtil.getElasticSearchClient();
		DeleteResponse response = client.prepareDelete("company", "employee", "1").get();
		System.out.println(response.getResult());
	}
	
	public static void roocooFZSearch(){
		TransportClient client = ElasticSearchUtil.getElasticSearchClient();
		SearchResponse response = client.prepareSearch("company").setTypes("employee")
			.setQuery(QueryBuilders.matchQuery("position", "technique"))
			.setPostFilter(QueryBuilders.rangeQuery("age").from(30).to(40))
			.setFrom(0).setSize(2).get();
		SearchHit[] searchHits = response.getHits().getHits();
		for(int i = 0; i < searchHits.length; i++) {
			System.out.println(searchHits[i].getSourceAsString()); 
		}
	}
	
	public static void roocooJHSearch(){
		TransportClient client = ElasticSearchUtil.getElasticSearchClient();
		SearchResponse searchResponse = client.prepareSearch("company") 
				.addAggregation(AggregationBuilders.terms("group_by_country").field("country")
						.subAggregation(AggregationBuilders
								.dateHistogram("group_by_join_date")
								.field("join_date")
								.dateHistogramInterval(DateHistogramInterval.YEAR)
								.subAggregation(AggregationBuilders.avg("avg_salary").field("salary")))
				)
				.execute().actionGet();
		
		Map<String, Aggregation> aggrMap = searchResponse.getAggregations().asMap();
		
		StringTerms groupByCountry = (StringTerms) aggrMap.get("group_by_country");
		Iterator<Bucket> groupByCountryBucketIterator = groupByCountry.getBuckets().iterator();
		while(groupByCountryBucketIterator.hasNext()) {
			Bucket groupByCountryBucket = groupByCountryBucketIterator.next();
			System.out.println(groupByCountryBucket.getKey() + ":" + groupByCountryBucket.getDocCount()); 
		
			Histogram groupByJoinDate = (Histogram) groupByCountryBucket.getAggregations().asMap().get("group_by_join_date");
			Iterator<org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket> groupByJoinDateBucketIterator = (Iterator<org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket>) groupByJoinDate.getBuckets().iterator();
			while(groupByJoinDateBucketIterator.hasNext()) {
				org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket groupByJoinDateBucket = groupByJoinDateBucketIterator.next();
				System.out.println(groupByJoinDateBucket.getKey() + ":" +groupByJoinDateBucket.getDocCount()); 
			
				Avg avg = (Avg) groupByJoinDateBucket.getAggregations().asMap().get("avg_salary"); 
				System.out.println(avg.getValue()); 
			}
		}
		
		client.close();
	}
}
