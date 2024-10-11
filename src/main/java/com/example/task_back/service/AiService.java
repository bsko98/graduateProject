package com.example.task_back.service;

import com.example.task_back.entity.Prayer;
import com.example.task_back.repository.PrayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AiService {
    private final PrayerRepository prayerRepository;
    private final OpenAiChatModel chatModel;

    @Autowired
    public AiService(PrayerRepository prayerRepository, OpenAiChatModel chatModel) {
        this.prayerRepository = prayerRepository;
        this.chatModel = chatModel;
    }

    public String generatePrayer(String message){
        return chatModel.call(message);
    }

    public Map<String,Integer> analysisPrayer(){
        LocalDateTime startDate = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.of(0,0,0));
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
        List<Prayer> prayerList = prayerRepository.findByTimeOfPrayerBetween(startDate,endDate);
        String prompt = createBatchPrompt(prayerList);
        System.out.println(prompt);
        String ans = chatModel.call(prompt);
        System.out.println("답변: "+ans);
        System.out.println(ans.trim());
        String[] response = ans.replaceAll(" ","").split(",");
        Map<String,Integer> map = new HashMap<String,Integer>();
        for(String res : response){
            System.out.println("카테고리: "+ res);
           if(map.containsKey(res)){
               map.put(res,map.get(res)+1);
           }else{
               map.put(res,1);
           }
        }
        System.out.println(map.toString());
        return map;
    }

    private String createBatchPrompt(List<Prayer> prayers) {
        StringBuilder promptBuilder = new StringBuilder("다음 기도제목을 적절한 카테고리로 분류해서 카테고리를 단어로만 개행없이 한 줄로 콤마로 구분해서 대답해.\n");
        promptBuilder.append("카테고리는 '감사', '중보', '회개', '간구', '인도', '고난과 위로', '성령의 임재' 중 하나여야해:\n\n");
                /*.append("1. 건강\n")
                .append("2. 학업\n")
                .append("3. 영적 성장\n")
                .append("4. 진로\n")
                .append("5. 회개\n")
                .append("6. 기타\n\n");*/

        // 각 기도제목을 추가
        for (int i = 0; i < prayers.size(); i++) {
            promptBuilder.append(String.format("%d 기도제목: \"%s\"\n", i + 1, prayers.get(i).getContent()));
        }

        /*promptBuilder.append("\n각 기도제목의 분류:");*/
        return promptBuilder.toString();
    }

    public Integer getPrayerForWeek(){
        LocalDateTime startDate = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.of(0,0,0));
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
        return prayerRepository.countByTimeOfPrayerBetween(startDate,endDate);

    }

}