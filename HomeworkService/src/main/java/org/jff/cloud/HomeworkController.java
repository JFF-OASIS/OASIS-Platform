package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.Homework;
import org.jff.cloud.global.ResponseVO;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/homework")
public class HomeworkController {

    private final HomeworkService homeworkService;

    @PostMapping("/assign")
    public ResponseVO assignHomework(@RequestBody Map<String,Object> params){
        Long classId = Long.parseLong(params.get("classId").toString());
        Homework homework = (Homework) params.get("homework");
        return homeworkService.assignHomework(classId,homework);
    }



}
