package org.nikita.spingproject.filestorage.commons.breadcrumbs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Breadcrumb {
    private String name;
    private String link;
}
