package com.lkkp.runwith.IntervalRank.controller;

import com.lkkp.runwith.IntervalRank.Km1;
import com.lkkp.runwith.IntervalRank.Km3;
import com.lkkp.runwith.IntervalRank.Km5;
import com.lkkp.runwith.IntervalRank.repository.Km1Repository;
import com.lkkp.runwith.IntervalRank.repository.Km3Repository;
import com.lkkp.runwith.IntervalRank.repository.Km5Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/distance")
public class IntervalRankController {
    private Km1Repository km1Repository;
    private Km3Repository km3Repository;
    private Km5Repository km5Repository;

    @GetMapping("/km1")
    public List<Km1> getKm1Info(@RequestParam int user_id) {
        return km1Repository.findAll();
    }

    @GetMapping("/km3")
    public List<Km3> getKm3Info(@RequestParam int user_id) {
        return km3Repository.findAll();
    }

    @GetMapping("/km5")
    public List<Km5> getKm5Info(@RequestParam int user_id) {
        return km5Repository.findAll();
    }
}
