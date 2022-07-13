package org.jff.cloud.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

    private String name;

    private String type;

    private String url;

    private LocalDate uploadTime;

    private String key;

}
