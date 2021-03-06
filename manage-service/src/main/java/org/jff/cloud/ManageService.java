package org.jff.cloud;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.*;
import org.jff.cloud.entity.*;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.mapper.*;
import org.jff.cloud.vo.UpdateStudentClassVO;
import org.jff.cloud.vo.UpdateStudentGroupVO;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ManageService {

    private final TeachingClassMapper teachingClassMapper;

    private final GroupMapper groupMapper;

    private final Tracer tracer;

    private final RoleMapper roleMapper;

    private final StudentMapper studentMapper;

    private final UserMapper userMapper;

    private final RestTemplate restTemplate;


    public List<Long> findStudentIdByClassId(Long classId) {
        log.info("findStudentIdByClassId: {}", classId);
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("student_id");
        queryWrapper.eq("class_id", classId);
        List<Long> studentIdList = studentMapper.selectList(queryWrapper).stream().map(Student::getStudentId)
                .collect(Collectors.toList());
        log.info("studentIdList: {}", studentIdList);
        return studentIdList;
    }

    public ResponseVO addClass(String className, Long teacherId, Long engineerId, Long teachingPlanId) {
        TeachingClass teachingClass = TeachingClass.builder()
                .className(className)
                .teacherId(teacherId)
                .engineerId(engineerId)
                .teachingPlanId(teachingPlanId)
                .build();
        teachingClassMapper.insert(teachingClass);
        return new ResponseVO(ResultCode.SUCCESS, "??????????????????");
    }

    public ResponseVO addGroup(String groupName, Long classId) {
        //??????group
        Group group = Group.builder()
                .groupName(groupName)
                .classId(classId)
                .managerId(0L)
                .requirementAnalysisScore(0)
                .designScore(0)
                .projectDevelopmentScore(0)
                .projectReportScore(0)
                .qualityScore(0)
                .build();
        groupMapper.insert(group);
        return new ResponseVO(ResultCode.SUCCESS, "?????????????????????");
    }

    public ResponseVO addStudentToClass(UpdateStudentClassVO updateStudentVO) {
        List<Long> studentIds = updateStudentVO.getStudentIds();
        for (Long studentId : studentIds) {
            Student student = studentMapper.selectById(studentId);
            student.setClassId(updateStudentVO.getClassId());
            studentMapper.updateById(student);
        }
        return new ResponseVO(ResultCode.SUCCESS, "??????????????????");
    }

    public ResponseVO deleteStudentFromClass(UpdateStudentClassVO updateStudentVO) {
        List<Long> studentIds = updateStudentVO.getStudentIds();
        studentIds.forEach(studentId -> {
            Student student = studentMapper.selectById(studentId);
            student.setClassId(null);
            studentMapper.updateById(student);
        });
        return new ResponseVO(ResultCode.SUCCESS, "??????????????????");
    }

    public ResponseVO deleteGroup(Long groupId) {
        //??????????????????????????????
        List<Student> students = studentMapper.selectList(new QueryWrapper<Student>().eq("group_id", groupId));
        students.forEach(student -> {
            student.setGroupId(0L);
            studentMapper.updateById(student);
        });
        //???????????????
        groupMapper.deleteById(groupId);
        return new ResponseVO(ResultCode.SUCCESS, "??????????????????");
    }

    public ResponseVO deleteStudentFromGroup(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        student.setGroupId(0L);
        studentMapper.updateById(student);
        return new ResponseVO(ResultCode.SUCCESS, "??????????????????");
    }

    public ResponseVO getClassInfo(Long classId) {
        TeachingClass teachingClass = teachingClassMapper.selectById(classId);
        ClassDTO classDTO = ClassDTO.builder()
                .classId(teachingClass.getClassId())
                .className(teachingClass.getClassName())
                .teacherId(teachingClass.getTeacherId())
                .engineerId(teachingClass.getEngineerId())
                .teachingPlanId(teachingClass.getTeachingPlanId())
                .build();
        //???????????????????????????
        List<StudentDTO> students = new ArrayList<>();
        List<Student> studentList = studentMapper.selectList(new QueryWrapper<Student>().eq("class_id", classId));
        for (Student student : studentList) {
            StudentDTO studentDTO = StudentDTO.builder()
                    .studentId(student.getStudentId())
                    .studentName(student.getName())
                    .build();
            students.add(studentDTO);
        }
        classDTO.setStudents(students);
        return new ResponseVO(ResultCode.SUCCESS, classDTO);
    }

    public User getUser(Long userId) {
        return userMapper.selectById(userId);
    }

    public ResponseVO deleteUser(Long userId) {
        userMapper.deleteById(userId);
        return new ResponseVO(ResultCode.SUCCESS, "??????????????????");
    }

    public ResponseVO deleteClass(Long classId) {
        teachingClassMapper.deleteById(classId);
        return new ResponseVO(ResultCode.SUCCESS, "??????????????????");
    }

    public ResponseVO updateClass(String className, Long teacherId, Long engineerId, Long teachingPlanId, Long classId) {
        TeachingClass teachingClass = teachingClassMapper.selectById(classId);
        teachingClass.setClassName(className);
        teachingClass.setTeacherId(teacherId);
        teachingClass.setEngineerId(engineerId);
        teachingClass.setTeachingPlanId(teachingPlanId);
        teachingClassMapper.updateById(teachingClass);
        return new ResponseVO(ResultCode.SUCCESS, "??????????????????");
    }

    public GroupDTO getGroupInfo(Long groupId) {

        //TODO:???SQL??????
        GroupDTO groupDTO = groupMapper.getGroupDTO(groupId);
//        Group group = groupMapper.selectById(groupId);
//        groupDTO.setGroup(group);
//        //??????????????????????????????
//        List<StudentDTO> students = new ArrayList<>();
//        List<Student> studentList = studentMapper.selectList(new QueryWrapper<Student>().eq("group_id", groupId));
//        for (Student student : studentList) {
//            StudentDTO studentDTO = StudentDTO.builder()
//                    .studentId(student.getStudentId())
//                    .studentName(student.getName())
//                    .build();
//            students.add(studentDTO);
//        }
//        groupDTO.setStudents(students);
        return groupDTO;
    }

    public int getStudentScore(Long studentId) {
        //TODO: ?????????????????????
        return studentMapper.selectById(studentId).getScore();
    }

    public ResponseVO updateGroup(Group group) {
        groupMapper.updateById(group);
        return new ResponseVO(ResultCode.SUCCESS, "????????????????????????");
    }

    public List<GroupDTO> getGroupList(Long classId) {

        List<GroupDTO> groups = groupMapper.getGroupDTOList(classId);
        log.info("groups:{}", groups);



//        List<SimpleGroupDTO> groupList = new ArrayList<>();
//        List<Group> groups = groupMapper.selectList(new QueryWrapper<Group>().eq("class_id", classId));
//        for (Group group : groups) {
//            SimpleGroupDTO simpleGroupDTO = SimpleGroupDTO.builder()
//                    .groupId(group.getGroupId())
//                    .groupName(group.getGroupName())
//                    .build();
//            groupList.add(simpleGroupDTO);
//        }
//        return groupList;
        return groups;
    }


    public ResponseVO addStudentToGroup(UpdateStudentGroupVO updateStudentGroupVO) {
        List<Long> studentIds = updateStudentGroupVO.getStudentIds();
        for(Long studentId : studentIds){
            Student student = studentMapper.selectById(studentId);
            student.setGroupId(updateStudentGroupVO.getGroupId());
            studentMapper.updateById(student);
        }
        return new ResponseVO(ResultCode.SUCCESS, "??????????????????");
    }

    public List<UserDTO> getUserList() {
        //TODO:??????????????????????????????????????????

        List<UserDTO> list = userMapper.getUserDTO();
        log.info("getUserList: {}", list);
        //
        //????????????????????????
//        List<UserDTO> userList = new ArrayList<>();
//        ScopedSpan userListSpan = tracer.startScopedSpan("getUserList");
//        List<User> users = userMapper.selectList(new QueryWrapper<User>());
//        userListSpan.end();
//        ScopedSpan roleSpan = tracer.startScopedSpan("getRoleOfEach");
//        users.forEach(user -> {
//            UserDTO userDTO = new UserDTO();
//            userDTO.setUserId(user.getUserId());
//            userDTO.setUsername(user.getUsername());
//            //???????????????????????????
//            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
//            roleQueryWrapper.eq("user_id", user.getUserId());
//            List<String> roles = roleMapper.findRolesByUserId(user.getUserId());
//            log.info("roles: {}", roles);
//            //???????????????????????????????????????????????????????????????null
//            if (roles.isEmpty()) {
//               userDTO.setRole("");
//            } else {
//                userDTO.setRole(roles.get(0));
//            }
//            userList.add(userDTO);
//        });
//        roleSpan.end();
//
//        return userList;
        return list;
    }

    public List<SimpleClassDTO> getClassList(Long userId,RoleStatus role) {
        List<SimpleClassDTO> classList = new ArrayList<>();
        QueryWrapper<TeachingClass> queryWrapper = new QueryWrapper<>();
        //????????????????????????????????????????????????????????????
        if (role == RoleStatus.ROLE_ENGINEER){
            queryWrapper.eq("engineer_id", userId);
            teachingClassMapper.selectList(queryWrapper).forEach(teachingClass -> {
                SimpleClassDTO simpleClassDTO = SimpleClassDTO.builder()
                        .classId(teachingClass.getClassId())
                        .className(teachingClass.getClassName())
                        .teacherId(teachingClass.getTeacherId())
                        .engineerId(teachingClass.getEngineerId())
                        .teachingPlanId(teachingClass.getTeachingPlanId())
                        .build();
                //??????teacherId???????????????
                User teacher = userMapper.selectById(teachingClass.getTeacherId());
                if (teacher != null){
                    simpleClassDTO.setTeacherName(teacher.getUsername());
                }

                //??????engineerId???????????????
                User engineer = userMapper.selectById(teachingClass.getEngineerId());
                if (engineer != null){
                    simpleClassDTO.setEngineerName(engineer.getUsername());
                }
                classList.add(simpleClassDTO);
            });
        }
        if (role == RoleStatus.ROLE_TEACHER){
            queryWrapper.eq("teacher_id", userId);
            teachingClassMapper.selectList(queryWrapper).forEach(teachingClass -> {
                SimpleClassDTO simpleClassDTO = SimpleClassDTO.builder()
                        .classId(teachingClass.getClassId())
                        .className(teachingClass.getClassName())
                        .teacherId(teachingClass.getTeacherId())
                        .engineerId(teachingClass.getEngineerId())
                        .teachingPlanId(teachingClass.getTeachingPlanId())
                        .build();
                //??????teacherId???????????????
                User teacher = userMapper.selectById(teachingClass.getTeacherId());
                if (teacher != null){
                    simpleClassDTO.setTeacherName(teacher.getUsername());
                }
                //??????engineerId???????????????
                User engineer = userMapper.selectById(teachingClass.getEngineerId());
                if (engineer != null){
                    simpleClassDTO.setEngineerName(engineer.getUsername());
                }


                classList.add(simpleClassDTO);
            });
        }
        if(role==RoleStatus.ROLE_ADMIN){
            teachingClassMapper.selectList(queryWrapper).forEach(teachingClass -> {
                SimpleClassDTO simpleClassDTO = SimpleClassDTO.builder()
                        .classId(teachingClass.getClassId())
                        .className(teachingClass.getClassName())
                        .teacherId(teachingClass.getTeacherId())
                        .engineerId(teachingClass.getEngineerId())
                        .teachingPlanId(teachingClass.getTeachingPlanId())
                        .build();
                //??????teacherId???????????????
                User teacher = userMapper.selectById(teachingClass.getTeacherId());
                if (teacher != null){
                    simpleClassDTO.setTeacherName(teacher.getUsername());
                }

                //??????engineerId???????????????
                User engineer = userMapper.selectById(teachingClass.getEngineerId());
                if (engineer != null){
                    simpleClassDTO.setEngineerName(engineer.getUsername());
                }

                classList.add(simpleClassDTO);
            });
        }
        return classList;
    }

    public List<Student> getAllStudent() {
        return studentMapper.selectList(new QueryWrapper<Student>());
    }

    public ResponseVO updateStudentInClass(UpdateStudentClassVO updateStudentVO) {
        List<Long> studentIds = updateStudentVO.getStudentIds();
        for (Long studentId : studentIds) {
            Student student = studentMapper.selectById(studentId);
            student.setClassId(updateStudentVO.getClassId());
            studentMapper.updateById(student);
        }
        return new ResponseVO(ResultCode.SUCCESS, "??????????????????");
    }

    public Map<String, Long> findTeacherIdAndEngineerIdByStudentId(Long studentId) {
        Map<String, Long> map = new HashMap<>();
        Student student = studentMapper.selectById(studentId);
        TeachingClass teachingClass = teachingClassMapper.selectById(student.getClassId());
        map.put("teacherId", teachingClass.getTeacherId());
        map.put("engineerId", teachingClass.getEngineerId());
        return map;
    }

    public TeachingPlanDTO getTeachingPlan(Long classId) {
        restTemplate.getForObject("http://plan-service/api/v1/plan/getTeachingPlanByClassId?classId=" + classId, TeachingPlanDTO[].class);
        return null;
    }

    public List<Long> findProjectIdListByClassId(Long classId) {
        List<Long> list = new ArrayList<>();
        QueryWrapper<Group> groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("class_id", classId);
        List<Group> groups = groupMapper.selectList(groupQueryWrapper);
        for (Group group : groups) {
            list.add(group.getProjectId());
        }
        return list;
    }

    public String getStudentNameByStudentId(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        return student.getName();
    }
}
