package com.example.quizz_biologies.controller;

import com.example.quizz_biologies.entity.TestEntity;
import com.example.quizz_biologies.entity.TestQuestion;
import com.example.quizz_biologies.model.QuizResult;
import com.example.quizz_biologies.repository.QuizResultRepository;
import com.example.quizz_biologies.repository.TestQuestionRepository;
import com.example.quizz_biologies.repository.TestRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tests")
public class StudentTestController {

    private final TestRepository testRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final QuizResultRepository quizResultRepository;

    public StudentTestController(TestRepository testRepository,
                                 TestQuestionRepository testQuestionRepository,
                                 QuizResultRepository quizResultRepository) {
        this.testRepository = testRepository;
        this.testQuestionRepository = testQuestionRepository;
        this.quizResultRepository = quizResultRepository;
    }

    @GetMapping("/available")
    public String listAvailableTests(Model model) {
        List<TestEntity> tests = testRepository.findAll();
        model.addAttribute("tests", tests);
        return "tests-available";
    }

    @GetMapping("/{testId}/take")
    public String takeTest(@PathVariable Long testId, Model model) {
        TestEntity test = testRepository.findById(testId).orElse(null);
        if (test == null) {
            return "redirect:/tests/available";
        }
        List<TestQuestion> questions = testQuestionRepository.findByTestId(testId);
        model.addAttribute("test", test);
        model.addAttribute("questions", questions);
        return "test-take";
    }

    @PostMapping("/{testId}/take")
    public String submitTest(@PathVariable Long testId,
                             @RequestParam Map<String, String> form,
                             Authentication authentication,
                             Model model) {
        TestEntity test = testRepository.findById(testId).orElse(null);
        if (test == null) {
            return "redirect:/tests/available";
        }
        List<TestQuestion> questions = testQuestionRepository.findByTestId(testId);
        int total = questions.size();
        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            String key = "q" + questions.get(i).getId();
            String answerIdx = form.get(key);
            if (answerIdx != null) {
                try {
                    int idx = Integer.parseInt(answerIdx);
                    if (idx == questions.get(i).getCorrectIndex()) {
                        correct++;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        int grade = total == 0 ? 1 : (int) Math.round((correct / (double) total) * 9 + 1);

        if (authentication != null) {
            QuizResult result = new QuizResult();
            result.setUsername(authentication.getName());
            result.setTestName(test.getName());
            result.setScore(grade);
            quizResultRepository.save(result);
        }

        model.addAttribute("test", test);
        model.addAttribute("questions", questions);
        model.addAttribute("submitted", true);
        model.addAttribute("correct", correct);
        model.addAttribute("total", total);
        model.addAttribute("grade", grade);
        return "test-take";
    }
}
