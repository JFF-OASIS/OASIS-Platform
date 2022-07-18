package org.jff.cloud;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.DTO.FileDTO;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MaterialService {
    private static final String secretId = "AKIDQilW1GEvLVO0MRFm1zg6F7i25qMcbJku";
    //TODO:记得加密
    private static final String secretKey = "ua2D7l8E3cttd5GAoBG4cQxDCuLVNzKN";
    private static final String bucketName = "zhw-1312170899";
    private static final String filePrefix = "oasis/file/teachingPlan/";
    private static final String homeworkPrefix = "oasis/file/homework/";

    private final MaterialMapper materialMapper;

    private final RestTemplate restTemplate;

    COSCredentials cred = null;

    COSClient cosClient = null;

    public MaterialService(MaterialMapper materialMapper, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.materialMapper = materialMapper;
        this.cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region("ap-chengdu");
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        cosClient = new COSClient(cred, clientConfig);
        getInfo();
    }

    private void getInfo() {
        List<Bucket> buckets = cosClient.listBuckets();
        for (Bucket bucketElement : buckets) {
            String bucketName = bucketElement.getName();
            String bucketLocation = bucketElement.getLocation();
            log.info("bucketName:{},bucketLocation:{}", bucketName, bucketLocation);
        }
    }

    public static File multipartToFile(MultipartFile multipart, String fileName) {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        try {
            multipart.transferTo(convFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return convFile;
    }


    private String getFileUrl(String key) {
        return cosClient.getObjectUrl(bucketName, key).toString();
    }

    public ResponseVO uploadFile(MultipartFile multipartFile,
                                 Long uploaderId,
                                 Long teachingDayId) {
        //桶里的文件需要与数据库中保持一致,id为key
        //以teachingDayId为单位来进行保存
        //根据teachingDayId查teachingPlanId
        Long teachingPlanId = restTemplate
                .getForObject("http://plan-service/api/v1/plan/getTeachingPlanIdByTeachingDayId?teachingDayId=" + teachingDayId,
                        Long.class);
        //根据teachingDayId查具体的时间来确定uploadTime
        LocalDate date = restTemplate
                .getForObject("http://plan-service/api/v1/plan/getTeachingDateByTeachingDayId?teachingDayId=" + teachingDayId,
                        LocalDate.class);


        //先上传到桶
        String fileName = multipartFile.getOriginalFilename();
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);
        File file = multipartToFile(multipartFile, fileName);
        String key = filePrefix + teachingPlanId.toString() + "/" + teachingDayId.toString() + "/" + fileName;
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        log.info("putObjectResult:{}", putObjectResult);


        //再在数据库中新增记录
        String url = getFileUrl(key);
        org.jff.cloud.entity.File fileEntity = org.jff.cloud.entity.File.builder()
                .teachingDayId(teachingDayId)
                .name(fileName)
                .type(type)
                .url(url)
                .key(key)
                .uploadTime(date)
                .uploaderId(uploaderId)
                .build();

        materialMapper.insert(fileEntity);
        return new ResponseVO(ResultCode.SUCCESS, "上传成功");
    }


    public ResponseVO deleteFile(Long userId, String key) {
        //TODO：userId用来鉴权
        //先删桶中的数据
        cosClient.deleteObject(bucketName, key);
        log.info("成功删除文件 key:{}", key);

        //再删除数据库中的项目

        org.jff.cloud.entity.File file = materialMapper.findFileByKey(key);
        materialMapper.deleteById(file.getId());

        return new ResponseVO(ResultCode.SUCCESS, "删除成功");
    }

    public List<FileDTO> getSelectedFileList(List<Long> materialList) {
        List<FileDTO> fileDTOList = new ArrayList<>();
        for (Long materialId : materialList) {
            org.jff.cloud.entity.File file = materialMapper.selectById(materialId);
            fileDTOList.add(FileDTO.builder()
                    .name(file.getName())
                    .type(file.getType())
                    .url(file.getUrl())
                    .uploadTime(file.getUploadTime())
                    .key(file.getKey())
                    .build());
        }
        return fileDTOList;
    }

    public String uploadHomework(MultipartFile multipartFile, Long homeworkId, Long studentId) {
        String fileName = multipartFile.getOriginalFilename();
        File file = multipartToFile(multipartFile, fileName);
        //不加studentId的话，文件名可能会有重复，key可能就会重复
        String key = homeworkPrefix + homeworkId.toString() + "/" + studentId.toString() + "/" + fileName;
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        log.info("putObjectResult:{}", putObjectResult);
        return getFileUrl(key);
    }

    public List<FileDTO> getFileList(Long teachingDayId) {
        List<FileDTO> fileDTOList = new ArrayList<>();
        QueryWrapper<org.jff.cloud.entity.File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teaching_day_id", teachingDayId);
        List<org.jff.cloud.entity.File> fileList = materialMapper.selectList(queryWrapper);
        for (org.jff.cloud.entity.File file : fileList) {
            fileDTOList.add(FileDTO.builder()
                    .name(file.getName())
                    .type(file.getType())
                    .url(file.getUrl())
                    .uploadTime(file.getUploadTime())
                    .key(file.getKey())
                    .build());
        }
        return fileDTOList;
    }

    public ResponseVO deleteMaterial(Long materialId) {
        //TODO:桶里的文件也要删除
        materialMapper.deleteById(materialId);
        return new ResponseVO(ResultCode.SUCCESS, "删除文件成功");
    }
}
