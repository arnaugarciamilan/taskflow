package com.taskflow.service;

import com.taskflow.dto.ProjectDto;
import com.taskflow.entity.Project;
import com.taskflow.entity.User;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.exception.UnauthorizedException;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<ProjectDto.Response> findAll() {
        return projectRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProjectDto.Response> findByOwner(Long ownerId) {
        return projectRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProjectDto.Response> findByOwnerEmail(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return projectRepository.findByOwnerIdOrderByCreatedAtDesc(owner.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public ProjectDto.Response findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        return toResponse(project);
    }

    @Transactional
    public ProjectDto.Response create(ProjectDto.Request request, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + ownerEmail));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();

        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto.Response update(Long id, ProjectDto.Request request, String currentUserEmail) {
        Project project = getProjectAndValidateOwner(id, currentUserEmail);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(Long id, String currentUserEmail) {
        getProjectAndValidateOwner(id, currentUserEmail);
        projectRepository.deleteById(id);
    }

    private Project getProjectAndValidateOwner(Long id, String currentUserEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        if (!project.getOwner().getEmail().equals(currentUserEmail)) {
            throw new UnauthorizedException("You don't have permission to modify this project");
        }
        return project;
    }

    public ProjectDto.Response toResponse(Project project) {
        ProjectDto.Response response = new ProjectDto.Response();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setOwner(userService.toResponse(project.getOwner()));
        response.setCreatedAt(project.getCreatedAt());
        response.setTaskCount(project.getTasks() != null ? project.getTasks().size() : 0);
        return response;
    }
}
