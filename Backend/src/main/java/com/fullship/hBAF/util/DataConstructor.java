package com.fullship.hBAF.util;

import com.fullship.hBAF.domain.member.repository.MemberRepository;
import com.fullship.hBAF.global.api.service.DataApiService;
import com.uber.h3core.exceptions.LineUndefinedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
@Component
@RequiredArgsConstructor
@Slf4j
public class DataConstructor {

    private final DataApiService dataApiService;
    private final BarrierFreeConstructor barrierFreeConstructor;
    private final DummyGenerator dummyGenerator;
    private final MemberRepository memberRepository;
    private final AltitudeCrawler altitudeCrawler;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void dataConstructor() throws LineUndefinedException, IOException, ParserConfigurationException, SAXException, ParseException {
//        dataApiService.saveBusInfo();
//        dataApiService.saveRoute();
//        dataApiService.saveDetailRoute();
//        dataApiService.saveStopInfo();
//        dataApiService.saveSubway();
//        H3.setH3Index();
//        barrierFreeConstructor.saveBarrierFree();
//        barrierFreeConstructor.saveElectricWheelchairExcel();
//        barrierFreeConstructor.saveBarrierFree();
//        barrierFreeConstructor.setBarrierfreeInfo();
//        dummyGenerator.createDummyMembers();
        altitudeCrawler.crawling();
    }

}
