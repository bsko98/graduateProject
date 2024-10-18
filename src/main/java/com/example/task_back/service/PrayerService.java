package com.example.task_back.service;

import com.example.task_back.dto.PrayerDto;
import com.example.task_back.entity.Group;
import com.example.task_back.entity.Prayer;
import com.example.task_back.entity.User;
import com.example.task_back.repository.GroupRepository;
import com.example.task_back.repository.PrayerRepository;
import com.example.task_back.repository.UserGroupRepository;
import com.example.task_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrayerService {

    private final PrayerRepository prayerRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;

    @Autowired
    public PrayerService(PrayerRepository prayerRepository, UserRepository userRepository,
                         GroupRepository groupRepository,UserGroupRepository userGroupRepository) {
        this.prayerRepository = prayerRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
    }

    public List<PrayerDto> findPrayer(String username) {
        User user = userRepository.findByUsername(username);
        return prayerRepository.findByUserUsernameOrderByIdDesc(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PrayerDto> findPrayerById(Long id){
        return prayerRepository.findById(id)
                .map(this::convertToDTO);
    }

    public PrayerDto savePrayer(PrayerDto prayerDto){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        Prayer prayer = new Prayer();
        prayer.setTitle(prayerDto.getTitle());
        prayer.setContent(prayerDto.getContent());
        prayer.setTimeOfPrayer(LocalDateTime.now());
        prayer.setUser(user);
        Prayer savedPrayer = prayerRepository.save(prayer);
        return convertToDTO(savedPrayer);
    }

    public Optional<PrayerDto> updatePrayer(Long id, PrayerDto prayerDto){
        User user = userRepository.findByUsername(prayerDto.getUsername());
        return prayerRepository.findById(id).map(prayer -> {
            prayer.setTitle(prayerDto.getTitle());
            prayer.setContent(prayerDto.getContent());
            prayer.setUser(user);
            Prayer updatedUser = prayerRepository.save(prayer);
            return convertToDTO(updatedUser);
        });
    }

    public void deletePrayerById(Long id){
        prayerRepository.deleteById(id);
    }

    public List<PrayerDto> getGroupPrayers(String groupName) {
        List<Long> groupIdList = groupRepository.findIdByGroupName(groupName);
        for(Long id : groupIdList){
            System.out.println("id값: "+id);
        }
        List<Long> userIdList = userGroupRepository.findUserIdIn(groupIdList);
        List<PrayerDto> prayerList = prayerRepository.findLatestPrayerForEachUser(userIdList).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());;
        for(PrayerDto p : prayerList){
            System.out.println(p.toString());
        }
        return prayerList;
    }

    private PrayerDto convertToDTO(Prayer prayer) {
        PrayerDto prayerDto = new PrayerDto();
        prayerDto.setId(prayer.getId());
        prayerDto.setTitle(prayer.getTitle());
        prayerDto.setContent(prayer.getContent());
        prayerDto.setTimeOfPrayer(prayer.getTimeOfPrayer());
        prayerDto.setUsername(prayer.getUser().getUsername());
        prayerDto.setUserNickname(prayer.getUser().getNickname());
        return prayerDto;
    }
}
