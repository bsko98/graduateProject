package com.example.task_back.service;

import com.example.task_back.dto.PrayerDto;
import com.example.task_back.entity.Prayer;
import com.example.task_back.repository.PrayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrayerService {

    private final PrayerRepository prayerRepository;

    @Autowired
    public PrayerService(PrayerRepository prayerRepository) {
        this.prayerRepository = prayerRepository;
    }

    public List<PrayerDto> findPrayer() {
        return prayerRepository.findByOrderByIdDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PrayerDto> findPrayerById(Long id){
        return prayerRepository.findById(id)
                .map(this::convertToDTO);
    }

    public PrayerDto savePrayer(PrayerDto prayerDto){
        Prayer prayer = new Prayer();
        prayer.setTitle(prayerDto.getTitle());
        prayer.setContent(prayerDto.getContent());
        prayer.setTimeOfPrayer(LocalDateTime.now());
        Prayer savedPrayer = prayerRepository.save(prayer);
        return convertToDTO(savedPrayer);
    }

    public Optional<PrayerDto> updatePrayer(Long id, PrayerDto prayerDto){
        return prayerRepository.findById(id).map(prayer -> {
            prayer.setTitle(prayerDto.getTitle());
            prayer.setContent(prayerDto.getContent());
            Prayer updatedUser = prayerRepository.save(prayer);
            return convertToDTO(updatedUser);
        });
    }

    public void deletePrayerById(Long id){
        prayerRepository.deleteById(id);
    }

    private PrayerDto convertToDTO(Prayer prayer) {
        PrayerDto prayerDto = new PrayerDto();
        prayerDto.setId(prayer.getId());
        prayerDto.setTitle(prayer.getTitle());
        prayerDto.setContent(prayer.getContent());
        prayerDto.setTimeOfPrayer(prayer.getTimeOfPrayer());
        return prayerDto;
    }

}
