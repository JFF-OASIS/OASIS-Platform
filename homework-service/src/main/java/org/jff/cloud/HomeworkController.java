package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.Homework;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.utils.SecurityUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/homework")
public class HomeworkController {

    private final HomeworkService homeworkService;

    private final SecurityUtil securityUtil;

    @PostMapping()
    //工程师布置班级作业
    public ResponseVO assignHomework(@RequestBody Map<String,Object> params){
        Homework homework = (Homework) params.get("homework");
        return homeworkService.assignHomework(homework);
    }

    @PutMapping()
    //工程师修改班级作业
    public ResponseVO updateHomework(@RequestBody Map<String,Object> params){
        Homework homework = (Homework) params.get("homework");
        return homeworkService.updateHomework(homework);
    }

    @DeleteMapping
    //工程师删除班级作业
    public ResponseVO deleteHomework(@RequestBody Map<String,String> params){
        Long homeworkId = Long.parseLong(params.get("homeworkId"));
        Long classId = Long.parseLong(params.get("classId"));
        return homeworkService.deleteHomework(homeworkId,classId);
    }

    @PutMapping("/mark")
    public ResponseVO markHomework(@RequestBody Map<String,Object> params){
        //TODO:确定接口
        Long homeworkId = Long.parseLong(params.get("homeworkId").toString());
        Long studentId = Long.parseLong(params.get("studentId").toString());
        Integer score = Integer.parseInt(params.get("score").toString());
        String contentUrl = params.get("contentUrl").toString();
        return homeworkService.markHomework(homeworkId,studentId,score,contentUrl);
    }

    @PutMapping("/submit")
    //学生提交作业
    public ResponseVO submitHomework(
            @RequestPart("uploadFile") MultipartFile file,
            @RequestPart("classId") Long homeworkId
    ){
        Long uploaderId = securityUtil.getUserId();
        return homeworkService.submitHomework(file,homeworkId,uploaderId);
    }




}
