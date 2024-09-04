package org.nikita.spingproject.filestorage.search;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.directory.exception.DirectorySearchFilesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchFileService searchFileService;

    @GetMapping
    public String search(@RequestParam(name = "query", required = false) String query,
                         Model model) {

        if (query == null || query.isBlank()) {
            model.addAttribute("errorSearch", "Empty field");
            return "search";
        }

        List<ObjectStorageDto> findObjects = null;
        try {
            findObjects = searchFileService
                    .search(new SearchFileDto(query));
        } catch (DirectorySearchFilesException e) {
            model.addAttribute("errorSearch", e.getMessage());
        }

        if (findObjects.isEmpty()) {
            model.addAttribute("errorSearch", "Nothing found");
        } else {
            model.addAttribute("find_objects", findObjects);
        }

        return "search";
    }
}
