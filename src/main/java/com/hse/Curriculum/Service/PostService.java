package com.hse.Curriculum.Service;
import com.hse.Curriculum.Exception.Post.PostNotFoundException;
import com.hse.Curriculum.Exception.Post.DuplicatePostNameException;
import com.hse.Curriculum.Repository.PostRepository;
import com.hse.Curriculum.Models.Post;
import com.hse.Curriculum.Dto.PostDTO.PostRegisterDTO;
import com.hse.Curriculum.Dto.PostDTO.PostResponseDTO;
import com.hse.Curriculum.Dto.PostDTO.PostUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    /**
     * Registrar nuevo cargo
     */
    @Transactional
    public Post register(PostRegisterDTO registerDTO, Integer createdBy) {
        System.out.println("📝 Registrando nuevo cargo: " + registerDTO.getNamePost());

        // Validar que el nombre no esté duplicado
        if (postRepository.existsByNamePost(registerDTO.getNamePost())) {
            throw new DuplicatePostNameException(registerDTO.getNamePost());
        }

        Post post = new Post();
        post.setNamePost(registerDTO.getNamePost());
        post.setDescription(registerDTO.getDescription());
        post.setStatus(true);
        post.setCreatedBy(createdBy);
        post.setUpdatedBy(createdBy);

        Post savedPost = postRepository.save(post);
        System.out.println("✅ Cargo registrado con ID: " + savedPost.getPostId());

        return savedPost;
    }

    /**
     * Buscar cargo por ID
     */
    public Optional<Post> findById(Integer postId) {
        System.out.println("🔍 Buscando cargo con ID: " + postId);
        Optional<Post> post = postRepository.findById(postId);

        if (post.isPresent()) {
            System.out.println("✅ Cargo encontrado: " + post.get().getNamePost());
        } else {
            System.out.println("⚠️ Cargo con ID " + postId + " no existe");
        }

        return post;
    }

    /**
     * Obtener cargo por ID (lanza excepción si no existe)
     */
    public Post getById(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    /**
     * Buscar cargo por nombre
     */
    public Optional<Post> findByName(String namePost) {
        System.out.println("🔍 Buscando cargo: " + namePost);
        return postRepository.findByNamePost(namePost);
    }

    /**
     * Listar todos los cargos activos
     */
    public List<PostResponseDTO> getAllActive() {
        System.out.println("📋 Listando cargos activos");
        return postRepository.findByStatusTrue().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar todos los cargos (activos e inactivos)
     */
    public List<PostResponseDTO> getAll() {
        System.out.println("📋 Listando todos los cargos");
        return postRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar cargo
     */
    @Transactional
    public Post update(Integer postId, PostUpdateDTO updateDTO, Integer updatedBy) {
        System.out.println("🔄 Actualizando cargo ID: " + postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        // Validar nombre duplicado (excluyendo el cargo actual)
        if (updateDTO.getNamePost() != null &&
                !updateDTO.getNamePost().equals(post.getNamePost()) &&
                postRepository.existsByNamePostAndPostIdNot(updateDTO.getNamePost(), postId)) {
            throw new DuplicatePostNameException(updateDTO.getNamePost());
        }

        // Actualizar campos
        if (updateDTO.getNamePost() != null) {
            post.setNamePost(updateDTO.getNamePost());
        }
        if (updateDTO.getDescription() != null) {
            post.setDescription(updateDTO.getDescription());
        }

        post.setUpdatedBy(updatedBy);

        Post updatedPost = postRepository.save(post);
        System.out.println("✅ Cargo actualizado exitosamente");

        return updatedPost;
    }

    /**
     * Deshabilitar cargo (soft delete)
     */
    @Transactional
    public void disable(Integer postId, Integer updatedBy) {
        System.out.println("🔄 Deshabilitando cargo ID: " + postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.setStatus(false);
        post.setUpdatedBy(updatedBy);
        postRepository.save(post);

        System.out.println("✅ Cargo deshabilitado exitosamente");
    }

    /**
     * Habilitar cargo
     */
    @Transactional
    public void enable(Integer postId, Integer updatedBy) {
        System.out.println("🔄 Habilitando cargo ID: " + postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.setStatus(true);
        post.setUpdatedBy(updatedBy);
        postRepository.save(post);

        System.out.println("✅ Cargo habilitado exitosamente");
    }

    /**
     * Verificar si existe cargo por ID
     */
    public boolean existsById(Integer postId) {
        return postRepository.existsById(postId);
    }

    /**
     * Verificar si existe cargo por nombre
     */
    public boolean existsByName(String namePost) {
        return postRepository.existsByNamePost(namePost);
    }

    /**
     * Buscar cargos por nombre (búsqueda parcial)
     */
    public List<PostResponseDTO> searchByName(String namePost) {
        System.out.println("🔍 Buscando cargos que contengan: " + namePost);
        return postRepository.findByNamePostContainingIgnoreCase(namePost).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertir entidad a DTO de respuesta
     */
    private PostResponseDTO convertToResponseDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setPostId(post.getPostId());
        dto.setNamePost(post.getNamePost());
        dto.setDescription(post.getDescription());
        dto.setStatus(post.getStatus());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }
}
