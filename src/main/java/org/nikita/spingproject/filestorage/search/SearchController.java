package org.nikita.spingproject.filestorage.search;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.search.dto.SearchFileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String search(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam(name = "query", required = false) String query,
                         Model model) {

        List<ObjectStorageDto> findObjects = searchFileService.search(new SearchFileDto(query, userDetails.getUsername()));

        model.addAttribute("find_objects", findObjects);

        return "search";
    }
}
