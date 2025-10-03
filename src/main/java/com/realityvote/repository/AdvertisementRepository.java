package com.realityvote.repository;

import com.realityvote.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> { }
