package com.example.quizz_biologies.controller;

import com.example.quizz_biologies.entity.TestEntity;
import com.example.quizz_biologies.entity.TestQuestion;
import com.example.quizz_biologies.model.AppUser;
import com.example.quizz_biologies.repository.TestQuestionRepository;
import com.example.quizz_biologies.repository.TestRepository;
import com.example.quizz_biologies.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/tests")
@PreAuthorize("hasRole('TEACHER')")
public class TestController {

    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final TestQuestionRepository testQuestionRepository;

    public TestController(TestRepository testRepository, UserRepository userRepository, TestQuestionRepository testQuestionRepository) {
        this.testRepository = testRepository;
        this.userRepository = userRepository;
        this.testQuestionRepository = testQuestionRepository;
    }

    @GetMapping
    public String listTests(Authentication authentication, Model model) {
        AppUser current = userRepository.findByUsername(authentication.getName()).orElse(null);
        Long teacherId = current != null ? current.getId() : null;
        List<TestEntity> tests = teacherId == null
                ? List.of()
                : testRepository.findByTeacherId(teacherId);
        // pentru fiecare test, încărcăm întrebările aferente
        model.addAttribute("questionsByTest", tests.stream()
                .collect(java.util.stream.Collectors.toMap(TestEntity::getId,
                        t -> testQuestionRepository.findByTestId(t.getId()))));
        model.addAttribute("tests", tests);
        return "tests";
    }

    @PostMapping("/create")
    public String createTest(@RequestParam String name,
                             @RequestParam(required = false) String description,
                             Authentication authentication) {
        AppUser current = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (current == null) {
            return "redirect:/login";
        }
        TestEntity test = new TestEntity();
        test.setName(name);
        test.setDescription(description);
        test.setTeacherId(current.getId());
        testRepository.save(test);
        return "redirect:/tests";
    }

    @PostMapping("/{testId}/questions")
    public String addQuestion(@PathVariable Long testId,
                              @RequestParam String text,
                              @RequestParam String optionA,
                              @RequestParam String optionB,
                              @RequestParam String optionC,
                              @RequestParam String optionD,
                              @RequestParam Integer correctIndex,
                              Authentication authentication) {
        AppUser current = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (current == null) {
            return "redirect:/login";
        }
        var test = testRepository.findById(testId).orElse(null);
        if (test == null || !test.getTeacherId().equals(current.getId())) {
            return "redirect:/tests";
        }

        TestQuestion q = new TestQuestion();
        q.setTestId(testId);
        q.setText(text);
        q.setOptionA(optionA);
        q.setOptionB(optionB);
        q.setOptionC(optionC);
        q.setOptionD(optionD);
        q.setCorrectIndex(correctIndex);
        testQuestionRepository.save(q);
        return "redirect:/tests";
    }
}
