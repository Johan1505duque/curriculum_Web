package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkExperienceRepository extends JpaRepository <WorkExperience, Integer> {
    List<WorkExperience> findByUserUserIdOrderByStartDateDesc(Integer userId);
}
