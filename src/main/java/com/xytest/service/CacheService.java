package com.xytest.service;

import com.xytest.domain.ArticleContent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangmg on 2017/5/3.
 */
@Service("cs")
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
    public void say(){
        System.out.println("here is service");
    }
}
