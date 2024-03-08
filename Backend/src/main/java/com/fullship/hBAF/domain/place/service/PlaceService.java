package com.fullship.hBAF.domain.place.service;

import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import com.fullship.hBAF.global.api.response.TransitPathForm;
import com.fullship.hBAF.global.api.service.TMapApiService;
import com.fullship.hBAF.global.api.service.command.SearchPathToTransitCommand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

  private final PlaceRepository placeRepository;
  private final TMapApiService tMapApiService;

  public String useTransitPath(SearchPathToTransitCommand command) throws ParseException {
    List<TransitPathForm> list = tMapApiService.searchPathToTransit(command);

    List<TransitPathForm> transferList = new ArrayList<>(list);
    Collections.sort(transferList,
        (o1, o2) -> (int) (o1.getTransferCount() - o2.getTransferCount()));

    int idx = 0;
    long min = transferList.get(0).getTransferCount();
    for (int i = 0; i < transferList.size(); i++, idx++) {
      if(transferList.get(i).getTransferCount() > min + 1)
        break;
    }

    for(int i = idx; i < transferList.size(); i++){
      transferList.remove(idx);
    }



    return null;
  }
}
