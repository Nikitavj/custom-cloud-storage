package org.nikita.spingproject.filestorage.controller;

import org.nikita.spingproject.filestorage.commons.dto.ObjectStorageDto;
import org.nikita.spingproject.filestorage.commons.dto.SearchRequest;
import org.nikita.spingproject.filestorage.file.exception.SearchFilesException;
import org.nikita.spingproject.filestorage.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {
    private SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public String search(@RequestParam(name = "query", required = false) String query,
                         Model model) {

        if (query == null || query.isBlank()) {
            model.addAttribute("errorMessage", "Empty field");
            return "search";
        }

        try {
            model.addAttribute("query", query);
            List<ObjectStorageDto> findObjects = searchService
                    .search(new SearchRequest(query.trim()));

            model.addAttribute("query", query);
            if (findObjects.isEmpty()) {
                model.addAttribute("errorMessage", "Nothing found");
            } else {
                model.addAttribute("find_objects", findObjects);
            }
        } catch (SearchFilesException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "search";
    }
}
