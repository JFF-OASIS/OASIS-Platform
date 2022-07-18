package org.jff.cloud;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.Project;
import org.jff.cloud.entity.ProjectStatus;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.vo.ProjectVO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;

    private final RestTemplate restTemplate;


    public ResponseVO createProject(ProjectVO projectVO) {
        Project project = Project.builder()
                .name(projectVO.getName())
                .description(projectVO.getDescription())
                .status(ProjectStatus.CREATED)
                .degreeOfDifficulty(projectVO.getDegreeOfDifficulty())
                .technologyRequirement(projectVO.getTechnologyRequirement())
                .language(projectVO.getLanguage())
                .build();
        projectMapper.insert(project);

        return new ResponseVO(ResultCode.SUCCESS, "项目创建成功");

    }

    public ResponseVO updateProject(ProjectVO projectVO) {
        Project project = projectMapper.selectById(projectVO.getProjectId());
        project.setName(projectVO.getName());
        project.setDescription(projectVO.getDescription());
        project.setDegreeOfDifficulty(projectVO.getDegreeOfDifficulty());
        project.setTechnologyRequirement(projectVO.getTechnologyRequirement());
        project.setLanguage(projectVO.getLanguage());
        projectMapper.updateById(project);
        return new ResponseVO(ResultCode.SUCCESS, "项目更新成功");
    }

    public Project getProjectByGroupId(Long groupId) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        return projectMapper.selectOne(queryWrapper);
    }

    public ResponseVO deleteProject(Long projectId) {
        projectMapper.deleteById(projectId);
        return new ResponseVO(ResultCode.SUCCESS, "项目删除成功");
    }

    public ResponseVO auditProject(Long projectId, ProjectStatus status) {
        Project project = projectMapper.selectById(projectId);
        project.setStatus(status);
        projectMapper.updateById(project);
        return new ResponseVO(ResultCode.SUCCESS, "项目审核成功");
    }

    public List<Project> getProjectList(Long classId) {
        //根据classId先查对应的group中对应的classId
        List<Project> projectList = new ArrayList<>();
        Long[] list = restTemplate
                .getForObject("http://manage-service/api/v1/manage/group/findProjectIdListByClassId?classId=" + classId, Long[].class);
        log.info("list: {}", list);
        for (Long projectId : list) {
            Project project = projectMapper.selectById(projectId);
            if (project != null) {
                projectList.add(project);
            }
        }
        log.info("projectList: {}", projectList);
        return projectList;
    }
}
