package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.jff.cloud.DTO.FileDTO;
import org.jff.cloud.global.NotResponseBody;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
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

    //TODO: 不能直接上传，需要与表“day_material”联系
    @PostMapping("/file")
    public ResponseVO uploadFile(
            @RequestPart("uploadFile") MultipartFile file,
            @RequestPart("classId") Long classId,
            @RequestPart("uploaderId") Long uploaderId,
            @RequestPart("type") String type
    ){
        LocalDate now = LocalDate.now();
        return materialService.uploadFile(file, classId, uploaderId,type, now);
    }

    @DeleteMapping("/file")
    public ResponseVO deleteFile(@RequestBody Map<String,String> params){
        Long userId = Long.parseLong(params.get("userId"));
        String key = params.get("key");
        return materialService.deleteFile(userId,key);
    }

    @GetMapping("/file")
    public List<FileDTO> getFileList(@RequestBody List<Long> materialIdList) {
        return materialService.getFileList(materialIdList);
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
