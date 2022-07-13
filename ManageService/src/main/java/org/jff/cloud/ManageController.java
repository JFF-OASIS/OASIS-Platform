package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.global.NotResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/manage")
public class ManageController {


    @NotResponseBody
    @GetMapping("/class/findStudentIdByClassId")
    public List<Long> findStudentIdByClassId(Long classId) {

    }

}
