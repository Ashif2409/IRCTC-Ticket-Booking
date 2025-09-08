package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrainService {
    private List<Train> trainList;
    private final String TRAIN_DB_PATH = "D:/CSE/webDevelopment/IRCTC-Ticket-Booking/app/src/main/java/org/example/localDb/train.json";
    private final ObjectMapper mapper=new ObjectMapper();

    public TrainService() throws IOException {
            System.out.println("Line 21");
        File trains=new File(TRAIN_DB_PATH);
        if(!trains.exists() || trains.length()==0){
            trainList=new ArrayList<>();
            return;
        }
        this.trainList=mapper.readValue(trains, new TypeReference<List<Train>>(){});
    }
    public List<Train> searchTrain(String src, String dest) {
        return trainList.stream()
                .filter(train -> {
                    List<String> stations = train.getStations();

                    int srcIdx = -1;
                    int destIdx = -1;

                    for (int i = 0; i < stations.size(); i++) {
                        String station = stations.get(i);
                        if (station.equalsIgnoreCase(src) && srcIdx == -1) {
                            srcIdx = i;
                        }
                        if (station.equalsIgnoreCase(dest) && destIdx == -1) {
                            destIdx = i;
                        }
                    }

                    return srcIdx != -1 && destIdx != -1 && srcIdx < destIdx;
                })
                .collect(Collectors.toList());
    }

}
