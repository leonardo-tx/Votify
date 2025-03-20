package br.com.votify.core.pollTemp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/polls")
public class PollController {

    @Autowired
    private PollService pollService;

    @GetMapping("/{id}")
    public PollResponseDto getPollById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId
    ) {
        return pollService.findSpecificPoll(id, userId);
    }
}