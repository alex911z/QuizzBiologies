package com.example.quizz_biologies;

import com.example.quizz_biologies.entity.TestEntity;
import com.example.quizz_biologies.model.AppUser;
import com.example.quizz_biologies.model.QuizResult;
import com.example.quizz_biologies.model.Role;
import com.example.quizz_biologies.repository.QuizResultRepository;
import com.example.quizz_biologies.repository.TestQuestionRepository;
import com.example.quizz_biologies.repository.TestRepository;
import com.example.quizz_biologies.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class DashboardController {

    private final QuizResultRepository quizResultRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final TestQuestionRepository testQuestionRepository;

    public DashboardController(QuizResultRepository quizResultRepository,
                               UserRepository userRepository,
                               TestRepository testRepository,
                               TestQuestionRepository testQuestionRepository) {
        this.quizResultRepository = quizResultRepository;
        this.userRepository = userRepository;
        this.testRepository = testRepository;
        this.testQuestionRepository = testQuestionRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            AppUser user = userRepository.findByUsername(username).orElse(null);
            List<QuizResult> results = quizResultRepository.findTop10ByUsernameOrderByCreatedAtDesc(username);
            int total = results.size();
            double avg = results.stream().mapToInt(QuizResult::getScore).average().orElse(0.0);

            model.addAttribute("results", results);
            model.addAttribute("resultsCount", total);
            model.addAttribute("avgScore", Math.round(avg * 10.0) / 10.0);
            model.addAttribute("role", user != null ? user.getRole().name() : "USER");

            if (!results.isEmpty()) {
                QuizResult last = results.get(0);
                model.addAttribute("lastTestName", last.getTestName());
                model.addAttribute("lastScore", last.getScore());
                model.addAttribute("lastDate", last.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM HH:mm")));
            }

            if (user != null && user.getRole() == Role.TEACHER) {
                List<TestEntity> tests = testRepository.findByTeacherId(user.getId());
                int testsCount = tests.size();
                int totalQuestions = tests.stream()
                        .mapToInt(t -> testQuestionRepository.findByTestId(t.getId()).size())
                        .sum();
                model.addAttribute("teacherTestsCount", testsCount);
                model.addAttribute("teacherQuestionsCount", totalQuestions);
            }
        }
        return "dashboard";
    }
}
