package com.example.web_doushijin.controller;

import com.example.web_doushijin.entity.Page;
import com.example.web_doushijin.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@CrossOrigin("*")
public class PageController {

    @Autowired
    private PageService pageService;

    // URL: http://localhost:8081/api/chapters/5/pages
    @GetMapping("/{chapterId}/pages")
    public List<Page> getPages(@PathVariable Long chapterId) {
        return pageService.getPagesByChapter(chapterId);
    }
}