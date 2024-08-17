package org.nikita.spingproject.filestorage.search.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchFileDto {
    private String name;
    private String userName;
}
