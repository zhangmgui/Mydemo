package com.xytest.service;

import com.xytest.domain.ArticleContent;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangmg on 2017/5/3.
 */

public class CacheService {
    @Cacheable(value = "ListCache", key = "'art_list'")
    public List<ArticleContent> getList(){
        ArticleContent articleContent = new ArticleContent();
        articleContent.setID(1);
        articleContent.setTitle("my title");
        ArrayList<ArticleContent> list = new ArrayList<>();
        list.add(articleContent);
        System.out.println("cache no shoot");
        return list;
    }
}
