package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.Project;
import org.jff.cloud.entity.ProjectStatus;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.vo.ProjectVO;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;


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
}
