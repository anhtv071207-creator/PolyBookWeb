package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Integer> {
    Address findFirstByUserId(Integer userId);
}
