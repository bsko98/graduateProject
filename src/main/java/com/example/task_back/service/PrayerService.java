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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final AiService aiService;

    @Autowired
    public PrayerService(PrayerRepository prayerRepository, UserRepository userRepository,
                         GroupRepository groupRepository,UserGroupRepository userGroupRepository, AiService aiService) {
        this.prayerRepository = prayerRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.aiService = aiService;
    }

    public List<PrayerDto> findPrayer(String username) {
        return prayerRepository.findByUserUsernameAndDeletedFalseOrderByIdDesc(username).stream()
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
        prayer.setIsPublic(prayerDto.getIsPublic());
        String category = aiService.analysisPrayerCategory(prayerDto.getContent());
        prayer.setCategory(category);
        List<String> keyword = aiService.analysisPrayerKeywords(prayerDto.getContent());
        prayer.setKeywords(keyword);
        Prayer savedPrayer = prayerRepository.save(prayer);
        return convertToDTO(savedPrayer);
    }

    public Optional<PrayerDto> updatePrayer(Long id, PrayerDto prayerDto){
        User user = userRepository.findByUsername(prayerDto.getUsername());
        return prayerRepository.findById(id).map(prayer -> {
            prayer.setTitle(prayerDto.getTitle());
            prayer.setContent(prayerDto.getContent());
            prayer.setIsPublic(prayerDto.getIsPublic());
            String category = aiService.analysisPrayerCategory(prayerDto.getContent());
            prayer.setCategory(category);
            List<String> keyword = aiService.analysisPrayerKeywords(prayerDto.getContent());
            prayer.setKeywords(keyword);
            Prayer updatedUser = prayerRepository.save(prayer);
            return convertToDTO(updatedUser);
        });
    }

    public PrayerDto deletePrayerById(Long id, PrayerDto prayerDto){
        Prayer prayer = prayerRepository.findById(id).orElseThrow(() -> new NullPointerException("Prayer not found"));
        prayer.setDeleted(true);
        Prayer updatedUser = prayerRepository.save(prayer);
        return convertToDTO(updatedUser);
    }

    public List<PrayerDto> getGroupPrayers(String groupName) {
        List<Long> groupIdList = groupRepository.findIdByGroupName(groupName);
        List<Long> userIdList = userGroupRepository.findUserIdIn(groupIdList);
        return prayerRepository.findLatestPrayerForEachUser(userIdList).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PrayerDto convertToDTO(Prayer prayer) {
        PrayerDto prayerDto = new PrayerDto();
        prayerDto.setId(prayer.getId());
        prayerDto.setTitle(prayer.getTitle());
        prayerDto.setContent(prayer.getContent());
        prayerDto.setTimeOfPrayer(prayer.getTimeOfPrayer());
        prayerDto.setUserNickname(prayer.getUser().getNickname());
        prayerDto.setIsPublic(prayer.getIsPublic());
        return prayerDto;
    }

    public List<PrayerDto> getPrayerContainsKeyword(String keyword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return prayerRepository.findByUser_UsernameAndKeywordsContaining(username, keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PrayerDto> findAllUserPrayer(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return prayerRepository.findAllByOrderByTimeOfPrayerDesc(pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Integer getAllUserPrayerCount() {
        return (int) prayerRepository.count();
    }

    public List<PrayerDto> findMyPrayerList(String id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return prayerRepository.findAllByUserUsernameOrderByTimeOfPrayerDesc(id, pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Integer getMyPrayerCount(String id) {
        return prayerRepository.countByUserUsername(id);
    }

    public Integer getGroupPrayerCount(String groupName) {
        List<Long> groupIdList = groupRepository.findIdByGroupName(groupName);
        List<Long> userIdList = userGroupRepository.findUserIdIn(groupIdList);
        return prayerRepository.countPrayerForEachUser(userIdList);
    }

    public List<PrayerDto> getGroupPrayerList(String groupName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Long> groupIdList = groupRepository.findIdByGroupName(groupName);
        List<Long> userIdList = userGroupRepository.findUserIdIn(groupIdList);

        return prayerRepository.findPrayerForEachUser(userIdList, pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
