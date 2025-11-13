package com.khohoclieumnhongphong.khohoclieu.service.impl; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.resource.CategoryRequestDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.resource.ResourceTypeResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.entity.ResourceType;
import com.khohoclieumnhongphong.khohoclieu.exception.ResourceNotFoundException;
import com.khohoclieumnhongphong.khohoclieu.repository.ResourceTypeRepository;
import com.khohoclieumnhongphong.khohoclieu.service.IResourceTypeService;
import com.khohoclieumnhongphong.khohoclieu.util.SlugUtil;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceTypeServiceImpl implements IResourceTypeService {

    private final ResourceTypeRepository resourceTypeRepository;

    // Helper (private) để map Entity sang DTO
    private ResourceTypeResponseDTO mapToDTO(ResourceType type) {
        ResourceTypeResponseDTO dto = new ResourceTypeResponseDTO();
        dto.setId(type.getId());
        dto.setName(type.getName());
        dto.setSlug(type.getSlug());
        dto.setCreatedAt(type.getCreatedAt());
        return dto;
    }

    @Override
    public List<ResourceTypeResponseDTO> getAllResourceTypes() {
        return resourceTypeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResourceTypeResponseDTO getResourceTypeById(Long id) {
        ResourceType type = resourceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.type.notfound")); // i18n
        return mapToDTO(type);
    }

    @Override
    public ResourceTypeResponseDTO createResourceType(CategoryRequestDTO request) {
        String slug = SlugUtil.toSlug(request.getName());

        if (resourceTypeRepository.findBySlug(slug).isPresent()) {
            throw new ValidationException("error.type.slug.exists"); // i18n
        }

        ResourceType newType = new ResourceType();
        newType.setName(request.getName());
        newType.setSlug(slug);
        // (ResourceType không có description)

        ResourceType savedType = resourceTypeRepository.save(newType);
        return mapToDTO(savedType);
    }

    @Override
    public ResourceTypeResponseDTO updateResourceType(Long id, CategoryRequestDTO request) {
        ResourceType type = resourceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.type.notfound"));

        String newSlug = SlugUtil.toSlug(request.getName());

        resourceTypeRepository.findBySlug(newSlug).ifPresent(existingType -> {
            if (!existingType.getId().equals(id)) {
                throw new ValidationException("error.type.slug.exists"); // i18n
            }
        });

        type.setName(request.getName());
        type.setSlug(newSlug);

        ResourceType updatedType = resourceTypeRepository.save(type);
        return mapToDTO(updatedType);
    }

    @Override
    public void deleteResourceType(Long id) {
        if (!resourceTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("error.type.notfound");
        }

        try {
            resourceTypeRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Bắt lỗi nếu Type này đang được Resource sử dụng (nhờ ON DELETE RESTRICT)
            throw new ValidationException("error.type.inuse"); // i18n
        }
    }
}