package com.waes.repository;

import com.waes.domain.BinaryData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * BinaryDataRepository is used save / find BinaryData in repository.
 */
@Component
public interface BinaryDataRepository extends CrudRepository<BinaryData, Long>
{
    BinaryData findByIdAndSide(String id, String side);
}
