package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.jff.cloud.DTO.FileDTO;
import org.jff.cloud.global.NotResponseBody;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/material")
public class MaterialController {

    private final MaterialService materialService;

    private final SecurityUtil securityUtil;



    @PostMapping("/{teachingDayId}")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    public ResponseVO uploadFile(
            @PathVariable("teachingDayId") Long teachingDayId,
            @RequestPart("uploadFile") MultipartFile file
    ){
        LocalDate now = LocalDate.now();
        Long userId = securityUtil.getUserId();
        log.info("uploadFile: {}, {}, {}, {}", teachingDayId, userId, now, file.getOriginalFilename());
        return materialService.uploadFile(file,userId,teachingDayId);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER','ADMIN','MANAGER','STUDENT')")
    public List<FileDTO> getFileList(@RequestParam Long teachingDayId){
        return materialService.getFileList(teachingDayId);
    }

    @DeleteMapping("/file")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    public ResponseVO deleteFile(@RequestBody Map<String,String> params){
        Long userId = Long.parseLong(params.get("userId"));
        String key = params.get("key");
        return materialService.deleteFile(userId,key);
    }

    @GetMapping("/file")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER','ADMIN','MANAGER','STUDENT')")
    public List<FileDTO> getSelectedFileList(@RequestBody List<Long> materialIdList) {
        return materialService.getSelectedFileList(materialIdList);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    public ResponseVO deleteMaterial(@RequestBody Map<String,String> params){
        Long materialId = Long.parseLong(params.get("materialId"));
        return materialService.deleteMaterial(materialId);
    }

    @NotResponseBody
    @PostMapping("/homework")
    public String uploadHomework(
            @RequestPart("uploadFile") MultipartFile file,
            @RequestPart("homeworkId") Long homeworkId,
            @RequestPart("studentId") Long studentId
            ){
        return materialService.uploadHomework(file,homeworkId,studentId);

    }

}
