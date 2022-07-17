package org.jff.cloud.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jff.cloud.entity.Group;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO extends Group{

    private List<StudentDTO> students;
}
